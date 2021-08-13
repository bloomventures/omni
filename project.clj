(defproject io.bloomventures/omni :lein-v
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.879"
                  :exclusions [com.cognitect/transit-clj ; newer in bloom.commons
                               ]]

                 [io.bloomventures/commons "0.11.0"]

                 [cljsbuild "1.1.8" ;; for cljs
                  :exclusions [org.clojure/clojure
                               clj-stacktrace ;; newer in figwheel
                               ]]
                 [clout "2.2.1"] ; clj-router
                 [clojure.java-time "0.3.2"] ; impl.crypto
                 [commons-codec "1.12"] ; impl.crypto
                 [com.bhauman/figwheel-main "0.2.13"
                  :exclusions [org.clojure/clojurescript]] ; figwheel
                 [garden "1.3.9"] ; impl.cssbuild
                 [hawk "0.2.11"] ; css-watcher
                 [girouette/girouette "0.0.3"] ; girouette-watcher
                 [girouette/processor "0.0.2"] ; girouette-watcher
                 [hiccup "1.0.5"] ; spa, auth.google

                 [http-kit "2.5.0"] ;; http-server
                 [org.clojure/core.async "0.4.490"] ; impl.async
                 [ring/ring-core "1.8.1"] ; spa
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
