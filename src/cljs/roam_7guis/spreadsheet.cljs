(ns roam-7guis.spreadsheet
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.pprint :as pp]
            [clojure.string :as string]
            [herb.core :refer [<class]]
            [roam-7guis.parser :as parser]))

(defn log [& args]
  (doseq [arg args]
    (pp/pprint arg)))

(defn set-state! [state key value]
  ;; (log @state key value)
  (swap! state #(assoc % key value)))

;; util

(def empty-cell {:content [:value "0"] :cache "0" :depends-on []})
(def alpha "ABCDEFGHIJKLMNOPQRSTUVWXYZ")

(defn coords->id [[x y]]
  (str (get alpha x) y))

(defn id->coords [id]
  (let [letter (string/replace id #"\d+" "")
        num (string/replace id #"[A-Z]+" "")]
    [(.indexOf alpha letter) (js/parseInt num)]))

(defn generate-cells [rows cols]
  (let [gen-cols (fn [_] (into [] (map (fn [_] (atom empty-cell)) (range cols))))
        cells (into [] (map gen-cols (range rows)))]
    cells))

(defn rowcount [cells]
  (count cells))

(defn colcount [cells]
  (count (get cells 0)))

;; state + selectors

(defn initial-state [] {:cells (generate-cells 26 26)})

(defn get-cell [matrix [x y]]
  (get (get matrix y) x))

(defn get-cell-id [matrix id]
  (let [coords (id->coords id)]
    (get-cell matrix coords)))

(defn get-cell-field [matrix k id]
  (k @(get-cell-id matrix id)))

;; domain

(defn render-cell-id [matrix id]
  ;; (log ["rendering" id])
  (let [[type contents] (get-cell-field matrix :content id)]
    (case type
      :value contents
      :formula (parser/evaluate-formula contents (partial get-cell-field matrix :cache)))))

(defn parse-contents [formula]
  (cond
    (= (first formula) "=") [:formula (subs formula 1)]
    :else [:value formula]))

;; mutations

(defn update-cell-field! [cell k v] (swap! cell #(assoc % k v)))

(defn recalc-cell-id! [matrix id]
  (let [update-cache! (fn [cell] (update-cell-field! cell :cache (render-cell-id matrix id)))]
    (update-in matrix (reverse (id->coords id)) update-cache!)))

(defn watch-cell [matrix target-id watcher-id]
  ;; (log ["started watching" target-id "from" watcher-id])
  (add-watch
   (get-cell matrix (id->coords target-id))
   watcher-id
   (fn [_ _ _ _]
     (log ["change in" target-id "updating" watcher-id])
     (recalc-cell-id! matrix watcher-id))))

(defn update-cell-id! [matrix id value]
  ;; (log ["updating" id (id->coords id) value])
  (let [formula (parse-contents value)
        [formula-type body] formula

        deps (if (= formula-type :formula) (parser/evaluate-deps body) [])
        old-deps (get-cell-field matrix :depends-on id)
        new-deps deps

        update-content! (fn [cell] (update-cell-field! cell :content formula))
        update-cache! (fn [cell] (update-cell-field! cell :cache (render-cell-id matrix id)))
        update-deps! (fn [cell] (update-cell-field! cell :depends-on deps))]

    (doseq [d old-deps]
      (remove-watch (get-cell-id matrix d) id))

    (doseq [d new-deps]
      (watch-cell matrix d id))

    (doseq [perform! [update-content! update-cache! update-deps!]]
      (update-in matrix (reverse (id->coords id)) perform!))))

;; view

(defn format-contents [cell]
  (let [contents (:content cell)
        [type content] contents]
    (case type
      :value (str content)
      :formula (str "=" content))))

(defn on-key-pressed [state form editing id event]
  (let [key (.-key event)]
    (case key
      "Enter" (do
                (update-cell-id! (:cells state) id @form)
                (reset! editing false))
      ())))

(defn cell [id state]
  (let [contents (get-cell-id (:cells state) id)
        editing (atom false)
        form (atom (format-contents @contents))]
    (fn []
      [:div {:style {:width "64px"}}
       (if @editing
         [:div
          [:input {:style {:width "48px"}
                   :type "text" :value @form
                   :on-change #(reset! form (-> % .-target .-value))
                   :on-key-down #(on-key-pressed state form editing id %)}]]
         [:div
          {:on-click (fn [_] (reset! editing true))}
          (str (render-cell-id (:cells state) id))])])))

(defn border-style []
  {:border "1px solid #ccc"})

(defn spreadsheet []
  (let [state (initial-state)]
    (fn []
      [:div
       [:p "Click a cell to edit, hit enter to save changes. Formula's take the form of: =[:add [\"A0\" \"A1\"]]"]
       [:table
        {:class (<class border-style)}
        [:thead
         [:tr
          [:td {:class (<class border-style)} ""]
          (map (fn [a]
                 ^{:key a}
                 [:td {:class (<class border-style)} a])
               (subs alpha 0 (colcount (:cells state))))]]
        [:tbody
         (map-indexed (fn [irow row]
                        ^{:key irow}
                        [:tr
                         [:td {:class (<class border-style)} irow]
                         (map (fn [icol]
                                ^{:key icol}
                                [:td
                                 {:class (<class border-style)}
                                 [cell (coords->id [icol irow]) state]])
                              (range (count row)))])
                      (:cells state))]]])))