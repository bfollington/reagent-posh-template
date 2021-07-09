(ns my-amazing-app.seed-db
  (:require [my-amazing-app.state :as db]))

(def counters ["Test", "Another Counter"])

(defn seed-db! []
  (doseq [c counters]
    (db/new-entity! db/conn {:counter/name c :counter/value 0})))
