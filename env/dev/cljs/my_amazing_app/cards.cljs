(ns my-amazing-app.cards
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.dom :as rdom]
            [my-amazing-app.core :as core]
            [devcards.core :as dc]
            [my-amazing-app.counter :as counter])
  (:require-macros
   [devcards.core
    :as dc
    :refer [defcard defcard-doc defcard-rg deftest]]))

(defcard-rg counter
  (counter/counter))

(rdom/render [:div] (.getElementById js/document "app"))

;; remember to run 'lein figwheel devcards' and then browse to
;; http://localhost:3449/cards
