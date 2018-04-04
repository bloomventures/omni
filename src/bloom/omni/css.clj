(ns bloom.omni.css
  (:require
    [bloom.omni.impl.css :as util]))

(defmacro defstyle [n rules]
  (let [styles (util/->styles rules)]
    `(def ~n ~styles)))
