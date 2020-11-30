(ns roam-7guis.cards
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.dom :as rdom]
            [roam-7guis.core :as core]
            [devcards.core :as dc]
            [roam-7guis.tempconv :as temp]
            [roam-7guis.flight-booker :as flight])
  (:require-macros
   [devcards.core
    :as dc
    :refer [defcard defcard-doc defcard-rg deftest]]))

(defcard-rg first-card
  [:div>h1 "This is your first devcard!"])

(defcard-rg counter
  (let [clicks (atom 0)
        on-clicked #(swap! clicks inc)]
    (fn []
      [:div
       [:label @clicks]
       [:button {:on-click on-clicked} "Count"]])))

(defcard-rg temp-conv
  (temp/temp-conv))

(defcard-rg flight-booker
  (flight/flight-booker))

(defcard-rg home-page-card
  [core/home-page])

(rdom/render [:div] (.getElementById js/document "app"))

;; remember to run 'lein figwheel devcards' and then browse to
;; http://localhost:3449/cards
