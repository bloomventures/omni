(ns bloom.omni.env
  (:refer-clojure :exclude [get])
  (:require
    [clojure.java.io :as io]
    [clojure.string :as string]))

(defn- from-file [k]
  (when (.exists (io/file "config.edn")) 
    (->> "config.edn" 
         slurp 
         read-string
         k)))

(defn- from-env [k]
  (let [k (-> (if (keyword? k)
                (name k)
                k)
              string/upper-case
              (string/replace "-" "_"))]
    (System/getenv k)))

(defn env [k]
  (from-env k))

(defn get [k]
  (or (from-env k)
      (from-file k)))
