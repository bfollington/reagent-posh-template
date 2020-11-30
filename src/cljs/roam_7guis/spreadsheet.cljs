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

(defn initial-state [] {:cells [[(atom [:value "1"]) (atom [:value "2"])]
                                [(atom [:formula "[:add [[:add [\"A0\" \"B0\"]] \"B1\"]]"]) (atom [:value "4"])]]
                        :references [[(atom []) (atom [])]
                                     [(atom []) (atom [])]]})
(def alpha "ABCDEFGHIJKLMNOPQRSTUVWXYZ")

(defn coords-to-id [[x y]]
  (str (get alpha x) y))

(defn id-to-coords [id]
  (let [letter (string/replace id #"\d+" "")
        num (string/replace id #"[A-Z]+" "")]
    [(.indexOf alpha letter) (js/parseInt num)]))

(defn get-cell [matrix [x y]]
  (get (get matrix y) x))

(defn get-cell-id [matrix id]
  (let [coords (id-to-coords id)]
    (get-cell matrix coords)))

(defn evaluate-formula [formula get-cell-value]
  (parser/evaluate-formula formula get-cell-value))

(defn render-cell-id [matrix id]
  (log ["rendering" id])
  (let [[type contents] @(get-cell-id matrix id)]
    (case type
      :value contents
      :formula (evaluate-formula contents (partial render-cell-id matrix)))))

(defn parse-formula [formula]
  (cond
    (= (first formula) "=") [:formula formula]
    :else [:value formula]))

(defn update-cell-id! [matrix id value]
  (update-in matrix (id-to-coords id) #(reset! % (parse-formula value))))

(defn watch-cell [matrix target-id watcher-id watcher]
  (add-watch (get-cell matrix (id-to-coords target-id)) watcher-id watcher))

;;


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
       [:div "A0=" (str @(get-cell-id (:cells state) "A0"))]
       [:div "A1=" (str @(get-cell-id (:cells state) "A1")) "=" (str (render-cell-id (:cells state) "A1"))]
       [:div "B0=" (str @(get-cell-id (:cells state) "B0"))]
       [:div "B1=" (str @(get-cell-id (:cells state) "B1"))]
       [:button {:on-click #(update-cell-id! (:cells state) "B1" "10")} "Test"]])))