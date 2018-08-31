(ns bloom.omni.impl.cljsbuild
  (:require
    [cljsbuild.compiler :as cljsbuild]))

(defn compile! [build]
  (cljsbuild/run-compiler
    (build :source-paths)
    [] ; checkout-cljs-paths
    "" ; crossover-path
    [] ; crossover-macro-paths
    (build :compiler)
    (build :parsed-notify-command)
    (build :incremental)
    (build :assert)
    {} ; mtimes
    false ; watching?
    ))
