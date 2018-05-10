(ns bloom.omni.compile
  (:require
    [bloom.omni.impl.cljsbuild :as cljsbuild]
    [bloom.omni.impl.cssbuild :as cssbuild]
    [bloom.omni.impl.builds :refer [builds]]
    [bloom.omni.impl.config :as config]))

(defn compile-css! [c]
  (cssbuild/compile! (:css (config/fill c))))
    
(defn compile-js! [c]
  (cljsbuild/compile! (last (builds (config/fill c)))))

(defn compile! [c]
  (compile-css! c)
  (compile-js! c))
