(ns bloom.omni.core
  (:require
    [bloom.omni.http-server :as http-server] 
    [bloom.omni.figwheel :as figwheel] 
    [bloom.omni.css-watcher :as css-watcher]
    [bloom.omni.impl.ring :as ring]
    [bloom.omni.spa :as spa]
    [bloom.omni.impl.config :as config]))

(def http-server 
  {:start (fn [config]
            (http-server/start! 
              (config :http-port) 
              (ring/combine
                (ring/->handler (config :routes))
                (ring/->handler (spa/routes config)))))
   :stop (fn [self]
           (http-server/stop! self))})

(def css-watcher 
  {:start (fn [config]
            (css-watcher/start! (config :css))) 
   :stop (fn [self]
           (css-watcher/stop! self))})

(def figwheel 
  {:start (fn [config]
            (figwheel/start! config))
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
  (-start! component (config/fill config)))

(defn stop! []
  (doseq [{:keys [component value]} @state]
    (when (component :stop)
      ((component :stop) value)))
  (reset! state []))
