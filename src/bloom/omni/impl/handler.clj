(ns bloom.omni.impl.handler
  (:require
    [mount.core :as mount]
    [bloom.omni.ring :as ring]
    [ring.middleware.format :refer [wrap-restful-format]]))

(mount/defstate handler
  :start (apply ring/combine (:handlers (mount/args)))
  :stop (fn []))
