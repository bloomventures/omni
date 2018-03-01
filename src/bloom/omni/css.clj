(ns bloom.omni.css
  (:require
    [bloom.omni.impl.css :as util]))

(defmacro defstyle [n rules]
  (let [class-name (util/register-style! rules)]
    `(def ~n ~class-name)))
