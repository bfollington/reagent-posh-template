(ns roam-7guis.spreadsheet
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.pprint :as pp]
            [clojure.string :as string]
            [herb.core :refer [<class]]
            [roam-7guis.parser :as parser]
            [re-com.core :refer [h-box v-box]]))

(defn log [& args]
  (doseq [arg args]
    (pp/pprint arg)))

(defn set-state! [state key value]
  ;; (log @state key value)
  (swap! state #(assoc % key value)))

;;

(defn initial-state [] {:cells [[(atom {:content [:value "1"] :cache "1"}) (atom {:content [:value "2"] :cache "2"}) (atom {:content [:value "0"] :cache "0"})]
                                [(atom {:content [:formula "[:add [[:add [\"A0\" \"B0\"]] \"B1\"]]"] :cache "7"}) (atom {:content [:value "4"] :cache "4"}) (atom {:content [:value "0"] :cache "0"})]
                                [(atom {:content [:value "0"] :cache "0"}) (atom {:content [:value "0"] :cache "0"}) (atom {:content [:value "0"] :cache "0"})]]
                        :references [[(atom []) (atom [])]
                                     [(atom []) (atom [])]]})
(def alpha "ABCDEFGHIJKLMNOPQRSTUVWXYZ")

(defn coords->id [[x y]]
  (str (get alpha x) y))

(defn id->coords [id]
  (let [letter (string/replace id #"\d+" "")
        num (string/replace id #"[A-Z]+" "")]
    [(.indexOf alpha letter) (js/parseInt num)]))

(defn get-cell [matrix [x y]]
  (get (get matrix y) x))

(defn get-cell-id [matrix id]
  (let [coords (id->coords id)]
    (get-cell matrix coords)))

(defn get-cell-content-id [matrix id]
  (:content @(get-cell-id matrix id)))

(defn get-cell-cache-id [matrix id]
  (:cache @(get-cell-id matrix id)))

(defn render-cell-id [matrix id]
  (log ["rendering" id])
  (let [[type contents] (get-cell-content-id matrix id)]
    (case type
      :value contents
      :formula (parser/evaluate-formula contents (partial get-cell-cache-id matrix)))))

(defn parse-formula [formula]
  (cond
    (= (first formula) "=") [:formula (subs formula 1)]
    :else [:value formula]))

(defn update-cell-id! [matrix id value]
  (log ["updating" id (id->coords id) value])
  (let [update-content! (fn [cell] (swap! cell #(assoc % :content (parse-formula value))))
        update-cache! (fn [cell] (swap! cell #(assoc % :cache (render-cell-id matrix id))))]
    (update-in matrix (reverse (id->coords id)) update-content!)
    (update-in matrix (reverse (id->coords id)) update-cache!)))

(defn watch-cell [matrix target-id watcher-id watcher]
  (add-watch (get-cell matrix (id->coords target-id)) watcher-id watcher))

;;

(defn format-contents [cell]
  (let [contents (:content cell)
        [type content] contents]
    (case type
      :value (str content)
      :formula (str "=" content))))

(defn cell [id state]
  (let [contents (get-cell-id (:cells state) id)
        form (atom (format-contents @contents))]
    (fn []
      [:div
       [:input {:type "text" :value @form :on-change #(reset! form (-> % .-target .-value))}]
       [:button {:on-click (fn [_] (update-cell-id! (:cells state) id @form))} "Save"]
       [:div  "=" (str (render-cell-id (:cells state) id))]])))

(defn border-style []
  {:border "1px solid #ccc"})

(defn spreadsheet []
  (let [state (initial-state)]
    ;; (watch-cell (:cells state) "B1" "A0"
    ;;             (fn [key _ _ new]
    ;;               (update-cell-id! (:cells state) key (dec new))))
    ;; (watch-cell (:cells state) "A0" "B0"
    ;;             (fn [key _ _ new]
    ;;               (update-cell-id! (:cells state) key (dec new))))
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