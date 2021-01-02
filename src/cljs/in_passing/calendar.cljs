(ns in-passing.calendar
  (:require [reagent.core :as reagent :refer [atom]]
            [herb.core :refer [<class]]
            [re-com.core :refer [h-box v-box]]
            [in-passing.days :as d]
            [in-passing.ui.days :as dui]
            [in-passing.util :as u]
            [goog.string :as gstring]
            [goog.string.format]))

(defn grid-cells [w h]
  (mapcat (fn [r] (map (fn [c] [c r])
                       (range w)))
          (range h)))

(def empty-cell "_")

(defn grid [w h]
  (let [gen-cols (fn [_] (into [] (map (fn [_] empty-cell) (range h))))
        cells (into [] (map gen-cols (range w)))]
    cells))

(defn grid->cell [matrix [x y]]
  (get (get matrix y) x))

(def grid-size 8)
(def margin 8)

(defn coord [k] (+ (* grid-size k) margin))

(defn map-range
  "map between two ranges, i.e. mapping 0.5 from [0 1] to [-1 1] gives 0."
  [x [in-min in-max] [out-min out-max]]
  (+ (/ (* (- x in-min) (- out-max out-min)) (- in-max in-min)) out-min))

(defn relative-pos [e]
  [(- (.-clientX e) (-> e .-target .getBoundingClientRect .-left))
   (- (.-clientY e) (-> e .-target .getBoundingClientRect .-top))])

(defn text-css []
  {:font-size "4px"})

(defn calc-day-state [d today]
  (cond
    (< d today) :past
    (= d today) :today
    (> d today) :default))

(defn get-active-piece [d events]
  (when (some? d)
    (let [day-events (get events d)
          active (->> day-events
                      (filter (fn [[_ _ s]] (= s :active)))
                      (first))]
      active)))

(defn in?
  "true if coll contains elm"
  [coll elm]
  (some #(= elm %) coll))

(defn calendar []
  (let [mpos (atom [0 0])
        days (d/gen-month 31)
        events (atom {7 [[:king "Appt/ Dr. King" :active]
                         [:pawn "Work" :taken]]
                      9 [[:pawn "Work" :active]]
                      16 [[:pawn "Work" :active]]
                      23 [[:pawn "Work" :active]]})
        selected (atom nil)
        today (atom 3)

        on-selected (fn [d _] (u/log d "test") (reset! selected d))]
    (fn []
      (let [[mx my] @mpos
            active-piece (get-active-piece @selected @events)
            possible-moves [6 13 14]]
        [:div
         [:button {:on-click (fn [e] (swap! today inc))} "Next Day"]
         [:div (str @selected) (str active-piece)]
         [:table
          [:thead
          ;;  [:tr
          ;;   [:td "M"]
          ;;   [:td "T"]
          ;;   [:td "W"]
          ;;   [:td "T"]
          ;;   [:td "F"]
          ;;   [:td "S"]
            ;; [:td "S"]]
           ]
          [:tbody
           (doall (map (fn [wk]
                         ^{:key wk}
                         [:tr
                          (doall (map (fn [d]
                                        ^{:key d}
                                        [:td [dui/day d {:state (calc-day-state d @today)
                                                         :on-selected (partial on-selected d)
                                                         :preview-piece (if (in? possible-moves d) active-piece nil)
                                                         :events (get @events d)}]])
                                      wk))])
                       days))]]]))))