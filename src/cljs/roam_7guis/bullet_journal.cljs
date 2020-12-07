(ns roam-7guis.bullet-journal
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.string :as string]
            [roam-7guis.util :as u]
            [roam-7guis.ui :as ui]
            [herb.core :refer [<class]]
            [re-com.core :refer [h-box v-box]]
            [goog.string :as gstring]
            [goog.string.format]))

;; credit https://github.com/gfredericks/svg-wrangler/blob/master/src/com/gfredericks/svg_wrangler.clj
(defn svg*
  [[minx miny user-width user-height :as dims] width height attrs contents]
  [:svg (merge attrs
               {:xmlns "http://www.w3.org/2000/svg" :version "1.1"
                :viewBox (apply gstring/format "%f %f %f %f" (map double dims))
                :width (gstring/format "%dpx" width)
                :height (gstring/format "%dpx" height)})
   contents])

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

(defn journal []
  (let [mpos (atom [0 0])
        letters (atom (grid 8 8))]
    (fn []
      (let [[mx my] @mpos]
        [:div
         [:div mx my]
         [svg* [0 0 100 100] 420 420
          {:on-mouse-move (fn [e] (reset! mpos (relative-pos e)))}
          [:g
           [:line {:x1 (coord 1)
                   :y1 (coord 2)
                   :x2 (map-range mx [0 420] [0 100])
                   :y2 (map-range my [0 420] [0 100])
                   :stroke "black"}]
           (doall (map (fn [[x y]] ^{:key [x y]} [:g
                                                  [:text {:x (+ (/ grid-size 3) (coord x))
                                                          :y (+ (* 2 (/ grid-size 3)) (coord y))
                                                          :class (<class text-css)
                                                          :on-click (fn [_] (swap! letters #(assoc-in % [y x] "B")))} (grid->cell @letters [x y])]
                                                  [:circle {:cx (coord x)
                                                            :cy (coord y)
                                                            :r 0.5 :fill "black"
                                                            :on-click #(js/alert (str %))}]])
                       (grid-cells 8 8)))]]]))))