(ns bloom.omni.figwheel
  "Provides a component that starts/stops figwheel.

  ```clojure
  (require '[bloom.omni.figwheel :as figwheel])
  (figwheel/start! {:figwheel-port 1234
                    :cljs {:main \"app.core\"}})
  ```"
  (:require
    [figwheel.main.api :as repl-api]
    [bloom.omni.impl.builds :refer [builds]]))

(defn start! [{:keys [cljs-opts server-port]}]
  (let [[build] (filter #(= "dev" (:id %)) (builds cljs-opts))]
    (repl-api/start
      {:id (:id build)
       :options (:compiler build)
       :config (assoc (:figwheel build)
                      :ring-server-options {:port server-port})})))

(defn stop! []
  (repl-api/stop-all))
