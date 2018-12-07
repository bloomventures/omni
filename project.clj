(defproject io.bloomventures/omni "0.17.9-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.946"]

                 [com.cognitect/transit-cljs "0.8.243"] ; fx.ajax
                 [cljsbuild "1.1.7" :exclude [org.clojure/clojure]] ; figwheel
                 [cljs-ajax "0.7.2"] ; fx.ajax
                 [clout "2.1.2"] ; clj-router
                 [clojure.java-time "0.3.2"] ; impl.crypto
                 [commons-codec "1.10"] ; impl.crypto
                 [figwheel-sidecar "0.5.14"] ; figwheel
                 [garden "1.3.4"] ; impl.cssbuild
                 [hawk "0.2.11"] ; css-watcher
                 [hiccup "1.0.5"] ; spa, auth.google
                 [http-kit "2.2.0"] ; http-server
                 [metosin/spec-tools "0.6.1"] ; config
                 [org.clojure/core.async "0.4.474"] ; impl.async
                 [ring/ring-core "1.6.3"] ; spa
                 [ring/ring-defaults "0.3.1"] ; ring
                 [ring-middleware-format "0.7.2"] ; ring
                 [secretary "1.2.3"] ; fx.router
                 [venantius/accountant "0.2.4"] ; fx.router

                 [leiningen-core "2.8.1"]
                 ])
