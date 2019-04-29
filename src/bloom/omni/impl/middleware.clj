(ns bloom.omni.impl.middleware
  (:require
    [ring.middleware.format :refer [wrap-restful-format]]
    [ring.middleware.session.cookie :refer [cookie-store]]
    [ring.middleware.defaults :refer [wrap-defaults]]
    [bloom.omni.auth.token :as auth.token]))

(defn defaults-config
  [{:keys [production? session? cookie-secret cookie-name cookie-same-site]}]
  (when (and production? session? (nil? cookie-secret))
    (throw (Exception. (str "Must set a cookie-secret in production."))))
  (-> {:proxy production?
       :params {:keywordize true
                :urlencoded true}
       :responses {:not-modified-responses true
                   :absolute-redirects true
                   :content-types true
                   :default-charset "utf-8"}
       :security {:ssl-redirect production?
                  :frame-options :deny
                  :content-type-options :nosniff
                  :hsts production?}}
      (merge (when session?
               {:cookies true
                :session {:store (cookie-store {:key (or cookie-secret "dev-only-secret!")})
                          :cookie-name (or cookie-name "omni-app")
                          :cookie-attrs {:secure production?
                                         :http-only true
                                         :same-site (or cookie-same-site :strict)
                                         :max-age (* 60 60 24 365)}}}))))

(defn make-api-middleware
  "Returns API defaults middleware"
  [{:keys [production? session? token-secret cookie-secret cookie-name] :as opts}]
  (fn [handler]
    (-> handler
        (wrap-defaults (defaults-config opts))
        (wrap-restful-format :formats [:json :edn :yaml :transit-json]))))

(defn make-spa-middleware
  [{:keys [production? session? token-secret cookie-secret cookie-name] :as opts}]
  (fn [handler]
    (-> handler
        ((if token-secret
           (auth.token/make-token-auth-middleware token-secret)
           identity))
        (wrap-defaults (defaults-config opts)))))
