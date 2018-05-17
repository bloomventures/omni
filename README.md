# Bloom Omni

A collection of lein tools, namespaces and functions, commonly used across Bloom projects.



## Omni Core 

Omni will...

- Compile CLJS and CSS (live-updates in dev; static compile for prod)
- Serve an html file pointing to relevant JS and CSS files. This file will be retunred for for all HTTP requests (except for those handled by the API and static resources under `/resources/public/*`))
- Server 

Add the following to your `project.clj`:

```clojure
:dependencies [[io.bloomventures/omni "0.11.0"]]

:plugins [[io.bloomventures/omni "0.11.0"]] 

:omni-config app.core/config

:profiles {:uberjar {:aot :all
                     :prep-tasks [["omni" "compile"]
                                 "compile"]}}
```

Somewhere in your app, have a function to start omni:

```
(ns app.core
  (:gen-class)
  (:require
    [bloom.omni.core :as omni]))

(def config
  {:omni/title "My Title" 
   :omni/cljs {:main "app.core"}  
   :omni/css {:styles "app.styles/styles"} 
   :omni/api-routes [[[:get "/api/:id"]
                      (fn [request]
                       {:status 200
                        :body {:id (get-in request [:params :id])})]]})

(defn start!
  (omni/start! omni/system config)) 

(defn stop!
  (omni/stop!)) 

(defn -main []
  (start!))
```

In dev, you should probably also have a `config.edn` at the root of the project (i.e. in the same directory as your `project.clj`) with:
```
{:omni/http-port 1234}
```

In production, you should probably pass the following env vars:

```
   HTTP_PORT=1234
   ENVIRONMENT=prod
```

Conventions:

- expects `init()` and `reload()` fns in the main cljs namespace (remember to mark as ^:export)
- css `styles()` returns a garden object

## Omni Auth

Add the following `config.edn`:

```
{:omni/auth {:google {:client-id "key-from-google"
                      :domain "http://localhost:1234"}}}
```

In prod, pass the following env vars:
```
COOKIE_SECRET="16-byte-string"
CLIENT_ID="key-from-google"
DOMAIN="https://domain.in.prod.com"
```

See `bloom.omni.auth/fx` for re-frame helpers.

## Other helper namespaces:

### EAV

### env

### uuid

### fx/ajax

### fx/dispatch-debounce

### fx/router

### fx/title




## Developing Omni

Clone omni and install it locally:
```
git clone git@github.com:bloomventures/omni.git`
cd omni
lein install
```

- should only expose what's necessary (`defn-` the rest)
- should have unit tests for public functions
- should have docstring for namespace w/ example usage
- files should be cljc if possible (unless it doesn't make sense)
- namespace that are not meant to be used externally should go in `bloom.omni.impl.*`

### Versioning

- use [BreakVer](https://github.com/ptaoussanis/encore/blob/master/BREAK-VERSIONING.md)
- have seperate commits for "Start 0.X.0" (with version: `0.X.0-SNAPSHOT`) and "Release 0.X.0" (with version: `0.X.0`)
- tag the release commit `git tag 0.X.0'
