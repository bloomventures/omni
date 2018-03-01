(ns bloom.omni.impl.spa
  (:require
    [compojure.core :refer [GET defroutes]]
    [compojure.route :as route]
    [hiccup.core :refer [html]]
    [ring.util.response :refer [resource-response]]
    [bloom.omni.impl.digest :as digest]
    [bloom.omni.impl.config :refer [config]]))

(defn- index-page []
  (let [title (get-in config [:title])
        main (get-in config [:cljs :main])]
    [:html
     [:head
      [:title title]
      (let [digest (digest/from-file "public/css/styles.css")]
        [:link {:rel "stylesheet" 
                :href (str "/css/styles.css?v=" digest) 
                :media "screen" 
                :integrity (str "sha256-" digest)}])]
     [:body
      [:div#app]
      (let [digest (digest/from-file "public/js/app.js")]
        [:script {:type "text/javascript"
                  :src (str "/js/app.js?v=" digest)
                  :crossorigin "anonymous"
                  :integrity (str "sha256-" digest)}])
      [:script {:type "text/javascript"}
       (str main ".init();")]]]))

(defroutes routes
  
  (GET "/js/app.js" _
    (when-let [response (resource-response "public/js/app.js")]
      (assoc-in response [:headers "Cache-Control"] "max-age=365000000, immutable")))

  (route/resources "/")

  (GET "/*" []
    {:status 200
     :headers {"Content-Type" "text/html; charset=utf-8"}
     :body (html (index-page))}))
