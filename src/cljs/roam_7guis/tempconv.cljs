(ns roam-7guis.tempconv
  (:require [reagent.core :as reagent :refer [atom]]
            [roam-7guis.util :as u]
            [roam-7guis.ui :as ui]
            [re-com.core :refer [h-box]]))

(defn c->f [c]
  (+ (* c (/ 9 5)) 32))

(defn f->c [f]
  (* (/ 5 9) (- f 32)))

(defn valid-input? [t]
  (-> t js/Number.isNaN not))

(defn validate! [in state]
  (let [num (js/parseFloat in)
        valid (valid-input? num)
        out {:value in :valid valid}]
    (reset! state out)
    out))

(defn temp-style [valid]
  {:background (if valid "white" "#FF9999")})

(defn mk-watch
  [input-field-state units]
  (fn [_ _ _ n]
    (swap! input-field-state
           #(assoc % :value
                   (case units
                     :c (Math/round n)
                     :f (-> n c->f Math/round))))))

(defn temp-conv []
  (let [temp-internal (atom 5) ;; "true value" in C

        input-c (atom {:value @temp-internal :valid true})
        input-f (atom {:value (c->f @temp-internal) :valid true})

        update-internal! (fn [v units]
                           (reset! temp-internal
                                   (case units
                                     :c v
                                     :f (f->c v))))

        on-edit (fn [field-state units e]
                  (let [result (validate! (u/value e) field-state)]
                    (when (:valid result) (update-internal! (:value result) units))))

        on-change-c #(on-edit input-c :c %)
        on-change-f #(on-edit input-c :c %)]

    (add-watch temp-internal :c (mk-watch input-c :c))
    (add-watch temp-internal :f (mk-watch input-f :f))

    (fn []
      [h-box
       :gap "8px"
       :align :center
       :children [[ui/input-field
                   {:type "number"
                    :valid (:valid @input-c)
                    :value (:value @input-c)
                    :on-change on-change-c}]
                  [ui/label "Celcius"]
                  [ui/label "<=>"]
                  [ui/input-field
                   {:type "number"
                    :valid (:valid @input-f)
                    :value (:value @input-f)
                    :on-change on-change-f}]
                  [ui/label "Fahrenheit"]]])))
