(ns bloom.omni.css-watcher
  "Provides a component that watches the src directory for changes to cljc files and compiles garden to css.

  Requires `{:css {:main \"...\"}}` to be set in `omni.config.edn`

  ```clojure
  (require '[bloom.omni.css-watcher :as css-watcher])
  (css-watcher/start!)
  ```"
  (:require
    [clojure.string :as string]
    [hawk.core :as hawk]
    [bloom.omni.impl.css :as css]
    [bloom.omni.impl.cssbuild :as cssbuild]
    [bloom.omni.impl.async :as async]))

(def previous-output-hash (atom nil))

(def compile!
  ; debounce, b/c some editors trigger multiple events on a file
  ; causing figwheel to send multiple css files, which is janky
  ; just filtering for :modify events isn't sufficient
  (async/debounce
    (fn [{:keys [output-to] :as css-config}]
      (try
        (do
          (cssbuild/mkdirs output-to)
          (let [output (cssbuild/compile-css css-config)
                output-hash (hash output)]
            (when (not= output-hash @previous-output-hash)
              (spit output-to output)
              (reset! previous-output-hash output-hash))))
        (catch Exception e
          (println "Unable to compile CSS due to error.")
          (println e))))
    50))

(defn stop! [watcher]
  (println "Stopping CSS watcher...")
  (hawk/stop! watcher))

(defn start! [css-config]
  (println "Starting CSS watcher...")
  (reset! previous-output-hash nil)
  (let [css-config (merge css-config
                          {:pretty-print? true})]

    (compile! css-config)

    (hawk/watch! [{:paths ["src"]
                   :handler
                   (fn [_ {:keys [kind file]}]
                     (when (and
                             (.isFile file)
                             (string/ends-with? (.getName file) "cljc"))
                       (compile! css-config))
                     nil)}])))
