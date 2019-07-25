(ns bloom.omni.auth.token
  "Use:
    token-auth-middeware
    login-query-string

  Expects :session to be available
  Stores :user-id to session.
  Expects user-id to be a UUID."
  (:require
    [java-time :as java-time]
    [clojure.string :as string]
    [bloom.omni.impl.crypto :as crypto])
  (:import
    (java.net URLEncoder)
    (org.apache.commons.codec.binary Base64)))

(defn- map->query-str
  [m]
  (->> m
       (map (fn [[k v]] (str (name k) "=" (URLEncoder/encode (str v) "UTF-8"))))
       (string/join "&")))

(defn- generate-login-token
  [user-id secret]
  (let [expiry (java-time/to-millis-from-epoch
                 (java-time/plus (java-time/instant) (java-time/weeks 1)))]
    {:user-id user-id
     :expiry expiry
     :mac (->> (str user-id expiry)
               crypto/str->bytes
               (crypto/hmac-sha256 (crypto/str->bytes secret))
               Base64/encodeBase64URLSafeString)}))

(defn login-query-string
  [user-id secret]
  (map->query-str (generate-login-token user-id secret)))

(defn verify-request-mac
  [{:keys [user-id expiry mac]} secret]
  (crypto/slow=
    (Base64/decodeBase64 mac)
    (crypto/hmac-sha256 (crypto/str->bytes secret)
                        (crypto/str->bytes (str user-id expiry)))))

(defn verify-request-expiry
  [expiry]
  (java-time/before? (java-time/instant) (java-time/instant expiry)))

(defn make-token-auth-middleware
  [secret]
  (fn [handler]
    (fn [request]
      (let [{:strs [user-id expiry mac]} (request :query-params)
            target-url (let [query-str (map->query-str (dissoc (request :query-params)
                                                               "user-id"
                                                               "expiry"
                                                               "mac"))]
                         (str (request :uri)
                              (when-not (string/blank? query-str)
                                (str "?" query-str))))]
        (cond
          (not (and user-id expiry mac))
          (handler request)

          (not (verify-request-mac {:user-id user-id
                                    :expiry expiry
                                    :mac mac}
                                   secret))
          {:status 400
           :headers {"Content-Type" "text/plain"}
           :body "Login link has been tampered with."}

          (not (verify-request-expiry (Long. expiry)))
          {:status 403
           :headers {"Content-Type" "text/html"}
           :body (str "Your login link has expired. Redirecting... <script>setTimeout(function(){ window.location = '" target-url "';}, 2000)</script>")}

          :else
          {:session {:user-id (java.util.UUID/fromString user-id)}
           :status 302
           :headers {"Location" target-url}})))))

