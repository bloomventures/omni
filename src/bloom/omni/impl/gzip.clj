(ns bloom.omni.impl.gzip
  (:require
    [clojure.java.io :as io])
  (:import
    (java.util.zip GZIPOutputStream)))

(defn copy
  [path-in path-out]
  (with-open [in (io/input-stream (io/file path-in))
              out (io/output-stream (io/file path-out))
              gzip (GZIPOutputStream. out)]
    (io/copy in gzip)
    (.finish gzip)))

(defn compress 
  [path-in]
  (copy path-in (str path-in ".gz")))
