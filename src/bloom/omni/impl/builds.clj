(ns bloom.omni.impl.builds)

(defn builds [{:keys [main externs css?]}]
  [{:id "dev"
    :figwheel (merge {:connect-url "ws://[[client-hostname]]:[[server-port]]/figwheel-connect"
                      :mode :serve
                      :open-url false
                      :watch-dirs ["src"]}
                     (when css?
                       {:css-dirs ["resources/public/css"]}))
    :source-paths ["src"]
    :compiler {:main main
               :output-to "resources/public/js/app.js"
               :asset-path "/js/dev"
               :output-dir "resources/public/js/dev"
               :closure-defines {"goog.DEBUG" true}
               :parallel-build true}}
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
                      :fn-invoke-direct true
                      :language-in :ecmascript-next
                      :language-out :ecmascript-2015}
                     (when externs
                       {:externs externs}))}])
