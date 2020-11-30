(ns roam-7guis.parser
  (:require [clojure.pprint :as pp]))

(defn log [& args]
  (doseq [arg args]
    (pp/pprint arg)))


(defn evaluate [get-cell-value [op arg]]
  (log [op arg])
  (case op
    :value arg
    :formula (evaluate get-cell-value arg)
    :add (reduce + (map (comp js/parseInt
                              (partial evaluate get-cell-value))
                        arg))

    (get-cell-value (str op arg))))

(defn evaluate-formula [formula get-cell-value]
  (let [parsed (cljs.reader/read-string formula)
        result (evaluate get-cell-value [:formula parsed])]
    result))
