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

;;

(def empty-cell {:content [:value "0"] :cache "0" :depends-on []})
(def alpha "ABCDEFGHIJKLMNOPQRSTUVWXYZ")

(defn coords->id [[x y]]
  (str (get alpha x) y))

(defn id->coords [id]
  (let [letter (string/replace id #"\d+" "")
        num (string/replace id #"[A-Z]+" "")]
    [(.indexOf alpha letter) (js/parseInt num)]))

;;

(defn initial-state [] {:cells [[(atom empty-cell) (atom empty-cell) (atom empty-cell)]
                                [(atom empty-cell) (atom empty-cell) (atom empty-cell)]
                                [(atom empty-cell) (atom empty-cell) (atom empty-cell)]]})

(defn get-cell [matrix [x y]]
  (get (get matrix y) x))

(defn get-cell-id [matrix id]
  (let [coords (id->coords id)]
    (get-cell matrix coords)))

(defn get-cell-content-id [matrix id]
  (:content @(get-cell-id matrix id)))

(defn get-cell-cache-id [matrix id]
  (:cache @(get-cell-id matrix id)))

(defn get-cell-deps-id [matrix id]
  (:depends-on @(get-cell-id matrix id)))

(defn render-cell-id [matrix id]
  (log ["rendering" id])
  (let [[type contents] (get-cell-content-id matrix id)]
    (case type
      :value contents
      :formula (parser/evaluate-formula contents (partial get-cell-cache-id matrix)))))

(defn parse-contents [formula]
  (cond
    (= (first formula) "=") [:formula (subs formula 1)]
    :else [:value formula]))

;;

(defn recalc-cell-id! [matrix id]
  (let [update-cache! (fn [cell] (swap! cell #(assoc % :cache (render-cell-id matrix id))))]
    (update-in matrix (reverse (id->coords id)) update-cache!)))

(defn watch-cell [matrix target-id watcher-id]
  (log ["started watching" target-id "from" watcher-id])
  (add-watch
   (get-cell matrix (id->coords target-id))
   watcher-id
   (fn [_ _ _ _]
     (log ["change in" target-id "updating" watcher-id])
     (recalc-cell-id! matrix watcher-id))))

(defn update-cell-id! [matrix id value]
  (log ["updating" id (id->coords id) value])
  (let [formula (parse-contents value)
        update-content! (fn [cell] (swap! cell #(assoc % :content formula)))
        update-cache! (fn [cell] (swap! cell #(assoc % :cache (render-cell-id matrix id))))
        [formula-type body] formula
        deps (if (= formula-type :formula) (parser/evaluate-deps body) [])
        old-deps (get-cell-deps-id matrix id)
        new-deps deps
        update-deps! (fn [cell] (swap! cell #(assoc % :depends-on deps)))]
    (update-in matrix (reverse (id->coords id)) update-content!)
    (update-in matrix (reverse (id->coords id)) update-cache!)

    (doseq [d old-deps]
      (remove-watch (get-cell-id matrix d) id))

    (doseq [d new-deps]
      (watch-cell matrix d id))

    (update-in matrix (reverse (id->coords id)) update-deps!)))

;;

(defn format-contents [cell]
  (let [contents (:content cell)
        [type content] contents]
    (case type
      :value (str content)
      :formula (str "=" content))))

(defn cell [id state]
  (let [contents (get-cell-id (:cells state) id)
        editing (atom false)
        form (atom (format-contents @contents))]
    (fn []
      [:div {:style {:width "64px"}}
       (if @editing
         [:div
          [:input {:style {:width "48px"} :type "text" :value @form :on-change #(reset! form (-> % .-target .-value))}]
          [:button {:on-click (fn [_] (update-cell-id! (:cells state) id @form) (reset! editing false))} "Save"]]
         [:div {:on-click (fn [_] (reset! editing true))} (str (render-cell-id (:cells state) id))])
      ;;  [:div (str (:depends-on @contents))]
       ])))

(defn border-style []
  {:border "1px solid #ccc"})

(defn spreadsheet []
  (let [state (initial-state)]
    (fn []
      [:div
       [:table
        {:class (<class border-style)}
        [:thead
         [:tr
          [:td {:class (<class border-style)} ""]
          [:td {:class (<class border-style)} "A"]
          [:td {:class (<class border-style)} "B"]
          [:td {:class (<class border-style)} "C"]]]
        [:tbody
         [:tr
          [:td {:class (<class border-style)} "0"]
          [:td {:class (<class border-style)} [cell "A0" state]]
          [:td {:class (<class border-style)} [cell "B0" state]]
          [:td {:class (<class border-style)} [cell "C0" state]]]
         [:tr
          [:td {:class (<class border-style)} "1"]
          [:td {:class (<class border-style)} [cell "A1" state]]
          [:td {:class (<class border-style)} [cell "B1" state]]
          [:td {:class (<class border-style)} [cell "C1" state]]]
         [:tr
          [:td {:class (<class border-style)} "2"]
          [:td {:class (<class border-style)} [cell "A2" state]]
          [:td {:class (<class border-style)} [cell "B2" state]]
          [:td {:class (<class border-style)} [cell "C2" state]]]]]])))