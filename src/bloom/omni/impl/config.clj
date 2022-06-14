(ns bloom.omni.impl.config
  (:refer-clojure :exclude [read])
  (:require
    [bloom.commons.config :as config]))

(def Config
  [:map
   [:omni/title {:optional true} string?]
   [:omni/css {:optional true}
    [:map
     [:styles {:optional true} string?]
     [:tailwind? {:optional true} boolean?]
     [:tailwind-opts {:optional true} map?]]]
   [:omni/cljs {:optional true}
    [:map
     [:main string?]
     [:externs {:optional true}
      [:vector string?]]]]
   [:omni/http-port {:optional true} integer?]
   [:omni/environment {:optional true} [:enum :prod :dev]]
   [:omni/api-routes {:optional true} [:fn 'any? #_var?]]
   [:omni/raw-routes {:optional true} [:fn 'any?]]
   [:omni/js-scripts {:optional true}
    [:vector
     [:map
      [:body {:optional true} string?]
      [:src {:optional true} string?]
      [:defer {:optional true} string?]
      [:async {:optional true} string?]]]]
   [:omni/auth {:optional true}
    [:map
     [:cookie {:optional true}
      [:map
       ;; a temporary one is used in dev
       [:secret {:optional true} [:and string?
                                  [:fn
                                   '(fn [s]
                                      (= 16 (count s)))]]]
       [:name {:optional true} string?]
       [:same-site {:optional true} [:enum :lax :strict :none]]]]
     [:token {:optional true}
      [:map
       [:secret string?]]]
     [:oauth {:optional true}
      [:map
       [:post-auth-fn {:optional true} [:fn 'fn?]]
       [:user-from-session-id {:optional true} [:fn 'fn?]]
       [:user-to-session-id {:optional true} [:fn 'fn?]]
       [:google
        [:map
         [:client-id string?]
         [:domain string?]]]]]]]])

(defn read [config]
  (config/parse config Config))
