;; From: https://github.com/http-kit/http-kit/commit/04bf72bfce25f0bdffd04548930693ecd6d3f5aa
;; No longer necessary when http-kit 2.4.0 lands

(ns org.httpkit.sni-client
  "Provides an SNI-capable SSL configurer and client, Ref. #335.
  In a separate namespace from `org.httpkit.client` so that
  http-kit can retain backwards-compatibility with JVM < 8."
  (:require
    [org.httpkit.client])
  (:import
    [java.net URI]
    [javax.net.ssl SNIHostName SSLEngine]))

(defn ssl-configurer
  "SNI-capable SSL configurer.
  May be used as an argument to `org.httpkit.client/make-client`:
    (make-client :ssl-configurer (ssl-configurer))"
  [^SSLEngine ssl-engine ^URI uri]
  (let [host-name  (SNIHostName. (.getHost uri))
        ssl-params (doto (.getSSLParameters ssl-engine)
                     (.setServerNames [host-name]))]
    (doto ssl-engine
      (.setUseClientMode true) ; required for JVM 12/13 but not for JVM 8
      (.setSSLParameters ssl-params))))

(defonce
  ^{:doc "Like `org.httpkit.client/default-client`, but provides SNI support using `ssl-configurer`"}
  default-client
  (org.httpkit.client/make-client
    {:ssl-configurer ssl-configurer}))
