(ns bloom.omni.env
  (:require
    [clojure.string :as string]))

(defn env [k]
  (let [k (-> (if (keyword? k)
                (name k)
                k)
              string/upper-case
              (string/replace "-" "_"))]
    (System/getenv k)))
