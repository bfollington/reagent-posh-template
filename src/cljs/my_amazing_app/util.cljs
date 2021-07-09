(ns my-amazing-app.util
  (:require
   [clojure.pprint :as pp]))

(defn log [& args]
  (doseq [arg args]
    (pp/pprint arg)))

(defn set-state! [state key value]
  ;; (log @state key value)
  (swap! state #(assoc % key value)))

(def value #(-> % .-target .-value))

(defn in?
  "true if coll contains elm"
  [coll elm]
  (some #(= elm %) coll))