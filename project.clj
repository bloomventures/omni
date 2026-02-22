(defproject io.bloomventures/omni :lein-v
  :description "homebrew spa web framework used by bloom"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo}
  :dependencies [[io.bloomventures/commons "0.16.0"
                  :exclusions [org.clojure/data.xml]]
                 [org.clojure/clojurescript "1.12.134"
                  :exclusions [com.cognitect/transit-java]]

                 ;; [org.clojure/clojure "1.12.0"] ;; take from bloom.commons

                 ;; overrides
                 [org.codehaus.plexus/plexus-utils "3.4.1"]
                 [org.slf4j/slf4j-api "2.0.9"]

                 [cljsbuild "1.1.8" ;; for cljs
                  :exclusions [org.clojure/clojurescript
                               org.clojure/tools.namespace
                               clj-stacktrace ;; newer in figwheel
                               ]]
                 [clout "2.2.1" ; clj-router
                  :exclusions [instaparse ;; newer in girouette
                               org.clojure/tools.analyzer ;; newer in core.async
                               ]]
                 [clojure.java-time "1.4.3"] ; auth.token
                 [commons-codec "1.21.0"] ; impl.crypto
                 [com.bhauman/figwheel-main "0.2.20" ; figwheel
                  :exclusions [org.clojure/tools.cli]]
                 [garden "1.3.10"] ; impl.cssbuild
                 [com.nextjournal/beholder "1.0.3" ; css-watcher
                  :exclusions [org.slf4j/slf4j-api]]
                 [girouette/girouette "0.0.10"] ; girouette-watcher
                 [girouette/processor "0.0.8" ; girouette-watcher
                  :exclusions [org.eclipse.jetty/jetty-util
                               org.clojure/tools.analyzer]]
                 [hiccup "1.0.5"] ; spa, auth.google

                 [http-kit "2.8.1"] ;; http-server
                 [org.clojure/core.async "1.8.741"] ; impl.async
                 [ring/ring-core "1.15.3"] ; spa
                 [ring/ring-defaults "0.7.0"] ; ring
                 ; [metosin/muuntaja "0.6.7"] ; ring ;; take from bloom.commons
                 [clj-commons/secretary "1.2.4"] ; fx.router
                 ; [venantius/accountant "0.2.4"] ; fx.router ;; take from bloom.commons

                 [com.github.rafd/sys "0.3.1"]

                 [leiningen-core "2.9.0"]

                 ; [reagent "1.1.0] ;; take from bloom.commons
]

  :plugins [[com.roomkey/lein-v "7.1.0"]]

  ;; run 'lein release :minor' for breaking changes
  ;; run 'lein release :patch' for non-breaking changes
  :release-tasks [["vcs" "assert-committed"]
                  ["v" "update"]
                  ["vcs" "push"]
                  ["deploy" "clojars"]]
  )
