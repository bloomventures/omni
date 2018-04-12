(ns bloom.omni.impl.cssbuild
  (:require
    [garden.core :as garden]))

(defn mkdir [output-to]
  (.mkdir (java.io.File. (.getParent (java.io.File. output-to)))))

(defn compile-css [{:keys [pretty-print? styles]}]
  (require (symbol (namespace (keyword styles))) :reload)
  (garden/css 
    {:pretty-print? pretty-print?}
    ((var-get (find-var (symbol styles))))))

(defn compile! [{:keys [output-to] :as css-config}]
  (mkdir output-to)
  (spit output-to (compile-css css-config)))
