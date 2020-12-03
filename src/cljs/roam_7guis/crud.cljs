(ns roam-7guis.crud
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.string :as string]
            [roam-7guis.util :as u]
            [roam-7guis.ui :as ui]
            [re-com.core :refer [h-box v-box]]))

(def initial-state
  {:names {"0" ["Ben" "Follington"]
           "1" ["Conor" "White-Sullivan"]
           "2" ["Michael" "Ashcroft"]}
   :last-generated-id 2
   :filter ""
   :edit-form {:first ""
               :last ""}
   :selected-id js/undefined})

(defn valid-name-form? [state]
  (and (-> @state :edit-form :first (string/blank?) (not))
       (-> @state :edit-form :last (string/blank?) (not))))

;;

(defn format-name [[first last]]
  (str last ", " first))

(defn s->names [state filter-text]
  (->> @state
       :names
       (into [])
       (filter (fn [[_ [_ last]]]
                 (string/starts-with? last filter-text)))))

(defn set-selection! [state id]
  (u/set-state! state :selected-id id)
  (let [[first last] (get (:names @state) id)]
    (u/set-state! state :edit-form {:first first
                                    :last last})))

(defn generate-id! [state]
  (swap! state #(update % :last-generated-id inc))
  (str (:last-generated-id @state)))

(defn clear-selection! [state]
  (u/set-state! state :edit-form {:first "" :last ""})
  (swap! state #(assoc % :selected-id js/undefined)))

;; there's a bug here, when the list is empty each thing you "add" overwrites itself
;; may be that I need to clear the selected-id on input
(defn add-entry! [state]
  (let [form (:edit-form @state)
        entry [(:first form) (:last form)]
        id (generate-id! state)]
    (swap! state #(assoc-in % [:names id] entry))
    (clear-selection! state)))

(defn update-entry! [state]
  (let [form (:edit-form @state)
        entry [(:first form) (:last form)]]
    (swap! state #(assoc-in % [:names (:selected-id @state)] entry))
    (clear-selection! state)))

(defn delete-entry! [state]
  (swap! state #(update-in % [:names] dissoc (:selected-id @state)))
  (clear-selection! state))


;;

(defn name-list [state]
  [ui/select-field
   {:size 4
    :style {:width "256px"}
    :value (:selected-id @state)
    :on-change (fn [e]
                 (set-selection! state (u/value e)))
    :options (let [names (s->names state (:filter @state))]
               (map (fn [[i v]] [:option {:key i
                                          :value i} (format-name v)]) names))}])

(defn raw-input
  "an input field that directly updates the atom storing its value"
  [state path label]
  [ui/input-field
   {:type "text"
    :valid true
    :placeholder label
    :value (get-in @state path)
    :on-change (fn [e]
                 (swap! state #(assoc-in % path (u/value e))))}])

(defn edit-form [state]
  [v-box
   :gap "4px"
   :children [[raw-input state [:edit-form :first] "First name"]
              [raw-input state [:edit-form :last] "Last name"]]])

(defn filter-field [state]
  [ui/input-field
   {:type "text"
    :valid true
    :placeholder "type to filter..."
    :value (:filter @state)
    :on-change (fn [e]
                 (u/set-state! state :filter (u/value e))
                 (clear-selection! state))}])

(defn action-buttons [state]
  (let [has-selection (exists? (:selected-id @state))]
    [h-box
     :gap "8px"
     :children [[ui/button {:on-click #(add-entry! state) :disabled (not (valid-name-form? state)) :label "➕ Create"}]
                [ui/button {:on-click #(update-entry! state) :disabled (not has-selection) :label "✏️ Update"}]
                [ui/button {:on-click #(delete-entry! state) :disabled (not has-selection) :label "🗑 Delete"}]]]))

(defn crud []
  (let [state (atom initial-state)]
    (fn []
      [v-box
       :width "420px"
       :gap "8px"
       :children [[filter-field state]
                  [h-box
                   :gap "8px"
                   :children [[name-list state]
                              [edit-form state]]]
                  [action-buttons state]]])))