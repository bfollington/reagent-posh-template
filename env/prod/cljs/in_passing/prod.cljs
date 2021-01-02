(ns in-passino.prod
  (:require [in-passing.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
