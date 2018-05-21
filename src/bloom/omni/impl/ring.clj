(ns bloom.omni.impl.ring
  (:require
    [clout.core :as clout]))

(defn prepare-routes
  "Given routes of the form:
  [
    [
      [:method \"url-pattern\"]
      (fn [request] ...)
      [middleware-fn middleware-fn] ; optional
    ]
    ...
  ]
   converts to the form:

   [
     {:method :method
      :uri \"url-pattern\"
      :matcher (clout/route-compile uri)
      :handler-fn (fn [] )} ; with middleware applied
   ]"
  [routes]
  (->> routes
       (map (fn [[[method uri] handler middleware]]
              {:method method
               :uri uri
               :matcher (clout/route-compile uri)
               :handler-fn ((apply comp (reverse middleware)) handler)}))))



(defn- filter-matching
  "Filter a list of potential routes to return only those that can handle the request.

   A route can handle a request if:
  (1) the route's method matches the request method (or the route has method: :any),
  (2) the route's url matches the request's url (according to clout)"
  [routes request]
  (->> routes
       (filter (fn [{:keys [method matcher]}]
                 (and
                   (or
                     (= method :any)
                     (= method (request :request-method)))
                   (clout/route-matches matcher request))))))

(defn- dispatch
  "Given a list of routes, return the result of the first which returns a truthy value"
  [routes request]
  (->> routes
       (some (fn [route]
               (let [handler-fn (route :handler-fn)
                     params (clout/route-matches (route :matcher) request)]
                 (handler-fn (update request :params merge params)))))))

(defn ->handler
  "Given an list of pattern - handler pairs (see example below), returns a ring handler.

   Given a request, the handler will return a response from the first route that:
     (1) matches the method (:any is allowed),
     (2) matches the url pattern, and
     (3) returns a truthy value

   URL pattern matching is done with the clout library (ie. same as compojure).

   [
     [
      [:get \"/path/\"]
      (fn [request]
        {:status 200
         :body \"OK\"})
      [middleware-fn middleware-fn] ; optional
     ]
     ...
   ]"
  [input-routes]
  (let [routes (prepare-routes input-routes)]
    (fn [request]
      (-> routes
          (filter-matching request)
          (dispatch request)))))

(defn combine
  "Combine multiple ring handlers"
  [& handlers]
  (fn [request]
    (some #(% request) handlers)))
