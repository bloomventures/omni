(ns bloom.omni.test.eav
  (:require
    [clojure.test :refer :all]
    [bloom.omni.eav :as eav]))

(defn ->id [obj]
  (str "_" (or (obj :id) 
               (obj :person/id) 
               (obj :location/id) 
               (obj :pet/name))))

(def tests
  [{:id "single rec, no rels"
    :recs [{:id "foo"
            :value "bar"}]
    :eavs [["_foo" :id "foo"]
           ["_foo" :value "bar"]]
    :schema {:id :id}}

   {:id "multiple recs, no rels"
    :recs [{:id "foo"
            :value "bar"}
           {:id "abc"
            :value "xyz"}]
    :eavs [["_foo" :id "foo"]
           ["_foo" :value "bar"]
           ["_abc" :id "abc"]
           ["_abc" :value "xyz"]]
    :schema {:id :id}}

   {:id "rels: many"
    :recs [{:id "foo"
            :rel ["a" "b" "c"]}]
    :eavs [["_foo" :id "foo"]
           ["_foo" :rel "a"]
           ["_foo" :rel "b"]
           ["_foo" :rel "c"]]
    :schema {:id :id
             :rel :many}}

   {:id "rels: embed-many"
    :recs [{:id "foo"
            :rel [{:id "a"
                   :value 0}
                  {:id "b"
                   :value 1}]}]
    :eavs [["_foo" :id "foo"]
           ["_foo" :rel "_a"]
           ["_a" :id "a"]
           ["_a" :value 0]
           ["_foo" :rel "_b"]
           ["_b" :id "b"]
           ["_b" :value 1]]
    :schema {:id :id
             :rel :embed-many}}

   {:id "rels: reference-many"
    :recs [{:id "foo"
            :rel ["a" "b"]}
           {:id "a"
            :value 0}
           {:id "b"
            :value 1}]
    :eavs [["_foo" :id "foo"]
           ["_foo" :rel "_a"]
           ["_a" :id "a"]
           ["_a" :value 0]
           ["_foo" :rel "_b"]
           ["_b" :id "b"]
           ["_b" :value 1]]
    :schema {:id :id
             :rel :reference-many}}

   {:id "rels: embed-one"
    :recs [{:id "foo"
            :rel {:id "abc"
                  :value "xyz"}}]
    :eavs [["_foo" :id "foo"]
           ["_foo" :rel "_abc"]
           ["_abc" :id "abc"]
           ["_abc" :value "xyz"]]
    :schema {:id :id
             :rel :embed-one}}

   {:id "rels: reference-one"
    :recs [{:id "foo"
            :rel "abc"}
           {:id "abc"
            :value "xyz"}]
    :eavs [["_foo" :id "foo"]
           ["_foo" :rel "_abc"]
           ["_abc" :id "abc"]
           ["_abc" :value "xyz"]]
    :schema {:id :id
             :rel :reference-one}}

   {:id "rels: embed-many nested one level"
    :recs [{:id "a"
            :rel [{:id "b"
                   :rel [{:id "c"
                          :value 1}]}]}]
    :eavs [["_a" :id "a"]
           ["_a" :rel "_b"]
           ["_b" :id "b"]
           ["_b" :rel "_c"]
           ["_c" :id "c"]
           ["_c" :value 1]]
    :schema {:id :id
             :rel :embed-many}}

   {:id "rels: embed-many nested two levels"
    :recs [{:id "a"
            :rel [{:id "aa"
                   :rel [{:id "aaa"
                          :value 1}
                         {:id "aab"
                          :value 2}]}
                  {:id "ab"
                   :rel [{:id "aba"
                          :value 3}
                         {:id "abb"
                          :value 4}]}]}
           {:id "b"
            :rel [{:id "ba"
                   :rel [{:id "baa"
                          :value 5}
                         {:id "bab"
                          :value 6}]}
                  {:id "bb"
                   :rel [{:id "bba"
                          :value 7}
                         {:id "bbb"
                          :value 8}]}]}]
    :eavs [["_a" :id "a"]
           ["_b" :id "b"]
           ["_aa" :id "aa"]
           ["_ab" :id "ab"]
           ["_ba" :id "ba"]
           ["_bb" :id "bb"]
           ["_aaa" :id "aaa"]
           ["_aab" :id "aab"]
           ["_aba" :id "aba"]
           ["_abb" :id "abb"]
           ["_baa" :id "baa"]
           ["_bab" :id "bab"]
           ["_bba" :id "bba"]
           ["_bbb" :id "bbb"]
           ["_aaa" :value 1]
           ["_aab" :value 2]
           ["_aba" :value 3]
           ["_abb" :value 4]
           ["_baa" :value 5]
           ["_bab" :value 6]
           ["_bba" :value 7]
           ["_bbb" :value 8]
           ["_a" :rel "_aa"]
           ["_aa" :rel "_aaa"]
           ["_aa" :rel "_aab"]
           ["_a" :rel "_ab"]
           ["_ab" :rel "_aba"]
           ["_ab" :rel "_abb"]
           ["_b" :rel "_ba"]
           ["_ba" :rel "_baa"]
           ["_ba" :rel "_bab"]
           ["_b" :rel "_bb"]
           ["_bb" :rel "_bba"]
           ["_bb" :rel "_bbb"]]
    :schema {:id :id
             :rel :embed-many}}

   {:id "comprehensive, multi-rels"
    :recs [{:person/id :a
            :person/name "Alice"
            :person/emails ["alice@example.com"
                            "a@example.com"]
            :person/friend-ids [:b :d]
            :person/best-friend-id :b
            :person/location {:location/id :toronto
                              :location/city "Toronto"
                              :location/country "Canada"}
            :person/pets [{:pet/name "Doggo"
                           :pet/type :dog}
                          {:pet/name "Woofles"
                           :pet/type :dog}]}
           {:person/id :b
            :person/name "Bob"
            :person/emails ["bob@example.com"]
            :person/friend-ids [:a]
            :person/pets [{:pet/name "Meowsers"
                           :pet/type :cat}]}
           #_{:person/id :c
              :person/name "Cathy"
              :person/emails []
              :person/friend-ids []
              :person/pets []}
           {:person/id :d
            :person/name "Donald"}]
    :eavs [; a
           ["_:a" :person/id :a]
           ["_:a" :person/name "Alice"]
           ["_:a" :person/emails "alice@example.com"]
           ["_:a" :person/emails "a@example.com"]
           ["_:a" :person/friend-ids "_:b"]
           ["_:a" :person/friend-ids "_:d"]
           ["_:a" :person/best-friend-id "_:b"]
           ["_:a" :person/location "_:toronto"]
           ["_:toronto" :location/id :toronto]
           ["_:toronto" :location/city "Toronto"]
           ["_:toronto" :location/country "Canada"]
           ["_:a" :person/pets "_Doggo"]
           ["_Doggo" :pet/name "Doggo"]
           ["_Doggo" :pet/type :dog]
           ["_:a" :person/pets "_Woofles"]
           ["_Woofles" :pet/name "Woofles"]
           ["_Woofles" :pet/type :dog]
           ; b
           ["_:b" :person/id :b]
           ["_:b" :person/name "Bob"]
           ["_:b" :person/emails "bob@example.com"]
           ["_:b" :person/friend-ids "_:a"]
           ["_:b" :person/pets "_Meowsers"]
           ["_Meowsers" :pet/name "Meowsers"]
           ["_Meowsers" :pet/type :cat]
           ; c
           #_[:c :id :c]
           #_[:c :name "Cathy"]
           ; d
           ["_:d" :person/id :d]
           ["_:d" :person/name "Donald"]]
    :schema {:person/id :id
             :location/id :id
             :pet/name :id
             :person/emails :many
             :person/friend-ids :reference-many
             :person/best-friend-id :reference-one
             :person/location :embed-one
             :person/pets :embed-many}}])

