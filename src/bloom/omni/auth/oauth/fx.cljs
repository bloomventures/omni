(ns bloom.omni.auth.oauth.fx
  (:refer-clojure :exclude [get])
  (:require
    [re-frame.db :refer [app-db]]
    [reagent.ratom :as r]
    [re-frame.core :refer [reg-fx reg-sub]]
    [bloom.omni.fx.ajax :as ajax]))

(defn- db-set! [m]
  (swap! app-db update :omni/auth merge m))

(defn- db-get [k]
  (get-in @app-db [:omni/auth k]))

(defn- remote-oauth! [token]
  (ajax/fx
    {:method :put
     :uri "/api/auth/authenticate"
     :params {:token token}
     :on-success
     (fn [{:keys [user]}]
       (db-set! {:authenticating? false
                 :user user})
       ((db-get :after-login-fn) user))}))

(defn- check-authentication!
  [after-fn]
  (ajax/fx
    {:method :get
     :uri "/api/auth/user"
     :on-success
     (fn [{:keys [user]}]
       (db-set! {:user user})
       (after-fn user))}))

(defn- attach-message-listener! []
  (when-not (db-get :message-handler-attached?)
    (js/window.addEventListener "message"
                                (fn [e]
                                  (let [token (.-data e)]
                                    (remote-oauth! token))))
    (db-set! {:message-handler-attached? true})))

(defn- init! [after-fn]
  (db-set! {:authenticating? false
            :user nil
            :after-login-fn nil
            :message-handler-attached? false})
  (check-authentication! after-fn))

(defn- log-in!
  [after-fn]
  (db-set! {:authenticating? true
            :after-login-fn after-fn})
  (attach-message-listener!)
  (js/window.open "/api/auth/request-token"
                  "Log In"
                  "width=500,height=700"))

(defn- log-out!
  [after-fn]
  (ajax/fx
    {:uri "/api/auth"
     :method :delete
     :on-success
     (fn [_]
       (db-set! {:user nil})
       (after-fn))}))

(defn- fx [[k opt]]
  (case k
    :init! (init! opt)
    :log-in! (log-in! opt)
    :log-out! (log-out! opt)))

; usage:
; (require [bloom.omni.auth.oauth.fx :as auth])
; (auth/register)

; effects in events:
; {:auth [:init! after-fn]}
; {:auth [:log-in! after-fn]}
; {:auth [:log-out! after-fn]}

; data in events:
; (auth/get db [:logged-in?])
; (auth/get db [:state])
; (auth/get db [:user])

; subs:
; [:omni/auth :logged-in?]
; [:omni/auth :state]
; [:omni/auth :user]

(defn get [db [k & args]]
  (case k
    :logged-in? (boolean (get-in db [:omni/auth :user]))
    :user (get-in db [:omni/auth :user])
    :state (cond
             (get-in db [:omni/auth :authenticating?])
             :authenticating
             (get-in db [:omni/auth :user])
             :logged-in
             :else
             :logged-out)))

(defn register []
  (reg-fx :omni/auth fx)

  (reg-sub :omni/auth
    (fn [db [_ & args]]
      (get db args))))

