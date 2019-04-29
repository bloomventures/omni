(defproject io.bloomventures/omni "0.21.0"
  :dependencies [; Overrides
                 [org.clojure/tools.reader "1.3.2"]
                 [com.cognitect/transit-clj "0.8.313"]
                 [clj-stacktrace "0.2.8"]
                 [com.fasterxml.jackson.core/jackson-core "2.9.8"]
                 [cheshire "5.8.1"]
                 [args4j "2.33"]
                 [com.google.errorprone/error_prone_annotations "2.1.3"]
                 [com.google.code.findbugs/jsr305 "3.0.2"]

                 [org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.520"]

                 [com.cognitect/transit-cljs "0.8.256"] ; fx.ajax
                 [cljsbuild "1.1.7" :exclude [org.clojure/clojure]] ; figwheel
                 [cljs-ajax "0.8.0"] ; fx.ajax
                 [clout "2.2.1"] ; clj-router
                 [clojure.java-time "0.3.2"] ; impl.crypto
                 [commons-codec "1.12"] ; impl.crypto
                 [com.bhauman/figwheel-main "0.2.0"] ; figwheel
                 [garden "1.3.9"] ; impl.cssbuild
                 [hawk "0.2.11"] ; css-watcher
                 [hiccup "1.0.5"] ; spa, auth.google
                 [http-kit "2.3.0"] ; http-server
                 [metosin/spec-tools "0.9.1"] ; config
                 [org.clojure/core.async "0.4.490"] ; impl.async
                 [ring/ring-core "1.7.1"] ; spa
                 [ring/ring-defaults "0.3.2"] ; ring
                 [ring-middleware-format "0.7.4"] ; ring
                 [clj-commons/secretary "1.2.4"] ; fx.router
                 [venantius/accountant "0.2.4"] ; fx.router

                 [leiningen-core "2.9.0"]
                 ])
