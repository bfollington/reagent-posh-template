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

(def default-radius 16)
(defn px [v] (str v "px"))

(defn constrain [min max value]
  (Math/max min (Math/min max value)))

(def initial-state {:actions [[:init 0 []]]
                    :last-id 1
                    :cursor 0})

(defn generate-id! [state]
  (swap! state #(update % :last-id inc))
  (:last-id @state))

(defn add-action! [state action]
  (let [history (subvec (:actions @state) 0 (inc (:cursor @state)))
        history' (conj history action)]
    (swap! state (fn [s] (-> s
                             (assoc :actions history')
                             (update :cursor inc))))))


(defn set-circle-diameter! [state id d]
  (add-action! state [:set-diameter id d]))

(defn add-circle! [state x y]
  (add-action! state [:add-circle (generate-id! state) [x y default-radius]]))

;;

(defn count-entries [state]
  (count (:actions @state)))

(defn calculate-circles [actions]
  (into [] (reduce (fn [circles [cmd id param]]
                     (case cmd
                       :init circles
                       :add-circle (assoc circles id param)
                       :set-diameter (update circles id (fn [[x y _]] [x y param]))))
                   {} actions)))

(defn select-current [state]
  (-> @state :actions (subvec 0 (inc (:cursor @state)))))

;;


(defn circle-style [x y r]
  ;; the things I do for hover styles
  ^{:pseudo {:hover {:background "rgba(0, 0, 0, 0.2)"}}}
  {:position "absolute"
   :left (px x)
   :top (px y)
   :width (str (* 2 r) "px")
   :height (str (* 2 r) "px")
   :border-radius "9999px"
   :transform "translate(-50%, -50%)"
   :border "1px solid black"})


(defn on-click-circle [id state e]
  (.stopPropagation e)
  (set-circle-diameter! state id 32))

(defn circle [state [id [x y r]]]
  ^{:key (str x y r)}
  [:div
   {:class (<class circle-style x y r)
    :on-click (partial on-click-circle id state)}])

(defn on-add-circle! [state e]
  (let [rect (-> e .-target .getBoundingClientRect)
        x (- (.-clientX e) (.-left rect))
        y (- (.-clientY e) (.-top rect))]
    (add-circle! state x y)))

(defn on-undo! [state]
  (let [dec-cursor (comp (partial constrain 0 (count-entries state)) dec)]
    (swap! state #(update-in % [:cursor] dec-cursor))))

(defn on-redo! [state]
  (let [inc-cursor (comp (partial constrain 0 (dec (count-entries state))) inc)]
    (swap! state #(update-in % [:cursor] inc-cursor))))

(defn gen-key [[id _]]
  (str id))

(defn circles []
  (let [state (atom initial-state)]
    (fn []
      [v-box
       :gap "8px"
       :children [[h-box
                   :gap "8px"
                   :children [[:button {:on-click #(on-undo! state)} "⏪ Undo"]
                              [:button {:on-click #(on-redo! state)} "Redo ⏩"]]]
                  [:div {:style {:position "relative"
                                 :width "420px"
                                 :height "420px"
                                 :border "1px solid black"
                                 :overflow "hidden"}
                         :on-click (partial on-add-circle! state)}
                   (map
                    (fn [v] ^{:key (gen-key v)} [circle state v])
                    (-> state select-current calculate-circles))]]])))