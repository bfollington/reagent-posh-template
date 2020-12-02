(ns roam-7guis.counter
  (:require [reagent.core :as reagent :refer [atom]]
            [re-com.core :refer [h-box]]))

(defn counter []
  (let [clicks (atom 0)
        on-clicked #(swap! clicks inc)]
    (fn []
      [h-box
       :gap "8px"
       :children [[:label @clicks]
                  [:button {:on-click on-clicked} "Count"]]])))