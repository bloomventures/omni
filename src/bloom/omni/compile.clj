(ns bloom.omni.compile
  (:require
    [bloom.omni.impl.builds :refer [builds]]
    [bloom.omni.impl.gzip :as gzip]
    [bloom.omni.impl.cljsbuild :as cljsbuild]
    [bloom.omni.impl.config :as config]
    [bloom.omni.impl.cssbuild :as cssbuild]))

(defn compile-css! [c]
  (cssbuild/compile! (:css (config/fill c)))
  (gzip/compress (get-in (config/fill c) [:css :output-to])))
    
(defn compile-js! [c]
  (let [prod-build (->> (builds (config/fill c))
                        (filter (fn [b]
                                  (= "prod" (b :id))))
                        first)]
    (cljsbuild/compile! prod-build)
    (gzip/compress (get-in prod-build [:compiler :output-to]))))

(defn compile! [c]
  (compile-css! c)
  (compile-js! c))
