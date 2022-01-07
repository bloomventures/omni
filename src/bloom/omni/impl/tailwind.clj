(ns bloom.omni.impl.tailwind
  (:require
    [girouette.processor]
    [hawk.core :as hawk]))

(def opts
  {:css {:output-file "resources/public/css/twstyles.css"}
   :input {:file-filters [".cljs" ".cljc"]}
   :verbose? false})

(defn start!
  [extra-opts]
  (girouette.processor/process (merge (assoc opts :watch? true)
                                      extra-opts)))

(defn stop!
  [self]
  (hawk/stop! self))

(defn compile!
  [extra-opts]
  (girouette.processor/process (merge opts extra-opts)))
