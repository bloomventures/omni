(ns bloom.omni.impl.async
  (:require
    [clojure.core.async :refer [go-loop alts! chan <! put! timeout sliding-buffer]]))

(defn throttle 
  "Given a function, returns a throttled version,
   where the function is only invoked once every t milliseconds,
   on the trailing edge
  
   Based on: 
   https://stackoverflow.com/questions/35663415/throttle-functions-with-core-async"
  [f t]
  (let [first? (atom true)
        c (chan (sliding-buffer 1))]
    (go-loop []
      (let [v (<! c)]
        (if @first?
          (reset! first? false)
          (apply f v)))
      (<! (timeout t))
      (recur))
    (fn [& args]
      (put! c (or args [])))))

(defn debounce 
  "Given a function, returns a debounced version,
  where after a t ms gap, the final function is invoked

  Based on:
  https://gist.github.com/scttnlsn/9744501"
  [f ms]
  (let [c (chan (sliding-buffer 1))]
    (go-loop [last-val nil]
      (let [v (if (nil? last-val) (<! c) last-val)
            timer (timeout ms)
            [new-val ch] (alts! [c timer])]
        (condp = ch
          timer (do 
                  (apply f v)
                  (recur nil))
          c (recur new-val))))
    (fn [& args]
      (put! c (or args [])))))
