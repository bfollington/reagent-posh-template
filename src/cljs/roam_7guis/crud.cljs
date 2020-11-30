(ns roam-7guis.crud
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.pprint :as pp]
            [clojure.string :as string]
            [re-com.core :refer [h-box v-box]]))

(defn log [& args]
  (doseq [arg args]
    (pp/pprint arg)))

(defn set-state! [state key value]
  ;; (log @state key value)
  (swap! state #(assoc % key value)))

;;

(def initial-state
  {:names {"0" ["Ben" "Follington"]
           "1" ["Conor" "White-Sullivan"]
           "2" ["Michael" "Ashcroft"]}
   :last-generated-id 2
   :filter ""
   :edit-form {:first ""
               :last ""}
   :selected-id "-1"})

(defn format-name [[first last]]
  (str last ", " first))

(defn select-names [state filter-text]
  (->> @state
       :names
       (into [])
       (filter (fn [[_ [_ last]]]
                 (string/starts-with? last filter-text)))))

(defn set-selected-entry! [state value]
  (set-state! state :selected-id value)
  (let [[first last] (get (:names @state) value)]
    (set-state! state :edit-form {:first first
                                  :last last})))

(defn name-list [state]
  [:select {:size 4
            :on-change (fn [e]
                         (let [value (-> e .-target .-value)]
                           (set-selected-entry! state value)))}
   (let [names (select-names state (:filter @state))]
     (map (fn [[i v]] [:option {:key i
                                :value i} (format-name v)]) names))])

(defn raw-input
  "an input field that directly updates the atom storing its value"
  [state path]
  [:input {:type "text"
           :value (get-in @state path)
           :on-change (fn [e]
                        (let [value (-> e .-target .-value)]
                          (swap! state #(assoc-in % path value))))}])

(defn edit-form [state]
  [v-box
   :children [[raw-input state [:edit-form :first]]
              [raw-input state [:edit-form :last]]]])

(defn filter-field [state]
  [:input {:type "text"
           :value (:filter @state)
           :on-change (fn [e] (let [value (-> e .-target .-value)]
                                (set-state! state :filter value)))}])
(defn generate-id! [state]
  (swap! state #(update % :last-generated-id inc))
  (str (:last-generated-id @state)))

(defn add-entry! [state]
  (let [form (:edit-form @state)
        entry [(:first form) (:last form)]]
    (swap! state #(assoc-in % [:names (generate-id! state)] entry))))

(defn update-entry! [state]
  (let [form (:edit-form @state)
        entry [(:first form) (:last form)]]
    (swap! state #(assoc-in % [:names (:selected-id @state)] entry))))

(defn delete-entry! [state]
  (swap! state #(update-in % [:names] dissoc (:selected-id @state)))
  (set-state! state :edit-form {:first "" :last ""}))

(defn action-buttons [state]
  [h-box
   :children [[:button {:on-click #(add-entry! state)} "Create"]
              [:button {:on-click #(update-entry! state)} "Update"]
              [:button {:on-click #(delete-entry! state)} "Delete"]]])

(defn crud []
  (let [state (atom initial-state)]
    (fn []
      [v-box
       :width "320px"
       :children [[filter-field state]
                  [h-box
                   :children [[name-list state]
                              [edit-form state]]]
                  [action-buttons state]]])))