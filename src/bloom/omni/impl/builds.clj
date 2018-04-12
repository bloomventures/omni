(ns bloom.omni.impl.builds
  (:require
    [mount.core :as mount]
    [bloom.omni.impl.config :refer [config]]))

(mount/defstate builds 
  :start (let [main (get-in config [:cljs :main])]
           [{:id "dev"
             :figwheel {:on-jsload (str main "/reload")}
             :source-paths ["src"]
             :compiler {:main main
                        :asset-path "/js/dev"
                        :output-to "resources/public/js/app.js"
                        :output-dir "resources/public/js/dev"
                        :verbose true}}
            {:id "prod"
             :source-paths ["src"]
             :compiler {:main main
                        :asset-path "/js/prod"
                        :output-to "resources/public/js/app.js"
                        :output-dir "resources/public/js/prod"
                        :optimizations :advanced}}]))
