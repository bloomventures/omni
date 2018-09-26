(ns bloom.omni.impl.crypto
  (:require
   [clojure.java.io :as io])
  (:import
   (java.security DigestInputStream MessageDigest)
   (javax.crypto Mac)
   (javax.crypto.spec SecretKeySpec)
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

(defn sha256
  ^bytes [^bytes input-bytes]
  (-> (doto (MessageDigest/getInstance "SHA-256")
        (.update input-bytes))
      (.digest)))

(defn hmac-sha256
  ^bytes [^bytes key-bytes ^bytes to-sign-bytes]
  (let [mac (Mac/getInstance "HmacSHA256")
        secret-key (SecretKeySpec. key-bytes (.getAlgorithm mac))]
    (-> (doto mac (.init secret-key))
        (.doFinal to-sign-bytes))))

(defn str->bytes
  ^bytes [s]
  (.getBytes s "UTF-8"))

(defn bytes->hex
  [^bytes bs]
  (->> bs (map (partial format "%02x")) (apply str)))

(def hex-hash (comp bytes->hex sha256 str->bytes))

(defn slow=
  "Do a constant-time comparision check, to avoid timing attack"
  [as bs]
  (loop [as (seq as)
         bs (seq bs)
         eq? false]
    (if (and (empty? as) (empty? bs))
      eq?
      (recur (next as)
             (next bs)
             (= (first as) (first bs))))))
