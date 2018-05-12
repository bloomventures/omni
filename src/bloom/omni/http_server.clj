(ns bloom.omni.http-server
  "Provides a component that starts/stop an http-kit server.

  ```clojure
  (require '[bloom.omni.http-server :as http-server])
  (http-server/start! 1523 handler)
  ```"
  (:require
    [org.httpkit.server :refer [run-server]]))

(defn stop!
  [server]
  (println "Stopping HTTP server...")
  (server :timeout 100)
  (println "HTTP server stopped"))

(defn start!
  [{:keys [port handler]}]
  (println (str "Starting HTTP server..."))
  (let [server (run-server handler
                           {:port port
                            :max-body (* 100 1024 1024)})]
    (println (str "HTTP server started on http://127.0.0.1:" port "/"))
    server))
