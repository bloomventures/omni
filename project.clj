(defproject io.bloomventures/omni "0.7.0"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.946"]
                 [mount "0.1.12"]

                 ; spa
                 [hiccup "1.0.5"]
                 [ring/ring-core "1.6.3"]

                 ; css-watcher
                 [hawk "0.2.11"]

                 ; css
                 [garden "1.3.4"]

                 ; http-server
                 [http-kit "2.2.0"]

                 ; router
                 [secretary "1.2.3"] 
                 [venantius/accountant "0.2.4"]

                 ; figwheel
                 [figwheel-sidecar "0.5.14"]

                 ; ajax
                 [cljs-ajax "0.7.2"]
                 [com.cognitect/transit-cljs "0.8.243"]

                 ; impl.async
                 [org.clojure/core.async "0.4.474"]

                 ; impl.digest
                 [commons-codec "1.10"]
                 
                 ; router
                 [clout "2.1.2"]])
