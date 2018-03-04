(ns bloom.omni.test.eav
  (:require
    [clojure.test :refer :all]
    [bloom.omni.eav :as eav]))

(def tests
  [{:id "single rec, no rels"
    :recs [{:id "foo"
            :value "bar"}]
    :eavs [["foo" :id "foo"]
           ["foo" :value "bar"]]
    :rels {}}

   {:id "multiple recs, no rels"
    :recs [{:id "foo"
            :value "bar"}
           {:id "abc"
            :value "xyz"}]
    :eavs [["foo" :id "foo"]
           ["foo" :value "bar"]
           ["abc" :id "abc"]
           ["abc" :value "xyz"]]
    :rels {}}

   {:id "rels: many"
    :recs [{:id "foo"
            :rel ["a" "b" "c"]}]
    :eavs [["foo" :id "foo"]
           ["foo" :rel "a"]
           ["foo" :rel "b"]
           ["foo" :rel "c"]]
    :rels {:rel :many}}

   {:id "rels: embed-many"
    :recs [{:id "foo"
            :rel [{:id "a"
                   :value 0}
                  {:id "b"
                   :value 1}]}]
    :eavs [["foo" :id "foo"]
           ["foo" :rel "a"]
           ["a" :id "a"]
           ["a" :value 0]
           ["foo" :rel "b"]
           ["b" :id "b"]
           ["b" :value 1]]
    :rels {:rel :embed-many}}

   {:id "rels: reference-many"
    :recs [{:id "foo"
            :rel ["a" "b"]}
           {:id "a"
            :value 0}
           {:id "b"
            :value 1}]
    :eavs [["foo" :id "foo"]
           ["foo" :rel "a"]
           ["a" :id "a"]
           ["a" :value 0]
           ["foo" :rel "b"]
           ["b" :id "b"]
           ["b" :value 1]]
    :rels {:rel :reference-many}}

   {:id "rels: embed-one"
    :recs [{:id "foo"
            :rel {:id "abc"
                  :value "xyz"}}]
    :eavs [["foo" :id "foo"]
           ["foo" :rel "abc"]
           ["abc" :id "abc"]
           ["abc" :value "xyz"]]
    :rels {:rel :embed-one}}

   {:id "rels: reference-one"
    :recs [{:id "foo"
            :rel "abc"}
           {:id "abc"
            :value "xyz"}]
    :eavs [["foo" :id "foo"]
           ["foo" :rel "abc"]
           ["abc" :id "abc"]
           ["abc" :value "xyz"]]
    :rels {:rel :reference-one}}

   {:id "rels: embed-many nested one level"
    :recs [{:id "a"
            :rel [{:id "b"
                   :rel [{:id "c"
                          :value 1}]}]}]
    :eavs [["a" :id "a"]
           ["a" :rel "b"]
           ["b" :id "b"]
           ["b" :rel "c"]
           ["c" :id "c"]
           ["c" :value 1]]
    :rels {:rel :embed-many}}

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
    :eavs [["a" :id "a"]
           ["b" :id "b"]
           ["aa" :id "aa"]
           ["ab" :id "ab"]
           ["ba" :id "ba"]
           ["bb" :id "bb"]
           ["aaa" :id "aaa"]
           ["aab" :id "aab"]
           ["aba" :id "aba"]
           ["abb" :id "abb"]
           ["baa" :id "baa"]
           ["bab" :id "bab"]
           ["bba" :id "bba"]
           ["bbb" :id "bbb"]
           ["aaa" :value 1]
           ["aab" :value 2]
           ["aba" :value 3]
           ["abb" :value 4]
           ["baa" :value 5]
           ["bab" :value 6]
           ["bba" :value 7]
           ["bbb" :value 8]
           ["a" :rel "aa"]
           ["aa" :rel "aaa"]
           ["aa" :rel "aab"]
           ["a" :rel "ab"]
           ["ab" :rel "aba"]
           ["ab" :rel "abb"]
           ["b" :rel "ba"]
           ["ba" :rel "baa"]
           ["ba" :rel "bab"]
           ["b" :rel "bb"]
           ["bb" :rel "bba"]
           ["bb" :rel "bbb"]]
    :rels {:rel :embed-many}}
   
   {:id "comprehensive, multi-rels"
    :recs [{:id :a
            :name "Alice"
            :emails ["alice@example.com"
                     "a@example.com"]
            :friend-ids [:b :c :d]
            :best-friend-id :b
            :location {:id :toronto
                       :city "Toronto"
                       :country "Canada"}
            :pets [{:name "Doggo"
                    :type :dog}
                   {:name "Woofles"
                    :type :dog}]}
           {:id :b
            :name "Bob"
            :emails ["bob@example.com"]
            :friend-ids [:a]
            :pets [{:name "Meowsers"
                    :type :cat}]}
           #_{:id :c
              :name "Cathy"
              :emails []
              :friend-ids []
              :pets []}
           {:id :d
            :name "Donald"}]
    :eavs [; a
           [:a :id :a]
           [:a :name "Alice"]
           [:a :emails "alice@example.com"]
           [:a :emails "a@example.com"]
           [:a :friend-ids :b]
           [:a :friend-ids :c]
           [:a :friend-ids :d]
           [:a :best-friend-id :b]
           [:a :location :toronto]
           [:toronto :id :toronto]
           [:toronto :city "Toronto"]
           [:toronto :country "Canada"]
           [:a :pets -1611178140]
           [-1611178140 :name "Doggo"]
           [-1611178140 :type :dog]
           [:a :pets 657552642]
           [657552642 :name "Woofles"]
           [657552642 :type :dog]
           ; b
           [:b :id :b]
           [:b :name "Bob"]
           [:b :emails "bob@example.com"]
           [:b :friend-ids :a]
           [:b :pets 579057835]
           [579057835 :name "Meowsers"]
           [579057835 :type :cat]
           ; c
           #_[:c :id :c]
           #_[:c :name "Cathy"]
           ; d
           [:d :id :d]
           [:d :name "Donald"]]
    :rels {:emails :many
           :friend-ids :reference-many
           :best-friend-id :reference-one
           :location :embed-one
           :pets :embed-many}}])

(deftest all
  (doseq [t tests]
    (testing (str "test " (t :id))

      (testing "eavs->recs"
        (is (= (set (t :recs)) 
               (set (eav/eavs->recs
                      (t :eavs)
                      (t :rels))))))

      (testing "recs->eav"
        (is (= (set (t :eavs)) 
               (set (eav/recs->eavs
                      (t :recs))))))

      (testing "recs->rels"
        (is (= (t :rels) 
               (eav/recs->rels
                 (t :recs)))))

      (testing "eavs->recs -> recs->eav"
        (is (= (set (t :eavs))
               (set (eav/recs->eavs (eav/eavs->recs (t :eavs)
                                                    (t :rels))))))) 

      (testing "recs->eav -> eavs->recs"
        (is (= (set (t :recs))
               (set (eav/eavs->recs (eav/recs->eavs (t :recs))
                                    (eav/recs->rels (t :recs))))))))))

