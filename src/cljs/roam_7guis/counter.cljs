(ns roam-7guis.counter
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.dom :as rdom]
            [roam-7guis.core :as core]
            [devcards.core :as dc])
  (:require-macros
   [devcards.core
    :as dc
    :refer [defcard defcard-doc defcard-rg deftest]]))

(defn counter []
  (let [clicks (atom 0)
        on-clicked #(swap! clicks inc)]
    (fn []
      [:div
       [:label @clicks]
       [:button {:on-click on-clicked} "Count"]])))