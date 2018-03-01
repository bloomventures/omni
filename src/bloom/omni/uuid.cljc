(ns bloom.omni.uuid
  "Provides clj+cljs functions for working with uuids.

  ```clojure
  (require '[bloom.omni.uuid :as uuid])
   
  (uuid/random) 
   
  (uuid/from-string s)
  ```")

(defn random 
  "Generates a random v4 UUID"
  []
  #?(:cljs (random-uuid)
     :clj (java.util.UUID/randomUUID)))

(defn from-string 
  "Converts string representation of uuid into native UUID"
  [uuid-string]
  #?(:cljs (cljs.core/uuid uuid-string)
     :clj (java.util.UUID/fromString uuid-string)))
