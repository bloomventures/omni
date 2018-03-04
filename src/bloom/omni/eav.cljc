(ns bloom.omni.eav
  "Provides facitilities from turning nested records into EAVs and back")

(defn ->id [obj]
  (obj :id))

(defn record->eav
  "Given a nested data structure, of maps, vectors and primitives,
   returns a list of corresponding EAV triplets.

  All maps must include an :id.

  Ex.
  ```clojure
  {:id 123
   :value \"Alice\"
   :friend {:id 345
            :name \"Bob\"}}

  =>
  [[123 :id 123]
   [123 :name \"Alice\"]
   [123 :friend 345]
   [345 :id 345]
   [345 :name \"Bob\"]]
  ```"
  [record]
  (cond
    (map? record)
    (mapcat (fn [[k v]]
              (cond
                (map? v)
                (concat
                  [[(->id record) k (->id v)]]
                  (record->eav v))

                (vector? v)
                (mapcat (fn [v']
                          (cond
                            (map? v')
                            (concat [[(->id record) k (->id v')]]
                                    (record->eav v'))
                            :else
                            [[(->id record) k v']]))
                        v)

                :else
                [[(->id record) k v]]))
            record)

    (vector? record)
    (mapcat record->eav record)))

(defn eav->record
  "...

    {:rel :many}

      [[:foo :id :foo]
       [:foo :rel 1]
       [:foo :rel 2]]
       =>
      {:id :foo
       :rel [1 2]}

    {:rel :one}

      [[:foo :id :foo]
       [:foo :rel 1]
       [:foo :rel 2]]
       =>
      {:id :foo
       :rel 2}  ; last one wins
  "
  [eavs rels]
  (let [->e (fn [[e a v]] e)
        ->a (fn [[e a v]] a)
        ->v (fn [[e a v]] v)
        ; records-lookup are eavs converted as follows:
        ; [123 :id 123]
        ; [123 :value :a]
        ; [999 :id 999]
        ; [999 :value :b]
        ; =>
        ; {123 {:id [123]
        ;       :value [:a]}
        ;  999 {:id [999]
        ;       :value [:b]}}
        records-lookup (->> eavs
                            (group-by ->e)
                            (mapv (fn [[e eavs]]
                                    [e (->> eavs
                                            (group-by ->a)
                                            (map (fn [[a eavs]]
                                                   [a (mapv ->v eavs)]))
                                            (into {}))]))
                            (into {}))
        ids-to-remove (atom #{})
        lookup (fn [id]
                 (swap! ids-to-remove conj id)
                 (records-lookup id))
        fix-rels (fn fix-rels [record]
                   (->> record
                        (map (fn [[k vs]]
                               (case (rels k)

                                 :embed-many
                                 [k (->> vs
                                         (map lookup)
                                         (mapv fix-rels))]

                                 :reference-many
                                 [k vs]

                                 :many
                                 [k vs]

                                 :embed-one
                                 [k (->> vs
                                         last
                                         lookup
                                         fix-rels)]


                                 :reference-one
                                 [k (last vs)]

                                 :one
                                 [k (last vs)]

                                 ; no rels definition
                                 [k (last vs)])))
                        (into {})))]
    (->> (vals records-lookup)
         (map fix-rels)
         doall
         (remove (fn [record]
                   (contains? @ids-to-remove (record :id))))
         vec)))
