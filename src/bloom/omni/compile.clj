(ns bloom.omni.compile
  (:require
    [clojure.java.io :as io]
    [bloom.omni.impl.builds :refer [builds]]
    [bloom.omni.impl.gzip :as gzip]
    [bloom.omni.impl.cljsbuild :as cljsbuild]
    [bloom.omni.impl.config :as config]
    [bloom.omni.impl.cssbuild :as cssbuild]))

(defn touch [& paths]
  (let [t (System/currentTimeMillis)]
    (doseq [path paths]
      (.setLastModified (io/file path) t))))

(defn compile-css! [config]
  (when (config :omni/css)
    (let [path "resources/public/css/styles.css"]
      (cssbuild/compile! {:styles (get-in config [:omni/css :styles])
                          :output-to path
                          :pretty-print? false})
      (gzip/compress path)
      (touch path (str path ".gz")))))
    
(defn compile-js! [config]
  (when (config :omni/cljs)
    (let [prod-build (->> (builds (get-in config [:omni/cljs :main]))
                          (filter (fn [b]
                                    (= "prod" (b :id))))
                          first)
          path "resources/public/js/app.js"]
      (cljsbuild/compile! prod-build)
      (gzip/compress path)
      (touch path (str path ".gz")))))

(defn compile! [config]
  (let [config (config/read config)]
    (compile-css! config)
    (compile-js! config)))