(deftest all
  (doseq [t tests]
    (testing (str "test " (t :id))

      (testing "eavs->recs"
        (is (= (set (t :recs)) 
               (set (eav/eavs->recs
                      ->id
                      (t :eavs)
                      (t :schema))))))

      (testing "recs->eav"
          (is (= (set (t :eavs)) 
                 (set (eav/recs->eavs ->id
                                      (t :recs)
                                      (t :schema))))))

      
      (testing "eavs->recs -> recs->eav"
        (is (= (set (t :eavs))
               (set (eav/recs->eavs ->id 
                                    (eav/eavs->recs ->id
                                                    (t :eavs)
                                                    (t :schema))
                                    (t :schema)))))) 

      (testing "recs->eav -> eavs->recs"
        (is (= (set (t :recs))
               (set (eav/eavs->recs ->id
                                    (eav/recs->eavs ->id (t :recs) (t :schema))
                                    (t :schema)))))))))


(deftest namespace-keys
  (testing "namespace-keys"

    (is (= {:ns/foo 1
            :ns/bar 2}
           (eav/namespace-keys (fn [_]
                                 :ns)
                               {:foo 1
                                :bar 2})))
    
    (is (= [{:ns/foo 1
             :ns/bar 2}
            {:ns/foo 3
             :ns/bar 4}]
           (eav/namespace-keys (fn [_]
                                 :ns)
                               [{:foo 1
                                 :bar 2}
                                {:foo 3
                                 :bar 4}])))
    
    (is (= [{:person/name "Alice"
             :person/type :person
             :person/pets [{:pet/name "Meowsers"
                            :pet/type :pet}]}
            {:person/name "Bob"
             :person/type :person}]
           (eav/namespace-keys (fn [r]
                                 (r :type))
                               [{:name "Alice"
                                 :type :person
                                 :pets [{:name "Meowsers"
                                         :type :pet}]}
                                {:name "Bob"
                                 :type :person}])))))
