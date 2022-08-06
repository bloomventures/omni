(ns bloom.omni.css-watcher
  "Provides a component that watches the src directory for changes to cljc files and compiles garden to css.

  Requires `{:css {:main \"...\"}}` to be set in `omni.config.edn`

  ```clojure
  (require '[bloom.omni.css-watcher :as css-watcher])
  (css-watcher/start!)
  ```"
  (:require
    [clojure.string :as string]
    [nextjournal.beholder :as beholder]
    [bloom.omni.impl.cssbuild :as cssbuild]
    [bloom.omni.impl.async :as async])
  (:import
   (java.nio.file Path)))

(def previous-output-hash (atom nil))

(def compile!
  ; debounce, b/c some editors trigger multiple events on a file
  ; causing figwheel to send multiple css files, which is janky
  ; just filtering for :modify events isn't sufficient
  (async/debounce
    (fn [{:keys [output-to] :as css-config}]
      (try
        (cssbuild/mkdirs output-to)
        (let [output (cssbuild/compile-css css-config)
              output-hash (hash output)]
          (when (not= output-hash @previous-output-hash)
            (spit output-to output)
            (reset! previous-output-hash output-hash)))
        (catch Throwable e
          (println "Unable to compile CSS due to error.")
          (println e))))
    50))

(defn stop! [watcher]
  (println "Stopping CSS watcher...")
  (beholder/stop watcher))

(defn start! [css-config]
  (println "Starting CSS watcher...")
  (reset! previous-output-hash nil)
  (let [css-config (merge css-config
                          {:pretty-print? true})]

    (compile! css-config)

    (beholder/watch
      (fn [{:keys [^Path path]}]
        (let [file (.toFile path)]
          (when (and
                  (.isFile file)
                  (string/ends-with? (.getName file) "cljc"))
            (compile! css-config)))
        nil)
      "src")))
