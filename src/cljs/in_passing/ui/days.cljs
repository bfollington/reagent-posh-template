(ns in-passing.ui.days
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.test :refer [deftest is]]
            [clojure.string :as string]
            [in-passing.util :as u]
            [in-passing.ui :as ui]
            [herb.core :refer [<class]]
            [re-com.core :refer [h-box v-box]]
            [in-passing.days :as d]
            [in-passing.color :as col]
            [goog.string :as gstring]
            [goog.string.format]))

(defn day-css []
  ^{:pseudo {:hover {:background "rgba(0, 0, 0, 0.1)"}}}
  {:position "relative"
   :width "96px"
   :height "96px"
   :border "1px solid black"
   :padding "4px"
   :transition "background 0.2s"})

(defn day-number-css []
  {:position "absolute"
   :font-family "Indie Flower"
   :font-size "24px"
   :right "8px"
   :bottom 0})

(defn day-circle-css []
  {:position "absolute"
   :width "32px"
   :right "-8px"
   :bottom "-3px"})

(defn event-css [piece]
  ^{:pseudo {:hover {:cursor "pointer"
                     :border "2px solid white"}}}
  {:color "white"
   :font-family "Indie Flower"
   :font-size "14px"
   :line-height "13px"
   :background (col/piece->hex piece)
   :border (str "2px solid" (col/piece->hex piece))
   :transition "border 0.2s"
   :border-radius "5px"
   :padding "4px"})

(defn event [piece label]
  [:div {:class (<class event-css piece)} label])

(defn day [date {:keys [state]}]
  [:div
   {:class (<class day-css)}
   [v-box
    :gap "2px"
    :children [[event :king "Appt/ Dr. King"]
               [event :pawn "Work"]]]
   [:div {:class (<class day-number-css)}
    (if (= state :today)
      [:img {:class (<class day-circle-css)
             :src "/assets/scribble.svg"}])
    date]])

