(ns bloom.omni.impl.tailwind
  (:require
    [girouette.processor]
    [nextjournal.beholder :as beholder]))

(def opts
  {:css {:output-file "resources/public/css/twstyles.css"}
   :input {:file-filters [".cljs" ".cljc"]}
   :verbose? false
   :garden-fn 'girouette.tw.default-api/tw-v2-class-name->garden})

(defn start!
  [extra-opts]
  ;; New girouette uses a blocking watcher
  (with-redefs [beholder/watch-blocking #'beholder/watch]
    (girouette.processor/process (merge (assoc opts :watch? true)
                                      extra-opts))))

(defn stop!
  [self]
  (beholder/stop self))

(defn compile!
  [extra-opts]
  (girouette.processor/process (merge opts extra-opts)))
