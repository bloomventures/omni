(ns bloom.omni.http-server
  "Provides a component that starts/stop an http-kit server.

  ```clojure
  (require '[bloom.omni.http-server :as http-server])
  (http-server/start! 1523 handler)
  ```"
  (:require
    [org.httpkit.client]
    [org.httpkit.sni-client]
    [org.httpkit.server :refer [run-server]]))

;; because we often use http-kit as our http-client
;; including this so that SNI works
(alter-var-root #'org.httpkit.client/*default-client*
                (fn [_] org.httpkit.sni-client/default-client))

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
