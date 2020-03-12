(ns bloom.omni.auth.oauth.routes
  (:require
    [bloom.omni.auth.oauth.google :as oauth]
    [bloom.omni.impl.ring :as ring]))

(defn routes [oauth-config]
  [
   [[:get "/api/auth/user"]
    (fn [request]
      {:status 200
       :body (if-let [session-id (get-in request [:session :id])]
               (let [user-from-session-id (or (oauth-config :user-from-session-id)
                                              (fn [session-id] {:id session-id}))]
                 {:user (user-from-session-id session-id)})
               {:user nil})})]

   [[:get "/api/auth/request-token"]
    (fn [_]
      {:status 302
       :body {:ok true}
       :headers {"Location" (oauth/request-token-url oauth-config)}})]

   [[:get "/api/auth/post-auth"]
    (fn [_]
      {:status 200
       :headers {"Content-Type" "text/html"}
       :body (oauth/html)})]

   [[:put "/api/auth/authenticate"]
    (fn [request]
      (let [token (get-in request [:body-params :token])]
        (if-let [user-info (oauth/get-user-info oauth-config token)]
          (let [post-auth! (or (oauth-config :post-auth-fn)
                               (fn [user]))
                user-to-session-id (or (oauth-config :user-to-session-id)
                                       :id)]
            (post-auth! user-info)
            {:status 200
             :body {:ok true}
             :session {:id (user-to-session-id user-info)}})
          {:status 401
           :body {:error "User could not be authenticated"}})))]

   [[:delete "/api/auth"]
    (fn [_]
      {:status 200
       :body {:ok true}
       :session nil})]])
