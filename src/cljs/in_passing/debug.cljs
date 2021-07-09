(ns in-passing.debug
  (:require [in-passing.state :as db]
            [in-passing.query :as q]
            [goog.string.format]))

(defn debug []
  (let [select-many (partial db/select-many db/conn)

        ;; subscribe to all counter changes 
        _ (select-many q/->counter-values)]
    [:div
     [:code (pr-str @db/conn)]]))