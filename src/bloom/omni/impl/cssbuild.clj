(ns bloom.omni.impl.cssbuild
  (:require
    [garden.core :as garden]))

(defn mkdirs [output-to]
  (.mkdirs (java.io.File. (.getParent (java.io.File. output-to)))))

(defn compile-css [{:keys [pretty-print? styles]}]
  (require (symbol (namespace (keyword styles))) :reload)
  (garden/css
    {:pretty-print? pretty-print?
     :vendors ["webkit" "moz" "ms"]
     :auto-prefix #{:user-select}}
    ((var-get (find-var (symbol styles))))))

(defn compile! [{:keys [output-to] :as css-config}]
  (mkdirs output-to)
  (spit output-to (compile-css css-config)))
