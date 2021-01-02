(ns in-passing.ui.days
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.test :refer [deftest is]]
            [herb.core :refer [<class]]
            [re-com.core :refer [h-box v-box]]
            [in-passing.color :as col]))

(defn day-css []
  ^{:pseudo {:hover {:background "rgba(0, 0, 0, 0.1)"}}}
  {:position "relative"
   :width "96px"
   :height "96px"
   :border (str "1px solid" col/ui-color)
   :padding "4px"
   :transition "background 0.2s"})

(defn day-number-css []
  {:position "absolute"
   :font-family "Indie Flower"
   :font-size "24px"
   :color col/ui-color
   :right "8px"
   :bottom 0})

(defn day-passed-cross-css []
  {:position "absolute"
   :font-family "Indie Flower"
   :font-size "100px"
   :left "25px"
   :top "35px"
   :color col/ui-color
   :pointer-events "none"})

(defn day-circle-css []
  {:position "absolute"
   :width "32px"
   :right "-8px"
   :bottom "-3px"})

(defn event-css [piece state]
  (let [taken (= state :taken)
        col (if taken (col/piece->hex piece) "white")
        bg (if taken "white" (col/piece->hex piece))]

    ^{:pseudo {:hover {:cursor "pointer"
                       :border "2px solid white"}}}
    {:font-family "Indie Flower"
     :font-size "14px"
     :line-height "13px"
     :text-decoration (if (= state :taken) "line-through" "")
     :color col
     :background bg
     :border (str "2px solid " (if taken "white" bg))
     :transition "border 0.2s"
     :border-radius "5px"
     :padding "4px"
     :opacity (if (= state :preview) "0.5" "1")}))

(defn event [piece label state]
  [:div {:class (<class event-css piece state)} label])

;; state = :default :past :today :disabled
(defn day [date {:keys [state events preview-piece on-selected]}]
  (let [grouped-events (group-by (fn [[_ _ s]] s) events)
        taken (:taken grouped-events)
        active (:active grouped-events)
        ordered-events (concat taken active)]
    [:div
     {:class (<class day-css)
      :on-click on-selected}

     (when (= state :past)
       [:div {:class (<class day-passed-cross-css)} "X"])

     (when (some? events)
       [v-box
        :gap "2px"
        :children (map (fn [[p txt state]] [event p txt state]) ordered-events)])

     (when (some? preview-piece)
       (let [[p txt s] preview-piece]
         [event p txt :preview]))

     [:div {:class (<class day-number-css)}
      (when (= state :today)
        [:img {:class (<class day-circle-css)
               :src "/assets/scribble.svg"}])
      date]]))

