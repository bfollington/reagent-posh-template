(ns in-passing.state
  (:require [datascript.core :as dt]
            [in-passing.levels :as levels]
            [in-passing.util :as u]
            [posh.reagent :as p]))

(def db-schema
  {:game/id {:db/unique :db.unique/identity}
   :game/selected-day {:db/cardinality :db.cardinality/one}})

(defn load-month! [conn month]
  (dt/transact! conn (concat [{:game/id 0
                              ;;  :game/selected-day nil
                               :game/month :jan
                               :game/today 3}]
                             (get levels/levels-2 month))))

(def conn (dt/create-conn db-schema))
(load-month! conn :jan)
(p/posh! conn)

(defn new-entity!
  "Initialize a new entity in the db."
  [conn varmap]
  ((:tempids (dt/transact! conn [(merge varmap {:db/id -1})])) -1))

(defn retract-entity
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

(def ->selected-day
  '[:find ?selected
    :where [_ :game/selected-day ?selected]])

(def ->selected-piece
  '[:find ?p
    :where
    [_ :game/selected-day ?selected
     ?p :event/day ?selected]])

(def ->pieces-on-day
  '[:find ?d
    :in $ ?date
    :where
    [?d :event/day ?date]])

(def ->active-piece-on-day
  '[:find ?d
    :in $ ?date
    :where
    [?d :event/day ?date]
    [?d :event/status :active]])

(def ->event-by-name
  '[:find ?e
    :in $ ?name
    :where
    [?e :event/name ?name]])

(defn ->all [ent]
  @(p/pull conn '[*] ent))

(def ->current-month
  '[:find ?m
    :where
    [?e :game/month ?m]])

(defn select [conn query & params]
  (ffirst @(apply p/q query conn params)))

(defn select-many [conn query & params]
  (map first @(apply p/q query conn params)))