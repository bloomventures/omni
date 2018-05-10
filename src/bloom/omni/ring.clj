(ns bloom.omni.ring
  (:require
    [ring.middleware.format :refer [wrap-restful-format]]
    [ring.middleware.defaults :refer [wrap-defaults
                                      api-defaults
                                      secure-api-defaults]]))

(defn api 
  "Returns API defaults middleware"
  [{:keys [secure?]}]
  (fn [handler]
    (-> handler
        (wrap-defaults (if secure?
                         (assoc secure-api-defaults :proxy true)
                         api-defaults))
        (wrap-restful-format :formats [:transit-json]))))
