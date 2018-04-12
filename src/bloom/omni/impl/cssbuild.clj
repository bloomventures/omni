(ns bloom.omni.impl.cssbuild
  (:require
    [garden.core :as garden]))

(defn compile-css [{:keys [pretty-print? styles]}]
  (require (symbol (namespace (keyword styles))) :reload)
  (garden/css 
    {:pretty-print? pretty-print?}
    ((var-get (find-var (symbol styles))))))

(defn compile! [{:keys [output-to] :as css-config}]
  (spit output-to (compile-css css-config)))
