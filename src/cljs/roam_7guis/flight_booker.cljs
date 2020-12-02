(ns roam-7guis.flight-booker
  (:require [reagent.core :as reagent :refer [atom]]
            [roam-7guis.util :as u]
            [cljs-time.core :as time]
            [cljs-time.format :refer [parse unparse formatter]]
            [re-com.core :refer [h-box box gap v-box hyperlink-href p]]))


(def date-format (formatter "dd/MM/YYYY"))

;;

(defn yesterday-at-midnight []
  (time/plus (time/today-at-midnight) (time/days -1)))

(defn validate-date [date-str]
  (let [date (try
               (parse date-format date-str)
               (catch :default _
                 :invalid))
        valid (if (not (= date :invalid))
                (time/after? date (yesterday-at-midnight)) ;; dates must be in the future
                false)]
    {:value date-str :valid valid}))


(defn is-one-way-flight? [state]
  (= (:type state) "one-way"))

(defn get-dates [state]
  {:depart (->> state :depart-date :value (parse date-format))
   :return (->> state :return-date :value (parse date-format))})

(defn return-before-depart? [state]
  (if (and (-> state :depart-date :valid)
           (-> state :return-date :valid))
    (let [{:keys [depart return]} (get-dates state)]
      (time/before? return depart))
    false))


(defn set-flight-type! [state type]
  (u/set-state! state :type type))

(defn show-popup! [state]
  (let [depart (->> state :depart-date :value)
        return (->> state :return-date :value)]
    (js/alert
     (case (:type state)
       "one-way" (str "You have booked a one-way flight on " depart)
       "return" (str "You have booked a return flight, departing on " depart " and returning on " return)))))

;;

(defn flight-type [value state]
  [:select {:value value
            :on-change (fn [e] (set-flight-type! state (u/value e)))}
   [:option {:value :one-way} "one-way flight"]
   [:option {:value :return} "return flight"]])

(defn date-entry [field key state & {:keys [disabled] :or {disabled false}}]
  [:input {:value (:value field)
           :style {:background
                   (cond
                     (:valid field) "white"
                     disabled "#eee"
                     (not (:valid field)) "#FF9999")}
           :disabled disabled
           :on-change (fn [e]
                        (u/set-state! state key (validate-date (u/value e))))}])

(defn flight-booker []
  (let [state (atom {:type "one-way"
                     :depart-date {:value (unparse date-format (time/today-at-midnight))
                                   :valid true}
                     :return-date {:value (unparse date-format (time/today-at-midnight))
                                   :valid true}})]
    (fn []
      [v-box
       :width "256px"
       :gap "4px"
       :children [[:p "Flights can only be booked in the future"]
                  [flight-type (:type @state) state]
                  [date-entry (-> @state :depart-date) :depart-date state]
                  [date-entry
                   (-> @state :return-date)
                   :return-date
                   state
                   :disabled (or (-> @state :depart-date :valid not)
                                 (return-before-depart? @state)
                                 (is-one-way-flight? @state))]
                  [:button {:on-click #(show-popup! @state)} "Book"]]])))