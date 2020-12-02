(ns roam-7guis.circles
  (:require [reagent.core :as reagent :refer [atom]]
            [roam-7guis.util :as u]
            [roam-7guis.ui :as ui]
            [herb.core :refer [<class]]
            [re-com.core :refer [h-box v-box]]))

(def default-radius 16)

(defn constrain [min max value]
  (Math/max min (Math/min max value)))

(def initial-state {:actions [[:init 0 []]]
                    :last-id 1
                    :cursor 0
                    :selected-circle-id nil})

;; mutations

(defn generate-id! [state]
  (swap! state #(update % :last-id inc))
  (:last-id @state))

(defn add-action! [state action]
  (let [history (subvec (:actions @state) 0 (inc (:cursor @state)))
        history' (conj history action)]
    (swap! state (fn [s] (-> s
                             (assoc :actions history')
                             (update :cursor inc))))))


(defn set-circle-radius! [state id d]
  (add-action! state [:set-radius id d]))

(defn add-circle! [state x y]
  (add-action! state [:add-circle (generate-id! state) [x y default-radius]]))

(defn deselect-circle! [state]
  (swap! state #(assoc-in % [:selected-circle-id] nil)))

;; selectors

(defn count-entries [state]
  (count (:actions @state)))

(defn calculate-circles [actions]
  (reduce (fn [circles [cmd id param]]
            (case cmd
              :init circles
              :add-circle (assoc circles id param)
              :set-radius (update circles id (fn [[x y _]] [x y param]))))
          {} actions))

(defn calculate-circles-list [actions]
  (into [] (calculate-circles actions)))

(defn select-current [state]
  (-> @state :actions (subvec 0 (inc (:cursor @state)))))

;; views -> circle

(defn circle-style [x y r]
  ;; the things I do for hover styles
  ^{:pseudo {:hover {:background "rgba(0, 0, 0, 0.2)"}}}
  {:position "absolute"
   :left (ui/px x)
   :top (ui/px y)
   :width (str (* 2 r) "px")
   :height (str (* 2 r) "px")
   :border-radius "9999px"
   :transform "translate(-50%, -50%)"
   :border "1px solid black"})

(defn block-event [e]
  (.stopPropagation e))

(defn on-edit-circle! [id state e]
  (.stopPropagation e)
  (.preventDefault e)
  (u/set-state! state :selected-circle-id id))

(defn circle [state [id [x y r]]]
  ^{:key (str x y r)}
  [:div
   {:class (<class circle-style x y r)
    :on-click block-event
    :on-context-menu (partial on-edit-circle! id state)}])

;; views -> main canvas

(defn on-add-circle! [state e]
  (let [rect (-> e .-target .getBoundingClientRect)
        x (- (.-clientX e) (.-left rect))
        y (- (.-clientY e) (.-top rect))]
    (add-circle! state x y)))

(defn on-undo! [state]
  (let [dec-cursor (comp (partial constrain 0 (count-entries state)) dec)]
    (swap! state #(update-in % [:cursor] dec-cursor))
    (deselect-circle! state)))

(defn on-redo! [state]
  (let [inc-cursor (comp (partial constrain 0 (dec (count-entries state))) inc)]
    (swap! state #(update-in % [:cursor] inc-cursor))
    (deselect-circle! state)))

(defn gen-key [[id _]]
  (str id))

(defn edit-circle [state id [x y r]]
  (let [form (atom r)]
    (fn []
      [ui/popover
       {:x x :y y
        :content [v-box
                  :gap "4px"
                  :children [[ui/label "Adjust Radius"]
                             [:input {:type "range"
                                      :value @form
                                      :min 0
                                      :max 128
                                      :on-change #(reset! form (u/value %))}]
                             [ui/button
                              {:label "Save"
                               :on-click #((do (set-circle-radius! state id @form)
                                               (deselect-circle! state)))}]]]}])))

(defn canvas-style []
  {:position "relative"
   :width "420px"
   :height "420px"
   :border "1px solid #aaa"
   :border-radius "3px"
   :overflow "hidden"})

(defn circles []
  (let [state (atom initial-state)]
    (fn []
      [v-box
       :gap "8px"
       :children [[ui/label "Click to place a circle, right click to adjust size"]
                  [h-box
                   :gap "8px"
                   :children [[ui/button {:on-click #(on-undo! state) :label "⏪ Undo"}]
                              [ui/button {:on-click #(on-redo! state) :label "Redo ⏩"}]]]
                  [:div {:style {:position "relative"}}

                   [:div {:class (<class canvas-style)
                          :on-click (partial on-add-circle! state)}

                    (map
                     (fn [v] ^{:key (gen-key v)} [circle state v])
                     (-> state select-current calculate-circles-list))]

                   (let [selected (:selected-circle-id @state)
                         circles (-> state select-current calculate-circles)]
                     (when (some? selected)
                       (let [[y x r] (get circles selected)]
                         [edit-circle state selected [x y r]])))]]])))