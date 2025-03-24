(ns bloom.omni.figwheel
  "Provides a component that starts/stops figwheel.

  ```clojure
  (require '[bloom.omni.figwheel :as figwheel])
  (figwheel/start! {:figwheel-port 1234
                    :cljs {:main \"app.core\"}})
  ```"
  (:require
    [clojure.java.io :as io]
    [figwheel.main.api :as repl-api]
    [bloom.omni.impl.builds :refer [builds]]))

(defn start! [{:keys [cljs-opts server-port css?]}]
  (let [[build] (filter #(= "dev" (:id %)) (builds (assoc cljs-opts :css? css?)))]
    ;; figwheel watch throws if directory does not exist
    (when-let [css-dir (-> build :figwheel :css-dirs first)]
      (.mkdirs (io/file css-dir)))
    (repl-api/start
     {:id (:id build)
      :options (:compiler build)
      :config (assoc (:figwheel build)
                :ring-server-options {:port server-port})})))

(defn stop! []
  (repl-api/stop-all))
