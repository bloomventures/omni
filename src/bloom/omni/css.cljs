(ns bloom.omni.css
  "Provides `defstyle` for doing css-in-clj(s).

  ```clojure
  (require '[bloom.omni.css :refer [defstyle]])
  (defstyle view-styles
    [:& 
      {:color \"red\"}])

  (defn view []
    [:div.view {:class view-styles}]))
  ```"
  ; require-macros here makes it possible for cljs consumers
  ; of this namespace to just do a regular require
  (:require-macros 
    [bloom.omni.css :refer [defstyle]]))
