(ns bloom.omni.router
  "Provides cljs functions for client-side routing system:
  
  (router/init!) to initiate the router

  (router/defroute ...) to defining secretary routes
  
  (reg-fx :navigate router/navigate-fx) to define a re-fram fx"
  (:require
    [accountant.core :as accountant]
    [secretary.core :as secretary])
  (:require-macros
    [bloom.omni.router :refer [defroute]]))

(defn init! []
  (accountant/configure-navigation! {:nav-handler secretary/dispatch!
                                     :path-exists? secretary/locate-route})
  (accountant/dispatch-current!))

(defn navigate-fx 
  "Fx for use with re-frame"
  [path]
  (if (= path :current-path)
    (accountant/dispatch-current!)
    (accountant/navigate! path)))
