(ns bloom.omni.impl.builds)

(defn builds [main]
  [{:id "dev"
    :figwheel {:on-jsload (str main "/reload")
               :websocket-host :js-client-host}
    :source-paths ["src"]
    :compiler {:main main
               :output-to "resources/public/js/app.js"
               :asset-path "/js/dev"
               :output-dir "resources/public/js/dev"
               :verbose true}}
   {:id "prod"
    :source-paths ["src"]
    :compiler {:main main
               :output-to "resources/public/js/app.js"
               :output-dir "target/cljs-prod"
               :optimizations :advanced}}])
