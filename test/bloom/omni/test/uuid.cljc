(ns bloom.omni.test.uuid
  (:require
    [clojure.test :refer :all]
    [bloom.omni.uuid :as uuid]))

(deftest uuid-test

  (testing "(uuid/random) returns new uuid"
    (is (uuid? (uuid/random))))

  (testing "(uuid/from-string string) casts to uuid"
    (is (= (uuid/from-string "bb0bd8f9-d8b9-4d6e-b145-9a4501569cff")
           #uuid "bb0bd8f9-d8b9-4d6e-b145-9a4501569cff"))))


