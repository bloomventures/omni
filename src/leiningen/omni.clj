(ns leiningen.omni
  (:require
   [leiningen.core.eval]
   [leiningen.core.main]
   [leiningen.core.project :as p]))

(defn compile! 
  [project]
  (let [config (project :omni-config)
        config-ns (symbol (namespace config))]
    (leiningen.core.eval/eval-in-project 
      project 
      `(do
         (bloom.omni.compile/compile! ~config))
      `(do (require '~config-ns)
           (require 'bloom.omni.compile :reload)))))

(defn omni
  [project & [task]]
  (case task
    "compile" (compile! project)
    nil :not-implemented-yet
    (leiningen.core.main/warn "Unknown task.")))
