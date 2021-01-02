(ns in-passing.cards
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.dom :as rdom]
            [in-passing.core :as core]
            [devcards.core :as dc]
            [in-passing.calendar :as calendar])
  (:require-macros
   [devcards.core
    :as dc
    :refer [defcard defcard-doc defcard-rg deftest]]))

(defcard-rg calendar
  (calendar/calendar))

(rdom/render [:div] (.getElementById js/document "app"))

;; remember to run 'lein figwheel devcards' and then browse to
;; http://localhost:3449/cards
