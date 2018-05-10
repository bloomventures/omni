(ns bloom.omni.impl.config
  (:require
    [clojure.java.io :as io]))

(def base-config
  {:title "Omni App"
   :css {:output-to "resources/public/css/styles.css"}
   :figwheel-port 5123
   :http-port 6123})

(defn- parse [path]
  (if (.exists (io/file path))
    (do
      (println "Reading omni config from " path)
      (->> path
           slurp
           read-string))
    (println "No omni.config.edn found; using defaults.")))

(defn fill [config]
  (merge-with (fn [a b]
                (cond
                  (map? a)
                  (merge a b)
                  (vector? a)
                  (concat a b)
                  :else
                  b))
              base-config
              (parse "omni.config.edn")
              config))
