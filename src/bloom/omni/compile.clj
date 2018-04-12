(ns bloom.omni.compile
  (:require
    [mount.core :as mount]
    [bloom.omni.impl.cljsbuild :as cljsbuild]
    [bloom.omni.impl.cssbuild :as cssbuild]
    [bloom.omni.impl.builds :refer [builds]]
    [bloom.omni.impl.config :refer [config]]))

(mount/defstate compile-css! 
  :start (cssbuild/compile! (:css config)))
    
(mount/defstate compile-js! 
  :start (cljsbuild/compile! (last builds)))
