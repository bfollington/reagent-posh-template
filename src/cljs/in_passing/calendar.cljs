(ns in-passing.calendar
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.test :refer [deftest is]]
            [clojure.string :as string]
            [in-passing.util :as u]
            [in-passing.ui :as ui]
            [herb.core :refer [<class]]
            [re-com.core :refer [h-box v-box]]
            [in-passing.days :as d]
            [in-passing.ui.days :as dui]
            [in-passing.color :as col]
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

(defn calendar []
  (let [mpos (atom [0 0])
        letters (atom (grid 8 8))
        days (d/gen-month 31)
        today (atom 3)]
    (fn []
      (let [[mx my] @mpos]
        [:div
         [:button {:on-click (fn [e] (swap! today inc))} "Next Day"]
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
                                        [:td [dui/day d {:state (if (= d @today) :today :default)}]])
                                      wk))])
                       days))]]]))))