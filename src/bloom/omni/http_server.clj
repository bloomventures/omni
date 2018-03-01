(ns bloom.omni.http-server
  "Provides a mount system that starts/stop an http-kit server.
  
  ```clojure
  (require '[bloom.omni.http-server :as http-server])
  (mount/start #'http-server/system)
  ```"
  (:require
   [org.httpkit.server :refer [run-server]]
   [mount.core :as mount]
   [bloom.omni.impl.config :refer [config]]
   [bloom.omni.impl.spa :as spa]))

(defn- stop-server!
  [server]
  (println "Stopping HTTP server...")
  (server :timeout 100)
  (println "HTTP server stopped"))

(defn- start-server!
  [port]
  (println (str "Starting HTTP server..."))
  (let [server (run-server #'spa/routes {:port port
                                         :max-body (* 100 1024 1024)})]
    (println (str "HTTP server started on http://127.0.0.1:" port "/"))
    server))

(mount/defstate system
  :start (start-server! (config :http-port))
  :stop (stop-server! system))
