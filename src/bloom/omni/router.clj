(ns bloom.omni.router
  (:require
    [secretary.core :as secretary]))

(defmacro defroute [& args]
  `(secretary/defroute ~@args))
