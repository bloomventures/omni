(ns bloom.omni.fx.ajax
  "Provides a re-frame fx for making ajax calls with transit encoding."
  (:require
    [clojure.string :as string]
    [ajax.core :as ajax]
    [ajax.interceptors :as ajax.interceptors]
    [cognitect.transit :as transit]))

(defn empty-means-nil [response]
  (if (string/blank? (ajax.protocols/-body response))
    (reduced [(-> response ajax.protocols/-status ajax.core/success?) nil])
    response))

(def treat-empty-as-nil
  (ajax.interceptors/to-interceptor
    {:name "Transit special case nil"
     :response empty-means-nil}))

(defn fx
  [{:keys [uri method params body format on-success on-error headers timeout response-format credentials?]
    :or {format (ajax/transit-request-format)
         response-format (ajax/transit-response-format
                           {:type :json
                            :reader (transit/reader :json {:handlers {"u" uuid}})})
         on-success identity
         on-error (fn [r]
                    (.error js/console "Ajax request error" (pr-str r)))
         credentials? false}}]
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
       :response-format response-format
       :with-credentials credentials?
       :interceptors [treat-empty-as-nil]})))
