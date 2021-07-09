(ns my-amazing-app.state
  (:require [datascript.core :as dt]
            [posh.reagent :as p]))

(def db-schema {})

(def conn (dt/create-conn db-schema))
(p/posh! conn)

(defn new-entity!
  "Initialize a new entity in the db, generates an eid."
  [conn varmap]
  ((:tempids (dt/transact! conn [(merge varmap {:db/id -1})])) -1))

(defn retract-entity!
  "Remove an entity (eid) from the db."
  [conn eid]
  (p/transact! conn [[:db.fn/retractEntity eid]]))

(defn retract!
  "Retract passed attrs from eid."
  [eid & attrs]
  (->> attrs
       (map (fn [a] [:db/retract eid a]))
       (p/transact! conn)))

(defn add!
  "Associate and/or overwrite attr with val for eid.
   If a map is provided, associate each key and value with eid."
  ([eid attr val] (p/transact! conn [[:db/add eid attr val]]))
  ([eid attr-map]
   (->> attr-map
        (into [])
        (map (fn [[k v]] [:db/add eid k v]))
        (p/transact! conn))))

(defn ->all
  "Pull all attrs for eid"
  [eid]
  @(p/pull conn '[*] eid))

(defn select
  "Perform query, return one result."
  [conn query & params]
  (ffirst @(apply p/q query conn params)))

(defn select-many
  "Perform query, return set of results"
  [conn query & params]
  (map first @(apply p/q query conn params)))