(ns bloom.omni.http-server
  "Provides a mount component that starts/stop an http-kit server.
  
  ```clojure
  (require '[bloom.omni.http-server :as http-server])

  (-> (mount/with-args {:routes [[:get \"/*\"]
                                 (fn [r] 
                                   {:status 200
                                    :body \"OK\"})]})
      (mount/start #'http-server/component))
  ```"
  (:require
   [org.httpkit.server :refer [run-server]]
   [mount.core :as mount]
   [bloom.omni.impl.config :refer [config]]
   [bloom.omni.impl.handler :as handler]))

(defn- stop-server!
  [server]
  (println "Stopping HTTP server...")
  (server :timeout 100)
  (println "HTTP server stopped"))

(defn- start-server!
  [port handler]
  (println (str "Starting HTTP server..."))
  (let [server (run-server handler
                           {:port port
                            :max-body (* 100 1024 1024)})]
    (println (str "HTTP server started on http://127.0.0.1:" port "/"))
    server))

(mount/defstate component
  :start (start-server! (config :http-port) handler/handler)
  :stop (stop-server! component))
