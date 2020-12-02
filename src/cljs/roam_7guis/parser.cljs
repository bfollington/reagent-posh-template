(ns roam-7guis.parser
  (:require [clojure.pprint :as pp]))

(defn log [& args]
  (doseq [arg args]
    (pp/pprint arg)))

(defn find-deps [[op arg] deps]
  (case op
    :formula (find-deps arg deps)
    :ref (conj deps arg)
    :add (reduce concat (map #(find-deps % []) arg))
    :sub (reduce concat (map #(find-deps % []) arg))

    (conj deps (str op arg))))

(defn evaluate [get-cell-value [op arg]]
  (log [op arg])
  (case op
    :value arg
    :formula (evaluate get-cell-value arg)
    :ref (get-cell-value arg)
    :add (reduce + (map (comp js/parseInt
                              (partial evaluate get-cell-value))
                        arg))
    :sub (reduce - (map (comp js/parseInt
                              (partial evaluate get-cell-value))
                        arg))

    ;; should really be validating that this is a real cell ID
    (get-cell-value (str op arg))))

(defn evaluate-deps [formula]
  (try
    (let [parsed (cljs.reader/read-string formula)
          deps (find-deps parsed [])]
      deps)
    (catch js/Object _
      [])))

(defn evaluate-formula [formula get-cell-value]
  (try
    (let [parsed (cljs.reader/read-string formula)
          result (evaluate get-cell-value [:formula parsed])
          deps (find-deps parsed [])]

      (log ["evaluate-formula" result deps])
      result)
    (catch js/Object _
      "#ERROR!")))
