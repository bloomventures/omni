(ns bloom.omni.impl.config
  (:refer-clojure :exclude [read])
  (:require
    [clojure.spec.alpha :as s]
    [clojure.java.io :as io]
    [spec-tools.data-spec :as ds]
    [bloom.omni.env :refer [env]]))

(defn- deep-merge [& args]
  (apply merge-with
    (fn [a b]
      (cond
        (map? a) (deep-merge a b)
        (vector? a) (concat a b)
        :else b))
    args))

(def config-spec
  (ds/spec
    {:name :omni/config
     :spec {(ds/opt :omni/title) string?
            (ds/opt :omni/css) {:styles string?}
            (ds/opt :omni/cljs) {:main string?}
            (ds/opt :omni/http-port) integer?
            (ds/opt :omni/environment) keyword?
            (ds/opt :omni/auth) {(ds/opt :cookie) {;; a temporary one is used in dev
                                                   (ds/opt :secret)
                                                   (fn [s]
                                                     (and
                                                       (string? s)
                                                       (= 16 (count s))))
                                                   (ds/opt :name) string?}
                                 (ds/opt :token) {:secret string?}
                                 (ds/opt :oauth) {:google {:client-id string?
                                                           :domain string?}}
                                 (ds/opt :post-auth-fn) fn?
                                 (ds/opt :get-user-fn) fn?}
            (ds/opt :omni/api-routes) var?}}))

(defn- config-from-env []
  (deep-merge {}
              (when-let [port (some-> (env :http-port)
                                      (Integer/parseInt))]
                {:omni/http-port port})
              (when-let [environment (some-> (env :environment)
                                             keyword)]
                {:omni/environment environment})
              (when-let [cookie-secret (env :cookie-secret)]
                {:omni/auth {:cookie {:secret cookie-secret}}})
              (when-let [token-secret (env :token-secret)]
                {:omni/auth {:token {:secret token-secret}}})
              (when-let [domain (env :domain)]
                {:omni/auth {:oauth {:google {:domain domain}}}})
              (when-let [client-id (env :client-id)]
                {:omni/auth {:oauth {:google {:client-id client-id}}}})))

(defn- config-from-file []
  (let [path "config.edn"]
    (if (.exists (io/file path))
      (->> path
           slurp
           read-string)
      {})))

(defn fill [config]
  (deep-merge (config-from-file)
              (config-from-env)
              config))

(defn read [config]
  (let [config (fill config)]
    (if (s/valid? config-spec config)
      config
      (throw (Exception. (str "Config Invalid: "
                              (s/explain config-spec config)))))))
