(ns in-passing.move
  (:require [reagent.core :as reagent :refer [atom]]
            [herb.core :refer [<class]]
            [re-com.core :refer [h-box v-box]]
            [in-passing.days :as d]
            [in-passing.ui.days :as dui]
            [in-passing.util :refer [log in?]]
            [goog.string :as gstring]
            [goog.string.format]))

(defn move-piece! [piece-id target-day from-day events]
  (log ["move" piece-id "to" target-day])
  ;; 0. ensure there is a list for this day
  (swap! events update-in [target-day] (fnil identity []))
  ;; 1. mark all pieces on target-day as :taken
  ;; TODO
  ;; 2. remove piece-id from old location
  (swap! events update-in [from-day] (fn [old] (filter (fn [p] (not (= piece-id p))) old)))
  ;; 3. put piece-id on target-day
  (swap! events update-in [target-day] (fn [old] (conj old piece-id))))

(defn indices
  [v coll]
  (keep-indexed (fn [idx x]
                  (when (= v x)
                    idx))
                coll))

(defn touching-sides [day days]
  (let [idx (first (indices day days))
        left (= (mod idx 7) 0)
        right (= (mod idx 7) 6)
        top (< idx 7)]
    [left right top false]))

(defn king-movement [from-day [left right top bottom]]
  [(when (not left) (- from-day 1))
   (when (not right) (+ from-day 1))
   (when (not top) (- from-day 7))
   (when (not bottom) (+ from-day 7))
   (when (and (not top) (not left)) (- from-day 8))
   (when (and (not bottom) (not left)) (+ from-day 6))
   (when (and (not top) (not right)) (- from-day 6))
   (when (and (not bottom) (not right)) (+ from-day 8))])

(defn valid-moves [from-day days piece-id piece]
  (let [[type name status] piece
        touching (touching-sides from-day days)]
    (log touching)
    (king-movement from-day touching)))