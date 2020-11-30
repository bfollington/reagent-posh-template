(ns roam-7guis.tempconv
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.dom :as rdom]
            [roam-7guis.core :as core]
            [devcards.core :as dc])
  (:require-macros
   [devcards.core
    :as dc
    :refer [defcard defcard-doc defcard-rg deftest]]))

(defn to-fahrenheit [c]
  (+ (* c (/ 9 5)) 32))

(defn to-celcius [f]
  (* (/ 5 9) (- f 32)))

(defn value [e]
  (-> e .-target .-value))

(defn valid-input? [t]
  (-> t js/Number.isNaN not))

(defn validate! [in state]
  (let [num (js/parseFloat in)
        valid (valid-input? num)
        out {:value in :valid valid}]
    (reset! state out)
    out))

(defn temp-style [valid]
  {:background (if valid "white" "red")})

(defn temp-conv []
  (let [temp-internal (atom 5)

        input-c (atom {:value @temp-internal :valid true})
        input-f (atom {:value (to-fahrenheit @temp-internal) :valid true})

        update-internal! (fn [v units]
                           (reset! temp-internal
                                   (case units
                                     :c v
                                     :f (to-celcius v))))

        ;; TODO(ben): this is gross af
        mk-on-change (fn [input-field-state units]
                       (fn [e]
                         (let [result (validate! (value e) input-field-state)]
                           (when (:valid result) (update-internal! (:value result) units)))))

        mk-watch (fn [input-field-state units]
                   (fn [_ _ _ n]
                     (swap! input-field-state
                            #(assoc % :value
                                    (case units
                                      :c (Math/round n)
                                      :f (-> n to-fahrenheit Math/round))))))

        on-change-c (mk-on-change input-c :c)
        on-change-f (mk-on-change input-f :f)]

    (add-watch temp-internal :c (mk-watch input-c :c))
    (add-watch temp-internal :f (mk-watch input-f :f))

    (fn []
      [:div
       [:input {:type "number"
                :style (temp-style (:valid @input-c))
                :value (:value @input-c)
                :on-change on-change-c}]
       [:label "Celcius"]
       [:input {:type "number"
                :style (temp-style (:valid @input-f))
                :value (:value @input-f)
                :on-change on-change-f}]
       [:label "Fahrenheit"]])))
