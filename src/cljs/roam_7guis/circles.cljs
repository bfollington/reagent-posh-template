(ns roam-7guis.circles
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.pprint :as pp]
            [clojure.string :as string]
            [herb.core :refer [<class]]
            [re-com.core :refer [h-box v-box]]))

(defn log [& args]
  (doseq [arg args]
    (pp/pprint arg)))

(defn set-state! [state key value]
  ;; (log @state key value)
  (swap! state #(assoc % key value)))

;;

(defn circle-data [[x y] r]
  {:x x :y y :r r})

(def initial-state {:snapshots [[(circle-data [0 0] 32)]]}) ;; a list of circles

;;

(defn px [v] (str v "px"))

(defn circle-style [x y r]
  ;; the things I do for hover styles
  ^{:pseudo {:hover {:background "rgba(0, 0, 0, 0.2)"}}}
  {:position "absolute"
   :left (px x)
   :top (px y)
   :width (str (* 2 r) "px")
   :height (str (* 2 r) "px")
   :border-radius "9999px"
   :border "1px solid black"})

(defn circle [{:keys [x y r]}]
  [:div
   {:key (str x y r)
    :class (<class circle-style x y r)}])

(defn select-current [state]
  (-> @state :snapshots last))

(defn add-snapshot! [state circles]
  (swap! state (fn [s] (update s :snapshots #(conj % circles)))))

(def default-radius 16)

(defn add-circle! [state x y]
  (let [current (select-current state)
        circle (circle-data [x y] default-radius)
        circles (conj current circle)]
    (log [current x y])
    (add-snapshot! state circles)))

(defn on-add-circle! [state e]
  (let [rect (-> e .-target .getBoundingClientRect)
        x (- (.-clientX e) (.-left rect) default-radius)
        y (- (.-clientY e) (.-top rect) default-radius)]
    (add-circle! state x y)))

(defn circles []
  (let [state (atom initial-state)]
    (fn []
      [:div {:style {:position "relative"
                     :width "420px"
                     :height "420px"}
             :on-click (partial on-add-circle! state)}
      ;;  (str (select-current state))
       (map circle (select-current state))])))