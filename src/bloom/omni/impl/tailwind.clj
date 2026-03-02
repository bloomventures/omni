(ns bloom.omni.impl.tailwind
  (:require
    [bloom.omni.impl.giro :as giro]
    [nextjournal.beholder :as beholder]))

(def opts
  {:source-paths ["src"]
   :file-filters [".cljs" ".cljc"]
   :output-file "resources/public/css/twstyles.css"
   :base-css-rules ['girouette.tw.preflight/preflight-v3_0_24]})

(defn start!
  [extra-opts]
  (giro/process (merge opts
                       extra-opts
                       {:watch? true})))

(defn stop!
  [self]
  (beholder/stop self))

(defn compile!
  [extra-opts]
  (giro/process (merge opts extra-opts)))
