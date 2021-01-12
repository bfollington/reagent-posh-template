(ns in-passing.state
  (:require [datascript.core :as dt]
            [posh.reagent :as p]))

(def db-schema
  {:app/id {:db/unique :db.unique/identity}
   :day/date {:db/unique :db.unique/identity}
   :day/events {:db/valueType :db.type/ref
                :db/cardinality :db.cardinality/many}})

(defn seed-db [conn]
  (dt/transact! conn [{:app/id 0
                      ;;  :app/selected-piece nil
                       }
                      {:day/date 1
                       :day/events [{:event/piece :king
                                     :event/name "Test Event"}
                                    {:event/piece :queen
                                     :event/name "Another Test Event"}]}
                      {:day/date 2
                       :day/events [{:event/piece :pawn
                                     :event/name "Dummy Thicc"}]}]))

(def conn (dt/create-conn db-schema))
(seed-db conn)
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

(def ->selected-piece
  '[:find ?selected
    :where [_ :app/selected-piece ?selected]])

(def ->pieces-on-day
  '[:find ?ps
    :in $ ?date
    :where
    [?d :day/date ?date]
    [?d :day/events ?ps]])

(def ->event-by-name
  '[:find ?e
    :in $ ?name
    :where
    [?e :event/name ?name]])

(defn ->all [ent]
  @(p/pull conn '[*] ent))

