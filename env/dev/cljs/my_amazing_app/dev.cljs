(ns ^:figwheel-no-load my-amazing-app.dev
  (:require
   [my-amazing-app.core :as core]
   [devtools.core :as devtools]))

(extend-protocol IPrintWithWriter
  js/Symbol
  (-pr-writer [sym writer _]
    (-write writer (str "\"" (.toString sym) "\""))))

(devtools/install!)

(enable-console-print!)

(core/init!)
