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
    [bloom.omni.impl.crypto :as crypto]))

(defn- index-page [config]
  [:html
   [:head
    (when-let [title (get-in config [:omni/title])]
      [:title title])
    [:meta {:name "viewport"
            :content "user-scalable=no, initial-scale=1, maximum-scale=1, minimum-scale=1, width=device-width"}]
    (when (io/resource "public/manifest.webmanifest")
      [:link {:rel "manifest" :href "/manifest.webmanifest"}])
    (when (get-in config [:omni/css])
      (let [digest (crypto/sha256-file (io/resource "public/css/styles.css"))
            digest-gz (crypto/sha256-file (io/resource "public/css/styles.css.gz"))]
        [:link {:rel "stylesheet"
                :href (str "/css/styles.css?v=" digest)
                :media "screen"
                :integrity (->> [digest digest-gz]
                                (remove nil?)
                                (map (fn [d] (str "sha256-" d)))
                                (string/join " "))}]))]
   (when (get-in config [:omni/cljs])
     [:body
      [:div#app
       [:div#message {:style "display: flex; justify-content: center; align-items: center; height: 100%"}
        "This app requires Javascript. Please enable Javascript in your browser."]]
      [:script {:type "text/javascript"}
       "document.getElementById('message').outerHTML= '';"]
      (let [digest (crypto/sha256-file (io/resource "public/js/app.js"))
            digest-gz (crypto/sha256-file (io/resource "public/js/app.js.gz"))]
        [:script {:type "text/javascript"
                  :src (str "/js/app.js?v=" digest)
                  :crossorigin "anonymous"
                  :integrity (->> [digest digest-gz]
                                  (remove nil?)
                                  (map (fn [d] (str "sha256-" d)))
                                  (string/join " "))}])
      [:script {:type "text/javascript"}
       (str (string/replace (get-in config [:omni/cljs :main]) #"-" "_") ".init();")]])
   (for [script (get-in config [:omni/js-scripts])]
     [:script {:type "text/javascript"
               :src (script :src)
               :defer (script :defer)
               :async (script :async)}
      (script :body)])])

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
    (fn [req]
      (let [resource-etag (crypto/sha256-file (io/resource "public/js/app.js"))]
        (if (some-> (get-in req [:headers "if-none-match"])
                    (= resource-etag))
          {:status 304}
          (some-> (resource-response "public/js/app.js")
                  (assoc-in
                    [:headers "Cache-Control"]
                    "max-age=365000000, immutable")
                  (assoc-in [:headers "ETag"] resource-etag)))))]

   [[:get "/*"]
    (fn [r]
      (resource-response (str "public/" (-> r :params :*))))]

   [[:get "/*"]
    (fn [_]
      {:status 200
       :headers {"Content-Type" "text/html; charset=utf-8"}
       :body (html (index-page config))})]])
