(ns my-amazing-app.prod
  (:require [my-amazing-app.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
