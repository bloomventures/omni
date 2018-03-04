(ns bloom.omni.eav
  "Provides facilities for turning records into EAVs and back.")

(defn ->id [obj]
  (or (:id obj) 
      (hash obj)))

(defn recs->eavs
  "Converts vector of records to their corresponding EAVs.
  
  Records can be a nested data structure of maps, vectors and primitives.

  If a map does not include an :id value, the resulting EAVs 
  will have (hash {...}) as the entity-id.

  Ex.
  ```clojure
  [{:id 123
    :value \"Alice\"
    :friend {:id 345
             :name \"Bob\"}}]

  =>
  [[123 :id 123]
   [123 :name \"Alice\"]
   [123 :friend 345]
   [345 :id 345]
   [345 :name \"Bob\"]]
  ```"
  [records]
  (let [->eavs (fn ->eavs [record]
                 (mapcat (fn [[k v]]
                           (cond
                             (map? v)
                             (concat
                               [[(->id record) k (->id v)]]
                               (->eavs v))

                             (vector? v)
                             (mapcat (fn [v']
                                       (cond
                                         (map? v')
                                         (concat [[(->id record) k (->id v')]]
                                                 (->eavs v'))
                                         :else
                                         [[(->id record) k v']]))
                                     v)

                             :else
                             [[(->id record) k v]]))
                         record))]
    (mapcat ->eavs records)))

(defn recs->rels
  "Given a vector of records, returns the inferred relationship types for their keys."
  [records]
  (let [ids (set (map ->id records))
        ->rels (fn ->rels [record]
                 (cond 
                   (map? record)
                   (apply merge 
                          (map (fn [[k v]]
                                 (cond
                                   (vector? v)
                                   (cond 
                                     (map? (first v))
                                     {k :embed-many}
                                     (contains? ids (first v))
                                     {k :reference-many}
                                     :else
                                     {k :many})
                                   (map? v)
                                   {k :embed-one}

                                   (and 
                                     (not= v (->id record)) 
                                     (contains? ids v))
                                   {k :reference-one}

                                   :else
                                   {}))
                               record))

                   (vector? record)
                   (apply merge 
                          (map ->rels record))))]
    (->rels records)))

(defn eavs->recs
  "Converts a vector of EAVs to their corresponding records.

   Must also pass in a map defining the relationships on reference or multi-arity keys.

   There are 6 types of relationships:
     nil (the default)
     For keys that point to primitive values.

     :many
     For keys that point to a vector of values.

     :reference-once
     For keys that point to a primitive value that is an id of another record.

     :reference-many
     For keys that point to a primitive values that are ids of other records.

     :embed-one
     For keys that point to another record, directly included as a child. 

     :embed-many
     For keys that point to a vector of other records, 
     which are directly included as children.

     (Embedded records will not be returned on the top-level, 
      but they may be repeated as embedded children in other records.)

  See tests for examples."
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

                                 ; no rels definition
                                 [k (last vs)])))
                        (into {})))]
    (->> (vals records-lookup)
         (map fix-rels)
         doall
         (remove (fn [record]
                   (nil? (record :id))))
         (remove (fn [record]
                   (contains? @ids-to-remove (record :id))))
         vec)))
