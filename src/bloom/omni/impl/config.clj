(ns bloom.omni.impl.config
  (:refer-clojure :exclude [read])
  (:require
    [clojure.spec.alpha :as s]
    [spec-tools.data-spec :as ds]))

(def config-spec
  (ds/spec
    {:name :omni/config
     :spec {(ds/opt :omni/title) string?
            (ds/opt :omni/css) {:styles string?}
            (ds/opt :omni/cljs) {:main string?
                                 (ds/opt :externs) [string?]}
            (ds/opt :omni/http-port) integer?
            (ds/opt :omni/environment) (s/spec #{:prod :dev})
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

(defn read [config]
  (if (s/valid? config-spec config)
      config
      (throw (Exception. (str "Config Invalid: "
                              (s/explain config-spec config))))))
