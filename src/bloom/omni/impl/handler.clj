(ns bloom.omni.impl.handler
  (:require
    [mount.core :as mount]
    [bloom.omni.ring :as ring]
    [bloom.omni.impl.config :refer [config]]))

(mount/defstate handler
  :start (apply ring/combine (config :handlers))
  :stop (fn []))
