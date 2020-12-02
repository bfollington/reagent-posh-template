(ns roam-7guis.timer
  (:require [reagent.core :as reagent :refer [atom]]
            [roam-7guis.util :as u]
            [roam-7guis.ui :as ui]
            [re-com.core :refer [h-box v-box]]))

(defn set-duration! [state duration]
  (u/set-state! state :duration duration))


;;

(defn progress-bar [value max]
  [h-box
   :gap "8px"
   :align :center
   :children [[ui/label "Elapsed time"]
              [:progress {:style {:width "128px"} :value value :max max}]]])

(defn duration-slider [state]
  [h-box
   :gap "8px"
   :children [[ui/label "Duration"]
              [:input {:type "range"
                       :value (:duration @state)
                       :min 0
                       :max 16
                       :on-change (fn [e]
                                    (set-duration! state (u/value e)))}]]])

(defn timer []
  (let [state (atom {:duration 10
                     :current 5})]
    (fn []
      (js/setTimeout
       (fn [_]
         (when (< (:current @state) (:duration @state))
           (swap! state #(update % :current (partial + 0.01)))))
       10)

      [v-box
       :width "256px"
       :gap "4px"
       :children [[progress-bar (-> @state :current) (-> @state :duration)]
                  [ui/label (str (-> @state :current (.toFixed 2)) "s")]
                  [duration-slider state]
                  [ui/button {:on-click #(u/set-state! state :current 0) :label "Reset"}]]])))
