(ns bloom.omni.impl.builds)

(defn builds [{:keys [main externs]}]
  [{:id "dev"
    :figwheel {:websocket-host :js-client-host
               :mode :serve
               :open-url false
               :watch-dirs ["src"]}
    :source-paths ["src"]
    :compiler {:main main
               :output-to "resources/public/js/app.js"
               :asset-path "/js/dev"
               :output-dir "resources/public/js/dev"
               :closure-defines {"goog.DEBUG" true}
               :parallel-build true
               :verbose true}}
   {:id "prod"
    :source-paths ["src"]
    :compiler (merge {:main main
                      :output-to "resources/public/js/app.js"
                      :output-dir "target/cljs-prod"
                      :closure-defines {"goog.DEBUG" false}
                      :optimizations :advanced
                      :parallel-build true
                      :infer-externs true
                      :static-fns true
                      :fn-invoke-direct true}
                     (when externs
                       {:externs externs}))}])
