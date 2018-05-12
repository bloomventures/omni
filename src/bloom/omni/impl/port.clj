(ns bloom.omni.impl.port
  (:import
    (java.net ServerSocket)))

; ref: https://gist.github.com/vorburger/3429822

(defn next-available 
  "Returns next available port #"
  []
  (let [s (ServerSocket. 0)
        port (.getLocalPort s)]
    (.setReuseAddress s true)
    (.close s)
    port))
