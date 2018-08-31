(ns bloom.omni.impl.middleware
  (:require
    [ring.middleware.format :refer [wrap-restful-format]]
    [ring.middleware.session.cookie :refer [cookie-store]]
    [ring.middleware.defaults :refer [wrap-defaults]]))

(defn defaults-config
  [{:keys [production? session? cookie-name cookie-secret]}]
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
                                         :same-site :strict
                                         :max-age (* 60 60 24 365)}}}))))

(defn api
  "Returns API defaults middleware"
  [{:keys [production? session? cookie-secret cookie-name]}]
  (fn [handler]
    (-> handler
        (wrap-defaults (defaults-config {:production? production?
                                         :session? session?
                                         :cookie-secret cookie-secret
                                         :cookie-name cookie-name}))
        (wrap-restful-format :formats [:json :edn :yaml :transit-json]))))
