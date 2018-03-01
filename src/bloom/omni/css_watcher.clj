(ns bloom.omni.css-watcher
  "Provides a mount component that watches the src directory for changes to cljc files and compiles garden to css. 

  Requires `{:css {:main \"...\"}}` to be set in `omni.config.edn`
  
  ```clojure
  (require '[bloom.omni.css-watcher :as css-watcher])
  (mount/start #'css-watcher/component)
  ```"
  (:require
    [clojure.string :as string]
    [hawk.core :as hawk]
    [mount.core :as mount]
    [bloom.omni.impl.config :refer [config]]
    [bloom.omni.impl.css :as css]
    [bloom.omni.impl.async :as async]))

(defn- stop-watcher! [watcher]
  (println "Stopping CSS watcher...")
  (hawk/stop! watcher))

(defn- start-watcher! [css-config]
  (println "Starting CSS watcher...")
  (let [css-config (merge css-config
                          {:pretty-print? true})
        ; debounce, b/c some editors trigger multiple events on a file
        ; causing figwheel to send multiple css files, which is janky
        ; just filtering for :modify events isn't sufficient
        compile! (async/debounce 
                   #(css/compile! css-config)
                   50)]

    (compile!)
     
    (hawk/watch! [{:paths ["src"]
                   :handler
                   (fn [_ {:keys [kind file]}]
                     (when (and 
                             (.isFile file)
                             (string/ends-with? (.getName file) "cljc"))
                       (compile!))
                     nil)}])))

(mount/defstate component
  :start (start-watcher! (config :css))
  :stop (stop-watcher! component))
