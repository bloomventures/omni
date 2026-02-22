(ns bloom.omni.core
  (:require
   [sys.api :as sys]
   [bloom.omni.auth.oauth.routes :as oauth.routes]
   [bloom.omni.css-watcher :as css-watcher]
   [bloom.omni.figwheel :as figwheel]
   [bloom.omni.http-server :as http-server]
   [bloom.omni.impl.config :as config]
   [bloom.omni.impl.middleware :as middleware]
   [bloom.omni.impl.port :as port]
   [bloom.omni.impl.ring :as ring]
   [bloom.omni.impl.tailwind :as tailwind]
   [bloom.omni.spa :as spa]))

(def http-server-component
  {:sys.component/id      :omni/http-server
   :sys.component/expects #{:omni/config}
   :sys.component/provides #{:omni/http-server}
   :sys.component/start
   (fn [{config :omni/config}]
     (let [middleware-config {:production?      (= :prod (config :omni/environment))
                              :session?         (boolean (get-in config [:omni/auth :cookie]))
                              :token-secret     (get-in config [:omni/auth :token :secret])
                              :cookie-secret    (get-in config [:omni/auth :cookie :secret])
                              :cookie-name      (get-in config [:omni/auth :cookie :name])
                              :cookie-same-site (get-in config [:omni/auth :cookie :same-site])
                              :frame-options    (get-in config [:omni/auth :frame-options])}]
       {:omni/http-server
        (http-server/start!
         {:port    (or (config :omni/http-port)
                       (port/next-available))
          :handler (->> [(when (config :omni/raw-routes)
                           (ring/->handler
                            (if (= :prod (config :omni/environment))
                              (var-get (config :omni/raw-routes))
                              (config :omni/raw-routes))))
                         (->> [(when-let [oauth-config (get-in config [:omni/auth :oauth])]
                                 (ring/->handler (oauth.routes/routes oauth-config)))
                               (when (config :omni/api-routes)
                                 (ring/->handler
                                  (if (= :prod (config :omni/environment))
                                    (var-get (config :omni/api-routes))
                                    (config :omni/api-routes))))
                               (ring/->handler
                                [[[:any "/api/*"]
                                  (fn [_]
                                    {:status 404})]])]
                              (remove nil?)
                              (apply ring/combine)
                              ((middleware/make-api-middleware middleware-config)))
                         (->> (ring/->handler (spa/routes config))
                              ((middleware/make-spa-middleware middleware-config)))]
                        (remove nil?)
                        (apply ring/combine))})}))
   :sys.component/stop
   (fn [{server :omni/http-server}]
     (http-server/stop! server))})

(def girouette-watcher-component
  {:sys.component/id      :omni/girouette-watcher
   :sys.component/expects #{:omni/config}
   :sys.component/provides #{:omni/girouette-watcher}
   :sys.component/start
   (fn [{config :omni/config}]
     {:omni/girouette-watcher
      (when (get-in config [:omni/css :tailwind?])
        (tailwind/start! (get-in config [:omni/css :tailwind-opts] {})))})
   :sys.component/stop
   (fn [{watcher :omni/girouette-watcher}]
     (when watcher
       (tailwind/stop! watcher)))})

(def css-watcher-component
  {:sys.component/id      :omni/css-watcher
   :sys.component/expects #{:omni/config}
   :sys.component/provides #{:omni/css-watcher}
   :sys.component/start
   (fn [{config :omni/config}]
     {:omni/css-watcher
      (when (get-in config [:omni/css :styles])
        (css-watcher/start! {:styles    (get-in config [:omni/css :styles])
                             :output-to "resources/public/css/styles.css"}))})
   :sys.component/stop
   (fn [{watcher :omni/css-watcher}]
     (when watcher
       (css-watcher/stop! watcher)))})

(def figwheel-component
  {:sys.component/id      :omni/figwheel
   :sys.component/expects #{:omni/config}
   :sys.component/provides #{:omni/figwheel}
   :sys.component/start
   (fn [{config :omni/config}]
     {:omni/figwheel
      (when (config :omni/cljs)
        (figwheel/start! {:server-port (port/next-available)
                          :cljs-opts   (config :omni/cljs)
                          :css?        (boolean (config :omni/css))}))})
   :sys.component/stop
   (fn [_]
     (figwheel/stop!))})

(def dev-components
  [figwheel-component
   css-watcher-component
   girouette-watcher-component
   http-server-component])

(def prod-components
  [http-server-component])

(defn start! [raw-config]
  (let [omni-config (config/read raw-config)
        config-component {:sys.component/id      :omni/config
                          :sys.component/provides #{:omni/config}
                          :sys.component/start    (fn [_] {:omni/config omni-config})}
        components (if (= :prod (omni-config :omni/environment))
                     (conj prod-components config-component)
                     (conj dev-components config-component))]
    (sys/set! ::omni components)
    (sys/start! ::omni)
    nil))

(defn stop! []
  (sys/stop! ::omni)
  nil)
