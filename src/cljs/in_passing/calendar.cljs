(ns in-passing.calendar
  (:require [reagent.core :as reagent :refer [atom]]
            [herb.core :refer [<class]]
            [re-com.core :refer [h-box v-box]]
            [in-passing.days :as d]
            [in-passing.ui.days :as dui]
            [in-passing.util :as u]
            [goog.string :as gstring]
            [goog.string.format]))

(defn calc-day-state [d today]
  (cond
    (< d today) :past
    (= d today) :today
    (> d today) :default))

(defn get-pieces [d events pieces]
  (map (fn [d] (get pieces d)) (get events d)))

(defn get-active-piece [d events pieces]
  (when (some? d)
    (let [day-events (get events d)
          active (->> day-events
                      (map (fn [d] [d (get pieces d)]))
                      (filter (fn [[_ [_ _ s]]] (= s :active)))
                      (first))
          [d _] active]
      d)))

(defn in?
  "true if coll contains elm"
  [coll elm]
  (some #(= elm %) coll))

(defn move-piece! [piece-id target-day from-day events]
  (u/log ["move" piece-id "to" target-day])
  ;; 0. ensure there is a list for this day
  (swap! events update-in [target-day] (fnil identity []))
  ;; 1. mark all pieces on target-day as :taken
  ;; TODO
  ;; 2. remove piece-id from old location
  (swap! events update-in [from-day] (fn [old] (filter (fn [p] (not (= piece-id p))) old)))
  ;; 3. put piece-id on target-day
  (swap! events update-in [target-day] (fn [old] (conj old piece-id))))

(defn on-grid-cell-selected [active-piece selected possible-moves events]
  (fn [d _]
    (cond
      (some? active-piece) (if (= d @selected)
                             (do
                               (u/log d "deselect")
                               (reset! selected nil))
                             (when (in? possible-moves d)
                               (move-piece! active-piece d @selected events)
                               (reset! selected nil)))
      (nil? active-piece) (do
                            (u/log d "selected")
                            (reset! selected d))
      :else nil)))

(defn calendar []
  (let [mpos (atom [0 0])
        month :feb
        days (d/gen-month month)
        pieces (atom {0 [:king "Appt/ Dr. King" :active]
                      1 [:pawn "Work" :taken]
                      2 [:pawn "Work" :active]
                      3 [:pawn "Work" :active]
                      4 [:pawn "Work" :active]})
        events (atom {7 [0 1]
                      9 [2]
                      16 [3]
                      23 [4]})
        selected (atom nil)
        today (atom 3)]
    (fn []
      (let [[mx my] @mpos
            active-piece (get-active-piece @selected @events @pieces)
            possible-moves [6 13 14]
            on-selected (on-grid-cell-selected active-piece selected possible-moves events)]
        [:div
         [:p (str month) " 2020"]
         [:button {:on-click (fn [e] (swap! today inc))} "Next Day"]
         [:div (str @selected) (str (get @pieces active-piece))]
         [:table
          [:thead
           [:tr
            [:td "M"]
            [:td "T"]
            [:td "W"]
            [:td "T"]
            [:td "F"]
            [:td "S"]
            [:td "S"]]]
          [:tbody
           (doall (map (fn [wk]
                         ^{:key wk}
                         [:tr
                          (doall (map-indexed (fn [i d]
                                                ^{:key (str i d)}
                                                [:td [dui/day d {:state (calc-day-state d @today)
                                                                 :on-selected (partial on-selected d)
                                                                 :preview-piece (if (in? possible-moves d) (get @pieces active-piece) nil)
                                                                 :events (get-pieces d @events @pieces)}]])
                                              wk))])
                       days))]]]))))