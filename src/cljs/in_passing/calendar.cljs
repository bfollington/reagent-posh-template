(ns in-passing.calendar
  (:require [reagent.core :as reagent :refer [atom]]
            [herb.core :refer [<class]]
            [re-com.core :refer [h-box v-box]]
            [in-passing.days :as d]
            [in-passing.move :as moves]
            [in-passing.levels :as levels]
            [in-passing.ui.days :as dui]
            [in-passing.util :refer [log in?]]
            [in-passing.state :as db]
            [posh.reagent :as p]
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

(defn on-grid-cell-selected [active-piece selected possible-moves events pieces today]
  (fn [d _]
    (cond
      (some? active-piece) (if (= d @selected)
                             (do
                               (log d "deselect")
                               (reset! selected nil))
                             (when (in? possible-moves d)
                               (moves/move-piece! active-piece d @selected events pieces)
                               (swap! today inc)
                               (reset! selected nil)))
      (nil? active-piece) (do
                            (log d "selected")
                            (reset! selected d))
      :else nil)))

(defn select-piece! [app-id piece-id]
  (db/add! app-id {:app/selected-piece piece-id}))

(defn deselect-piece! [app-id]
  (db/retract! app-id :app/selected-piece))

(defn posh-test []
  (let [app (db/->all [:app/id 0])
        app-id (:db/id app)

        sel-id (ffirst @(p/q db/->selected-piece db/conn))
        selected (db/->all sel-id)

        events @(p/q db/->pieces-on-day db/conn 1)
        event-id  (ffirst @(p/q db/->event-by-name db/conn "Dummy Thicc"))]
    (log events)
    [:div
     [:button {:on-click (fn [_e] (select-piece! app-id event-id))} "Set Selected"]
     [:button {:on-click (fn [_e] (deselect-piece! app-id))} "Remove Selected"]
     [:div (str "app " app)]
     [:div (str "event " event-id)]
     [:div (str "events " events)]
     [:div (str "selected " selected)]]))


(defn calendar []
  (let [month (atom :jan)
        level (get levels/levels @month)
        pieces (atom (get level :pieces))
        events (atom (get level :events))
        selected (atom nil)
        today (atom 3)]
    (fn []
      (let [days (d/gen-month @month)
            weeks (d/days->weeks days)
            active-piece (get-active-piece @selected @events @pieces)
            possible-moves (moves/valid-moves @selected days active-piece (get @pieces active-piece))
            on-selected (on-grid-cell-selected active-piece selected possible-moves events pieces today)

            reset-month! (fn [m]
                           (let [level (get levels/levels m)]
                             (reset! today 1)
                             (reset! pieces (get level :pieces))
                             (reset! events (get level :events))))]
        [:div
         [posh-test]
         [:p (str @month) " 2020"]
         [:button {:on-click (fn [e] (reset-month! @month))} "Reset Month"]
         [:button {:on-click (fn [e]
                               (swap! month d/next-month)
                               (reset-month! @month))} "Next Month"]
         [:button {:on-click (fn [e]
                               (swap! month d/prev-month)
                               (reset-month! @month))} "Prev Month"]
        ;;  [:div (str @selected) (str (get @pieces active-piece))]
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
                       weeks))]]]))))