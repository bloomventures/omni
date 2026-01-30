# Omni

[![Clojars Project](https://img.shields.io/clojars/v/io.bloomventures/omni.svg)](https://clojars.org/io.bloomventures/omni)

Omni is a Clojure(Script) single-page app web framework, used by Bloom Ventures for most of its web apps.

It is not meant for public consumption:

  - it is inflexible (minimal configuration outside of the what we want it to do)
  - it's got lots of papercuts
  - it is idiosyncratic

Bloom is open-sourcing Omni for transparency (since we have also open-sourced many of our apps which make use of Omni).

## Features

- Serves CLJS files from same server as the HTTP API (which is assumed to speak transit-json)
- Includes various middleware with reasonable defaults (ring-middleware, muuntaja)
- Compiles CLJS with Figwheel (live-updates in dev; static compile for prod) (but doesn't currently expose the CLJS repl)
- Compiles CSS (with garden, and/or with girouette for tailwind classes)
- Serves an HTML response pointing to relevant JS and CSS files, with cache-busting and resource-verification (currently, this is a catch-all; there are no 404s in the backend by default)
- Supports enabling cookie-based sessions, token-based auth (email magic tokens), and Google OAuth

## Set Up

Add the following to your `project.clj`:

```clojure
:dependencies [[io.bloomventures/omni "0.16.0"]]

:plugins [[io.bloomventures/omni "0.16.0"]]

:omni-config app.omni-config/omni-config

:profiles {:uberjar {:aot :all
                     :prep-tasks [["omni" "compile"]
                                 "compile"]}}
```

Create an `omni_config.clj`:

```clojure
(ns app.omni-config
  (:require
    [app.config :refer [config]]))

(def omni-config
  {:omni/title "My Title"
   :omni/cljs {:main "app.core"}
   :omni/css {:styles "app.styles/styles"}
   :omni/api-routes #'routes
   :omni/http-port (config :http-port)
   :omni/environment (config :environment)})
```

Somewhere in your app, have a function to start omni:

```clojure
(ns app.core
  (:gen-class)
  (:require
    [bloom.omni.core :as omni]
    [app.omni-config :refer [omni-config]]))

(defn start! []
  (omni/start! omni/system config))

(defn stop! []
  (omni/stop!))

(defn -main []
  (start!))
```


## Config

```clojure
{;; port on which to run both the API and front-end
 :omni/http-port 3000

 ;; environment
 ;;   affects cookie settings and misc other optimizations
 :omni/environment :dev ;; or :prod

 ;; title for HTML SPA response
 :omni/title "App"

 ;; cljs main namespace
 ;;   expects an init() and reload() fn
 ;;   figwheel needs ^:figwheel-hooks meta on the ns
 ;;   and ^:export on init() and ^:after-load on reload()
 :omni/cljs {:main "app.client.core"}

 ;; externs
 :omni/cljs {:externs ["path/to/externs.js"]}

 ;; additional js to include in the SPA response
 ;;   can have :body, :src, :defer, :async
 :omni/js-scripts [{:src "/graph.js"}]

 ;; hiccup of other stuff to add into head
 :omni/html-head-includes [:meta ,,,]

 ;; tailwind styles
 :omni/css {:tailwind? true}

 ;; garden styles
 ;;   expects reference to garden styles
 :omni/css {:styles "app.styles/styles"}

 ;; api routes, following omni route format
 :omni/api-routes #'routes

 ;; raw routes (no middleware)
 :omni/raw-routes #'routes

 ;; cookie auth
 :omni/auth {:cookie {:name "myapp"
                      :secret "...."
                      :same-site :strict ;; defaults to strict, also supports :lax, :none
                      }}

 ;; token auth
 ;;   for email magic links
 ;;   to create, use (omni.auth.token/login-query-string user-id secret)
 :omni/auth {:token {:secret "...."}}

 ;; google oauth
 ;;   (require cookie auth enabled)
 :omni/auth {:oauth {:google {:client-id "key-from-google"
                              :domain "http://localhost:1234"}
                     :user-from-session-id (fn [session-id] ...)
                     :user-to-session-id (fn [user] ...)}}}

```

### Omni Route Format

```clojure
(def routes
  [
    [[:get "/foo/:bar/"]
     (fn [request]
       {:status 200
        :body {:foo (get-in request [:query-params :bar])}})
    [optional-middleware]]

   ,,,
  ])
```

## Developing Omni

### Releasing

- use [BreakVer](https://github.com/ptaoussanis/encore/blob/master/BREAK-VERSIONING.md)
- using lein-v
- `lein release patch`
