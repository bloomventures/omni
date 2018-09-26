(ns bloom.omni.auth.oauth.google
  (:require
    [clojure.data.json :as json]
    [hiccup.core :as hiccup]
    [org.httpkit.client :as http]
    [ring.util.codec :refer [form-encode]]))

(defn html []
  (hiccup/html
    [:html
     [:body
      [:script
       "var token = decodeURIComponent(window.location.toString().match(/access_token=(.*)&/, '')[1]);"
       "window.opener.postMessage(token, window.location);"
       "window.close();"]]]))

(defn request-token-url [oauth-config]
  (str "https://accounts.google.com/o/oauth2/v2/auth?"
       (form-encode {:response_type "token"
                     :client_id (get-in oauth-config [:google :client-id])
                     :redirect_uri (str (get-in oauth-config [:google :domain]) "/api/auth/post-auth")
                     :scope "email profile"})))

(defn- valid-token? [oauth-config token]
  (let [resp (-> @(http/request
                    {:method :get
                     :url (str "https://www.googleapis.com/oauth2/v3/tokeninfo?access_token=" token)})
                 :body
                 (json/read-str :key-fn keyword))]
    (= (resp :aud)
       (get-in oauth-config [:google :client-id]))))

(defn get-user-info [oauth-config token]
  (when (valid-token? oauth-config token)
    (let [resp (-> @(http/request
                      {:method :get
                       :url (str "https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=" token)})
                   :body
                   (json/read-str :key-fn keyword))]
      {:id (resp :email)
       :name (resp :name)
       :email (resp :email)
       :avatar (resp :picture)})))

