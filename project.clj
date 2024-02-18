(defproject io.bloomventures/omni :lein-v
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/clojurescript "1.11.60"
                  :exclusions [com.cognitect/transit-clj ; newer in bloom.commons
                               com.fasterxml.jackson.core/jackson-core]]

                 ;; overrides
                 [org.clojure/data.xml "0.2.0-alpha6"]
                 [instaparse "1.4.12"]
                 [borkdude/edamame "0.0.11-alpha.21"]
                 [riddley "0.1.12"]
                 [org.codehaus.plexus/plexus-utils "3.4.1"]

                 [io.bloomventures/commons "0.14.0"
                  :exclusions
                  [org.clojure/data.xml
                   borkdude/edamame
                   org.apache.httpcomponents/httpcore
                   org.clojure/tools.logging
                   riddley]]

                 [cljsbuild "1.1.8" ;; for cljs
                  :exclusions [org.clojure/clojure
                               clj-stacktrace ;; newer in figwheel
                               ]]
                 [clout "2.2.1" ; clj-router
                  :exclusions [instaparse]]
                 [clojure.java-time "0.3.2"] ; impl.crypto
                 [commons-codec "1.15"] ; impl.crypto
                 [com.bhauman/figwheel-main "0.2.18" ; figwheel
                  :exclusions [org.clojure/clojurescript
                               org.clojure/tools.cli
                               org.eclipse.jetty/jetty-http
                               org.eclipse.jetty/jetty-util
                               org.eclipse.jetty/jetty-io
                               org.eclipse.jetty/jetty-client]]
                 [garden "1.3.10"] ; impl.cssbuild
                 [com.nextjournal/beholder "1.0.0" ; css-watcher
                  :exclusions [org.slf4j/slf4j-api]]
                 [girouette/girouette "0.0.10" ; girouette-watcher
                  :exclusions [org.clojure/core.memoize]]
                 [girouette/processor "0.0.8" ; girouette-watcher
                  :exclusions [org.clojure/core.memoize
                               org.codehaus.plexus/plexus-utils]]
                 [hiccup "1.0.5"] ; spa, auth.google

                 [http-kit "2.5.0"] ;; http-server
                 [org.clojure/core.async "1.5.644" ; impl.async
                  :exclusions [org.clojure/core.memoize]]
                 [ring/ring-core "1.9.1" ; spa
                  :exclusions [org.clojure/core.memoize
                               commons-io]]
                 [ring/ring-defaults "0.3.2"] ; ring
                 ; [metosin/muuntaja "0.6.7"] ; ring ;; take from bloom.commons
                 [clj-commons/secretary "1.2.4"] ; fx.router
                 ; [venantius/accountant "0.2.4"] ; fx.router ;; take from bloom.commons

                 [leiningen-core "2.9.0"]
                 [reagent "1.1.0"]
                 [cljsjs/react "17.0.2-0"]
                 [cljsjs/react-dom "17.0.2-0"]]

  :plugins [[com.roomkey/lein-v "7.1.0"]]

  ;; run 'lein release :minor' for breaking changes
  ;; run 'lein release :patch' for non-breaking changes
  :release-tasks [["vcs" "assert-committed"]
                  ["v" "update"]
                  ["vcs" "push"]
                  ["deploy" "clojars"]]
  )
