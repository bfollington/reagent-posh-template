(ns roam-7guis.counter
  (:require [reagent.core :as reagent :refer [atom]]
            [roam-7guis.ui :as ui]
            [re-com.core :refer [h-box]]))

(defn counter []
  (let [clicks (atom 0)
        on-clicked #(swap! clicks inc)]
    (fn []
      [h-box
       :gap "8px"
       :align :center
       :children [[ui/label @clicks]
                  [ui/button {:on-click on-clicked
                              :label "Count"}]]])))