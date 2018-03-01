(ns bloom.omni.impl.config
  (:require
    [mount.core :as mount]))

(def base-config
  {:title "Omni App"
   :css {:output-to "resources/public/css/styles.css"}
   :figwheel-port 5123
   :http-port 6123})

(defn parse [path]
  (println "Reading omni config from " path)
  (->> path
       slurp
       read-string
       (merge-with merge base-config)))

(mount/defstate config
  :start (parse "omni.config.edn"))
