(ns roam-7guis.cards
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.dom :as rdom]
            [roam-7guis.core :as core]
            [devcards.core :as dc])
  (:require-macros
   [devcards.core
    :as dc
    :refer [defcard defcard-doc defcard-rg deftest]]))

(defcard-rg first-card
  [:div>h1 "This is your first devcard!"])

(defcard-rg counter
  (let [clicks (atom 0)
        on-clicked #(swap! clicks inc)]
    (fn []
      [:div
       [:label @clicks]
       [:button {:on-click on-clicked} "Count"]])))

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

(defcard-rg temp-conv
  (let [temp-internal (atom 5)

        input-c (atom {:value @temp-internal :valid true})
        input-f (atom {:value (to-fahrenheit @temp-internal) :valid true})

        on-change-c (fn [e]
                      (let [result (validate! (value e) input-c)]
                        (when (:valid result)
                          (reset! temp-internal (:value result)))))
        on-change-f (fn [e]
                      (let [result (validate! (value e) input-f)]
                        (when (:valid result)
                          (reset! temp-internal (-> result :value to-celcius)))))]

    (add-watch temp-internal :temp-c
               (fn [_ _ _ n]
                 (swap! input-c #(assoc % :value (Math/round n)))))
    (add-watch temp-internal :temp-f
               (fn [_ _ _ n]
                 (swap! input-f #(assoc % :value (-> n to-fahrenheit Math/round)))))

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

(defcard-rg home-page-card
  [core/home-page])

(rdom/render [:div] (.getElementById js/document "app"))

;; remember to run 'lein figwheel devcards' and then browse to
;; http://localhost:3449/cards
