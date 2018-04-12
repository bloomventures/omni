(ns bloom.omni.figwheel
  "Provides a mount component that starts/stops figwheel.
  
  ```clojure
  (require '[bloom.omni.figwheel :as figwheel])
  (mount/start #'figwheel/component)
  ```"
  (:require 
    [mount.core :as mount]
    [figwheel-sidecar.repl-api :as repl-api]
    [bloom.omni.impl.builds :refer [builds]]
    [bloom.omni.impl.config :refer [config]]))

(defn- start! [config]
  (repl-api/start-figwheel! 
    {:figwheel-options {:server-port (get-in config [:figwheel-port])
                        :css-dirs ["resources/public/css"]}
     :build-ids ["dev"]
     :all-builds builds}))

(defn- stop! []
  (repl-api/stop-figwheel!))

(mount/defstate component
  :start (start! config)
  :stop (stop!))
