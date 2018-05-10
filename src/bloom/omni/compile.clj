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

(defn compile-css! [c]
  (let [path (get-in (config/fill c) [:css :output-to])]
    (cssbuild/compile! (:css (config/fill c)))
    (gzip/compress path)
    (touch path (str path ".gz"))))
    
(defn compile-js! [c]
  (let [prod-build (->> (builds (config/fill c))
                        (filter (fn [b]
                                  (= "prod" (b :id))))
                        first)
        path (get-in prod-build [:compiler :output-to])]
    (cljsbuild/compile! prod-build)
    (gzip/compress path)
    (touch path (str path ".gz"))))

(defn compile! [c]
  (compile-css! c)
  (compile-js! c))
