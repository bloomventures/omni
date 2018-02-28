(ns bloom.omni.uuid
  "Provides (uuid) function, in clj and cljs, for creating random uuids and converting strings to uuids")

(defn uuid
  "When passed a string, attempts to convert it to a UUID.
  When called without arguments, returns a random UUID. "
  ([] 
   #?(:cljs (random-uuid)
      :clj (java.util.UUID/randomUUID)))
  ([uuid-string] 
   #?(:cljs (uuid uuid-string)
      :clj (java.util.UUID/fromString uuid-string))))
