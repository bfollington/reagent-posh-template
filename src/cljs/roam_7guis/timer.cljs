(ns roam-7guis.timer
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.pprint :as pp]
            [re-com.core :refer [h-box v-box]]))

(defn log [& args]
  (doseq [arg args]
    (pp/pprint arg)))

(defn set-state! [state key value]
  ;; (log @state key value)
  (swap! state #(assoc % key value)))

;;

(defn set-duration! [state duration]
  (set-state! state :duration duration))

;;

(defn progress-bar [value max]
  [h-box
   :gap "8px"
   :align :center
   :children [[:label "Elapsed time"]
              [:progress {:style {:width "128px"} :value value :max max}]]])

(defn duration-slider [state]
  [h-box
   :gap "8px"
   :children [[:label "Duration"]
              [:input {:type "range"
                       :value (:duration @state)
                       :min 0
                       :max 16
                       :on-change (fn [e]
                                    (let [value (-> e .-target .-value)]
                                      (set-duration! state value)))}]]])

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
                  [:label (str (-> @state :current (.toFixed 2)) "s")]
                  [duration-slider state]
                  [:button {:on-click #(set-state! state :current 0)} "Reset"]]])))
