(ns bloom.omni.auth.google
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

(defn request-token-url [config]
  (str "https://accounts.google.com/o/oauth2/v2/auth?"
       (form-encode {:response_type "token"
                     :client_id (get-in config [:omni/auth :google :client-id])
                     :redirect_uri (str (get-in config [:omni/auth :google :domain]) "/api/auth/post-auth")
                     :scope "email profile"})))

(defn- valid-token? [config token]
  (let [resp (-> @(http/request
                    {:method :get
                     :url (str "https://www.googleapis.com/oauth2/v3/tokeninfo?access_token=" token)})
                 :body
                 (json/read-str :key-fn keyword))]
    (= (resp :aud) 
       (get-in config [:omni/auth :google :client-id]))))

(defn get-user-info [config token]
  (when (valid-token? config token)
    (let [resp (-> @(http/request
                      {:method :get
                       :url (str "https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=" token)})
                   :body
                   (json/read-str :key-fn keyword))]
      {:id (resp :email)
       :name (resp :name)
       :email (resp :email)
       :avatar (resp :picture)})))

