(ns bloom.omni.figwheel
  "Provides a component that starts/stops figwheel.

  ```clojure
  (require '[bloom.omni.figwheel :as figwheel])
  (figwheel/start! {:figwheel-port 1234
                    :cljs {:main \"app.core\"}})
  ```"
  (:require
    [figwheel-sidecar.repl-api :as repl-api]
    [bloom.omni.impl.builds :refer [builds]]))

(defn start! [{:keys [cljs-main server-port]}]
  (repl-api/start-figwheel!
    {:figwheel-options {:server-port server-port
                        :css-dirs ["resources/public/css"]}
     :build-ids ["dev"]
     :all-builds (builds cljs-main)}))

(defn stop! []
  (repl-api/stop-figwheel!))
