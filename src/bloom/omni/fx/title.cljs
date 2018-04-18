(ns bloom.omni.fx.title)

(defn fx [title]
  (set! (.-title js/document) title))
