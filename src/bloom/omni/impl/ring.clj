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

(defn- matches?
  "A route can handle a request if:
  (1) the route's method matches the request method (or the route has method: :any),
  (2) the route's url matches the request's url (according to clout)"
  [request {:keys [method matcher]}]
  (and
    (or
      (= method :any)
      (= method (request :request-method)))
    (clout/route-matches matcher request)))

(defn- dispatch
  "Given a list of routes, return the result of the first which returns a truthy value"
  [request routes]
  (->> routes
       (some (fn [route]
               (let [handler-fn (route :handler-fn)
                     params (clout/route-matches (route :matcher) request)]
                 (handler-fn (update request :params merge params)))))))

(defn- ->static-handler
  [route-defs]
  (let [routes (prepare-routes route-defs)]
    (fn [request]
      (->> routes
           (filter (fn [route-meta]
                     (matches? request route-meta)))
           (dispatch request)))))

(defn- ->dynamic-handler
  [route-defs-var]
  (fn [request]
    ((->static-handler (var-get route-defs-var)) request)))

(defn ->handler
  "Given a list of route definitions (pattern - handler pairs, see example below),
   or a var of such, returns a ring handler.

   Given a request, the handler will return a response from the first route that:
     (1) matches the method (:any is allowed),
     (2) matches the url pattern, and
     (3) returns a truthy value

   URL pattern matching is done with the clout library (ie. same as compojure).

   If the supplied route definitions are a var, the returned handler will re-parse
   the route definitions each request. This useful in development,
   but not recommended in production.

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
  [route-defs]
  (if (var? route-defs)
    (->dynamic-handler route-defs)
    (->static-handler route-defs)))

(defn combine
  "Combine multiple ring handlers"
  [& handlers]
  (fn [request]
    (some #(% request) handlers)))
