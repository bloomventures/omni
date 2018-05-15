(ns bloom.omni.core
  (:require
    [bloom.omni.http-server :as http-server] 
    [bloom.omni.figwheel :as figwheel] 
    [bloom.omni.css-watcher :as css-watcher]
    [bloom.omni.impl.port :as port]
    [bloom.omni.impl.ring :as ring]
    [bloom.omni.impl.middleware :as middleware]
    [bloom.omni.auth.routes :as auth.routes]
    [bloom.omni.spa :as spa]
    [bloom.omni.impl.config :as config]))

(def http-server 
  {:start (fn [config]
            (let [api-middleware 
                  (middleware/api {:production? (= :prod (config :environment))
                                   :session? (boolean (config :omni/auth))
                                   :cookie-secret (get-in config [:omni/auth :cookie-secret])
                                   :cookie-name (get-in config [:omni/auth :cookie-name])})] 
              (http-server/start! 
                {:port (or (config :omni/http-port)
                           (port/next-available))
                 :handler (apply ring/combine
                            (->> [(when (config :omni/auth)
                                    (-> (ring/->handler (auth.routes/routes config))
                                        api-middleware))
                                  (-> (ring/->handler (config :omni/api-routes))
                                      api-middleware)
                                  (ring/->handler (spa/routes config))]
                                 (remove nil?)))})))
   :stop (fn [self]
           (http-server/stop! self))})

(def css-watcher 
  {:start (fn [config]
            (when (config :omni/css)
              (css-watcher/start! {:styles (get-in config [:omni/css :styles])
                                   :output-to "resources/public/css/styles.css"}))) 
   :stop (fn [self]
           (css-watcher/stop! self))})

(def figwheel 
  {:start (fn [config]
            (when (config :omni/cljs)
              (figwheel/start! {:server-port (port/next-available)
                                :cljs-main (get-in config [:omni/cljs :main])})))
   :stop (fn [self]
           (figwheel/stop!))})

(def system
  {:deps (fn [config] 
           (if (= "prod" (config :environment))
             [http-server]
             [figwheel
              css-watcher
              http-server]))})

(defonce state (atom []))

(defn- -start! [component config]
  (when (component :deps)
    (doseq [s ((component :deps) config)]
      (-start! s config)))
  (swap! state conj {:component component
                     :value (when (component :start)
                              ((component :start) config))}))

(defn start! [component config]
  (-start! component (config/read config)))

(defn stop! []
  (doseq [{:keys [component value]} @state]
    (when (component :stop)
      ((component :stop) value)))
  (reset! state []))
