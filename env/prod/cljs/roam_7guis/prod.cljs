(ns roam-7guis.prod
  (:require [roam-7guis.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
