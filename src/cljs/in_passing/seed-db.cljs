(ns in-passing.seed-db
  (:require [in-passing.state :as db]))

(def counters ["Test", "Another Counter"])

(defn seed-db! []
  (doseq [c counters]
    (db/new-entity! db/conn {:counter/name c :counter/value 0})))
