(ns bloom.omni.impl.handler
  (:require
    [mount.core :as mount]
    [bloom.omni.impl.router :as router]))

(mount/defstate handler
  :start (router/routes->handler 
           (:routes (mount/args)))
  :stop (fn []))
