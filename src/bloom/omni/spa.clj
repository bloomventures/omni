(ns bloom.omni.spa
  "Exposes a set of omni-style routes for serving an SPA, including:
     - 'immutable' app.js
     - resource routes (server from /public/*)
     - a catch-all html file that refers app.js and styles.css with cache-busting and integrity checking"
  (:require
    [clojure.java.io :as io]
    [clojure.string :as string]
    [hiccup.core :refer [html]]
    [ring.util.response :as ring.response]
    [ring.util.mime-type :as ring.mime]
    [bloom.omni.impl.digest :as digest]))

(defn- index-page [config]
  [:html
   [:head
    (when-let [title (get-in config [:omni/title])]
      [:title title])
    [:meta {:name "viewport"
            :content "user-scalable=no, initial-scale=1, maximum-scale=1, minimum-scale=1, width=device-width"}]
    (when (get-in config [:omni/css])
      (let [digest (digest/from-file (io/resource "public/css/styles.css"))]
        [:link {:rel "stylesheet"
                :href (str "/css/styles.css?v=" digest)
                :media "screen"
                :integrity (str "sha256-" digest)}]))]
   (when (get-in config [:omni/cljs])
     [:body
      [:div#app
       [:div#message {:style "display: flex; justify-content: center; align-items: center; height: 100%"}
        "This app requires Javascript. Please enable Javascript in your browser."]]
      [:script {:type "text/javascript"}
       "document.getElementById('message').outerHTML= '';"]
      (let [digest (digest/from-file (io/resource "public/js/app.js"))]
        [:script {:type "text/javascript"
                  :src (str "/js/app.js?v=" digest)
                  :crossorigin "anonymous"
                  :integrity (str "sha256-" digest)}])
      [:script {:type "text/javascript"}
       (str (string/replace (get-in config [:omni/cljs :main]) #"-" "_") ".init();")]])])

(defn- add-mime-type [response path]
  (if-let [mime-type (ring.mime/ext-mime-type path)]
    (ring.response/content-type response mime-type)
    response))

(defn- resource-response [path]
  (some->
    (ring.response/resource-response path)
    (add-mime-type path)))

(defn routes [config]
  [[[:get "/js/app.js"]
    (fn [_]
      (some-> (resource-response "public/js/app.js")
              (assoc-in
                [:headers "Cache-Control"]
                "max-age=365000000, immutable")))]

   [[:get "/*"]
    (fn [r]
      (resource-response (str "public/" (-> r :params :*))))]

   [[:get "/*"]
    (fn [_]
      {:status 200
       :headers {"Content-Type" "text/html; charset=utf-8"}
       :body (html (index-page config))})]])
