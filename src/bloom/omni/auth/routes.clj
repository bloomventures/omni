(ns bloom.omni.auth.routes
  (:require
    [bloom.omni.auth.google :as oauth]
    [bloom.omni.impl.ring :as ring]))

(defn routes [config]
  [
   [[:get "/api/auth/user"]
    (fn [request]
      {:status 200
       :body (if-let [user-id (get-in request [:session :user-id])]
               {:user {:id user-id}}
               {:user nil})})]

   [[:get "/api/auth/request-token"]
    (fn [_]
      {:status 302
       :body {:ok true}
       :headers {"Location" (oauth/request-token-url config)}})]

   [[:get "/api/auth/post-auth"]
    (fn [_]
      {:status 200
       :headers {"Content-Type" "text/html"}
       :body (oauth/html)})]

   [[:put "/api/auth/authenticate"]
    (fn [request]
      (let [token (get-in request [:params :token])]
        (if-let [user (oauth/get-user-info config token)]
          {:status 200
           :body {:user {:id (user :id)}}
           :session {:user-id (user :id)}}
          {:status 401
           :body {:error "User could not be authenticated"}})))]

   [[:delete "/api/auth"]
    (fn [_]
      {:status 200
       :body {:ok true}
       :session nil})]])
