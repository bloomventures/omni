(ns bloom.omni.impl.config
  (:refer-clojure :exclude [read])
  (:require
    [clojure.spec.alpha :as s]
    [clojure.java.io :as io]
    [bloom.omni.env :refer [env]]
    [spec-tools.data-spec :as ds]))

(def config-spec 
  (ds/spec
    {:name :omni/config
     :spec {(ds/opt :omni/title) string?
            (ds/opt :omni/css) {:styles string?}
            (ds/opt :omni/cljs) {:main string?}
            (ds/opt :omni/http-port) integer?
            (ds/opt :omni/environment) keyword?
            (ds/opt :omni/api-routes) vector?}}))

(defn- config-from-env []
  (merge {}
         (if-let [port (some-> (env :http-port)
                               (Integer/parseInt))] 
           {:omni/http-port port}
           {})
         (if-let [environment (some-> (env :environment)
                                      keyword)] 
           {:omni/environment environment}
           {})))

(defn- config-from-file [] 
  (let [path "config.edn"]
    (if (.exists (io/file path)) 
      (->> path 
           slurp 
           read-string)
      {}))) 

(defn fill [config]
  (merge-with (fn [a b]
                (cond
                  (map? a)
                  (merge a b)
                  (vector? a)
                  (concat a b)
                  :else
                  b))
              (config-from-file)
              (config-from-env)
              config))

(defn read [config]
  (let [config (fill config)]
    (if (s/valid? config-spec config)
      config
      (throw (Exception. (str "Config Invalid: "
                              (s/explain config-spec config)))))))
