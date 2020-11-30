(ns roam-7guis.crud
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.pprint :as pp]
            [clojure.string :as string]
            [cljs-time.format :refer [parse unparse formatter]]
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
   :filter ""
   :selected-id "-1"})

(defn format-name [[first last]]
  (str last ", " first))

(defn select-names [state filter-text]
  (->> @state
       :names
       (into [])
       (filter (fn [[_ [_ last]]]
                 (string/starts-with? last filter-text)))))

(defn name-list [state]
  [:select {:size 4
            :on-change (fn [e]
                         (let [value (-> e .-target .-value)]
                           (set-state! state :selected-id value)))}
   (let [names (select-names state (:filter @state))]
     (map (fn [[i v]] [:option {:key i
                                :value i} (format-name v)]) names))])

(defn edit-form [state selected-index]
  (let [selected (get (:names @state) selected-index)]
    (if (some? selected)
      (let [[first last] selected]
        [v-box
         :children [[:input {:type "text" :value first}]
                    [:input {:type "text" :value last}]]])
      [:div "does not have a value" selected-index])))

(defn filter-field [state]
  [:input {:type "text"
           :value (:filter @state)
           :on-change (fn [e] (let [value (-> e .-target .-value)]
                                (set-state! state :filter value)))}])

(defn crud []
  (let [state (atom initial-state)]
    (fn []
      [v-box
       :width "320px"
       :children [[filter-field state]
                  [h-box
                   :children [[name-list state]
                              [edit-form state (:selected-id @state)]]]
                  [:div "buttons"]]])))