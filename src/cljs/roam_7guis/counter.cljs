(ns roam-7guis.counter
  (:require [reagent.core :as reagent :refer [atom]]))

(defn counter []
  (let [clicks (atom 0)
        on-clicked #(swap! clicks inc)]
    (fn []
      [:div
       [:label @clicks]
       [:button {:on-click on-clicked} "Count"]])))