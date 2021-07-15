(ns bloom.omni.impl.tailwind
  (:require
    [girouette.processor]
    [hawk.core :as hawk]))

(def opts
  {:css {:output-file "resources/public/css/twstyles.css"}
   :input {:file-filters [".cljs" ".cljc"]}
   :verbose? false})

(defn start! []
  (girouette.processor/process (assoc opts :watch? true)))

(defn stop!
  [self]
  (hawk/stop! self))

(defn compile! []
  (girouette.processor/process opts))
