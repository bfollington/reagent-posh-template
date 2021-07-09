(ns my-amazing-app.query)

(def ->counter-value-by-name
  '[:find ?v
    :in $ ?counter-name
    :where
    [?c :counter/name ?counter-name]
    [?c :counter/value ?v]])

(def ->counter-values
  '[:find ?v
    :where
    [?c :counter/value ?v]])

(def ->counter-by-name
  '[:find ?c
    :in $ ?counter-name
    :where
    [?c :counter/name ?counter-name]])