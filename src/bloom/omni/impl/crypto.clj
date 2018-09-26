(ns bloom.omni.impl.crypto
  (:require
   [clojure.java.io :as io])
  (:import
   (java.security DigestInputStream MessageDigest)
   (org.apache.commons.codec.binary Base64)))

(defn sha256-file
  "Compute the base64-encoded sha256 sum of the given file"
  [file]
  (when file
    (let [md (MessageDigest/getInstance "SHA-256")
          is (io/input-stream file)
          dis (DigestInputStream. is md)
          bs (byte-array 1024)]
      (loop [] (when (not= (.read dis bs 0 1024) -1) (recur)))
      (-> (.digest md)
          Base64/encodeBase64String))))
