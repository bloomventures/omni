(ns bloom.omni.fx.ajax
  "Provides a re-frame fx for making ajax calls with transit encoding."
  (:require
    [ajax.core :as ajax]
    [cognitect.transit :as transit]))

(defn fx
  [{:keys [uri method params body format on-success on-error headers timeout]
    :or {format (ajax/transit-request-format)
         on-success identity
         on-error (fn [r]
                    (.error js/console "Ajax request error" (pr-str r)))}}]
  (let [request-id (gensym uri)]
    (ajax/ajax-request
      {:uri uri
       :method method
       :body body
       :params params
       :headers headers
       :timeout timeout
       :handler
       (fn [[ok response]]
         (if ok
           (on-success response)
           (on-error response)))
       :format format
       :response-format (ajax/transit-response-format
                          {:type :json
                           :reader (transit/reader :json {:handlers {"u" uuid}})})})))

