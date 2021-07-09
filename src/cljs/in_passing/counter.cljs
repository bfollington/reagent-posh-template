(ns in-passing.counter
  (:require [in-passing.state :as db]
            [in-passing.query :as q]
            [in-passing.ui :as ui]
            [goog.string.format]))

(defn increment! [counter-id curr]
  (db/add! counter-id {:counter/value (inc curr)}))

(defn decrement! [counter-id curr]
  (db/add! counter-id {:counter/value (dec curr)}))

(defn counter [name]
  (let [select (partial db/select db/conn)

        counter-id (select q/->counter-by-name name)
        counter (select q/->counter-value-by-name name)]
    [:div
     [ui/label (str name " = " counter)]
     [:span " "]
     [ui/button {:on-click (fn [_e] (increment! counter-id counter)) :label "+"}]
     [ui/button {:on-click (fn [_e] (decrement! counter-id counter)) :label "-"}]]))