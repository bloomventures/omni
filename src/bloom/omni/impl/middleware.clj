(ns bloom.omni.impl.middleware
  (:require
    [muuntaja.middleware :refer [wrap-format]]
    [ring.middleware.session.cookie :refer [cookie-store]]
    [ring.middleware.defaults :refer [wrap-defaults]]
    [bloom.omni.auth.token :as auth.token]))

(defn defaults-config
  [{:keys [production? session? cookie-secret cookie-name cookie-same-site
           frame-options]}]
  (when (and production? session? (nil? cookie-secret))
    (throw (Exception. (str "Must set a cookie-secret in production."))))
  (-> {:proxy production?
       :params {:keywordize true
                :urlencoded true}
       :responses {:not-modified-responses true
                   :absolute-redirects true
                   :content-types true
                   :default-charset "utf-8"}
       :security (cond-> {:ssl-redirect production?
                          :content-type-options :nosniff
                          :hsts production?}
                   frame-options (assoc :frame-options frame-options))}
      (merge (when session?
               {:cookies true
                :session {:store (cookie-store {:key (or cookie-secret "dev-only-secret!")})
                          :cookie-name (or cookie-name "omni-app")
                          :cookie-attrs (merge
                                          {:secure production?
                                           :http-only true
                                           :max-age (* 60 60 24 365)}
                                          ;; We need the ability to omit same-site
                                          ;; attribute completely since lax != omitted!
                                          (when (not= false cookie-same-site)
                                            {:same-site (or cookie-same-site :strict)}))}}))))

(defn wrap-frames-csp
  [handler frame-options]
  (if-let [domain (:allow-from frame-options)]
    (fn [req]
      (when-let [resp (handler req)]
        (assoc-in resp
                  [:headers "Content-Security-Policy"]
                  (str "frame-ancestors " domain))))
    handler))

(defn make-api-middleware
  "Returns API defaults middleware"
  [{:keys [production? session? token-secret cookie-secret cookie-name frame-options] :as opts}]
  (fn [handler]
    (-> handler
        ((if token-secret
           (auth.token/make-token-auth-middleware token-secret)
           identity))
        (wrap-format)
        (wrap-frames-csp frame-options)
        (wrap-defaults (defaults-config opts)))))

(defn make-spa-middleware
  [{:keys [production? session? token-secret cookie-secret cookie-name frame-options] :as opts}]
  (fn [handler]
    (-> handler
        ((if token-secret
           (auth.token/make-token-auth-middleware token-secret)
           identity))
        (wrap-frames-csp frame-options)
        (wrap-defaults (defaults-config opts)))))
