(ns bloom.omni.figwheel
  "Provides a mount system that starts/stops figwheel.
  
  ```clojure
  (require '[bloom.omni.figwheel :as figwheel])
  (mount/start #'figwheel/system)
  ```"
  (:require 
    [mount.core :as mount]
    [figwheel-sidecar.repl-api :as repl-api]
    [bloom.omni.impl.config :refer [config]]))

(defn- start! [config]
  (let [main (get-in config [:cljs :main])
        port (get-in config [:figwheel-port])]
    (repl-api/start-figwheel! 
      {:figwheel-options {:server-port port
                          :css-dirs ["resources/public/css"]}
       :build-ids ["dev"]
       :all-builds
       [{:id "dev"
         :figwheel {:on-jsload (str main "/reload")}
         :source-paths ["src"]
         :compiler {:main main
                    :asset-path "/js/dev"
                    :output-to "resources/public/js/app.js"
                    :output-dir "resources/public/js/dev"
                    :verbose true}}]})))

(defn- stop! []
  (repl-api/stop-figwheel!))

(mount/defstate system
  :start (start! config)
  :stop (stop!))
