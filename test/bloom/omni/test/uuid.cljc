(ns bloom.omni.test.uuid
  (:require
    [clojure.test :refer :all]
    [bloom.omni.uuid :refer [uuid]]))

(deftest uuid-test

  (testing "(uuid) returns new uuid"
    (is (uuid? (uuid))))

  (testing "(uuid string) casts to uuid"
    (is (= (uuid "bb0bd8f9-d8b9-4d6e-b145-9a4501569cff")
           #uuid "bb0bd8f9-d8b9-4d6e-b145-9a4501569cff"))))


