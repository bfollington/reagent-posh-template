(ns roam-7guis.util
  (:require
   [clojure.pprint :as pp]))

(defn log [& args]
  (doseq [arg args]
    (pp/pprint arg)))

(defn set-state! [state key value]
  ;; (log @state key value)
  (swap! state #(assoc % key value)))

(def value #(-> % .-target .-value))