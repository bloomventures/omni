(ns bloom.omni.router
  "Wrapper for secretary + accoutnant + re-frame based client-side routing.

  Provides defroute macro for registering routes and an fx for re-frame.

  (router/defroute ...) to define secretary routes

  (reg-fx :router router/fx) to register the fx
  
  {:router [:init!]} to initiate the router (should only be called once)

  {:router [:dispatch-current!]} to trigger route for the current URL

  {:router [:navigate! \"path\"]} to navigate browser to the provided path (and dispatch)"
  (:require
    [accountant.core :as accountant]
    [secretary.core :as secretary])
  (:require-macros
    [bloom.omni.router :refer [defroute]]))

(defn- navigate! [path]
  (accountant/navigate! path))

(defn- dispatch-current! []
  (accountant/dispatch-current!))

(defn- init! []
  (accountant/configure-navigation! {:nav-handler secretary/dispatch!
                                     :path-exists? secretary/locate-route})
  (dispatch-current!))

(defn fx 
  "Fx for use with re-frame"
  [[command & args]]
  (case command
    :init! (init!)
    :dispatch-current! (dispatch-current!)
    :navigate! (apply navigate! args)))

