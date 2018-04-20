(ns bloom.omni.eav
  "Provides facilities for turning records into EAVs and back.")

(defn- make->id [schema]
  (let [id-keys (->> schema
                     (filter (fn [[k v]]
                               (= v :id)))
                     (map first)
                     set)]
    (fn [r]
      (->> id-keys
           (map (fn [k]
                  (r k)))
           (remove nil?)
           first))))

(defn recs->eavs
  "Converts vector of records to their corresponding EAVs.
  
  Records can be a nested data structure of maps, vectors and primitives.

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
  [->eid records schema]
  (let [->id (make->id schema)
        id->r (->> records
                   (reduce (fn [memo r]
                             (assoc memo (->id r) r)) {}))
        ->eavs (fn ->eavs [record]
                 (mapcat (fn [[k v]]
                           (case (schema k)
                             :reference-many
                             (mapcat (fn [v']
                                       [[(->eid record) k (->eid (id->r v'))]])
                                     v)

                             :reference-one
                             [[(->eid record) k (->eid (id->r v))]]

                             :embed-many
                             (mapcat (fn [v']
                                       (concat [[(->eid record) k (->eid v')]]
                                               (->eavs v')))
                                     v)

                             :embed-one
                             (concat
                               [[(->eid record) k (->eid v)]]
                               (->eavs v))

                             :many
                             (mapcat (fn [v']
                                       [[(->eid record) k v']])
                                     v)

                             ; else
                             [[(->eid record) k v]]))
                         record))]
    (mapcat ->eavs records)))

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
  [->eid eavs schema]
  (let [->e (fn [[e a v]] e)
        ->a (fn [[e a v]] a)
        ->v (fn [[e a v]] v)
        ; records-lookup are eavs converted as follows:
        ; [e123 :id 123]
        ; [e123 :value :a]
        ; [e999 :id 999]
        ; [e999 :value :b]
        ; =>
        ; {e123 {:id [123]
        ;        :value [:a]}
        ;  e999 {:id [999]
        ;        :value [:b]}}
        records-lookup (->> eavs
                            (group-by ->e)
                            (mapv (fn [[e eavs]]
                                    [e (->> eavs
                                            (group-by ->a)
                                            (map (fn [[a eavs]]
                                                   [a (mapv ->v eavs)]))
                                            (into {}))]))
                            (into {}))
        ->id (make->id schema)
        ids-to-remove (atom #{})
        lookup (fn [id]
                 (swap! ids-to-remove conj id)
                 (records-lookup id))
        fix-rels (fn fix-rels [record]
                   (->> record
                        (map (fn [[k vs]]
                               (case (schema k)

                                 :embed-many
                                 [k (->> vs
                                         (map lookup)
                                         (mapv fix-rels))]

                                 :reference-many
                                 [k (->> vs
                                         (mapv (fn [eid]
                                                 (-> eid
                                                     records-lookup
                                                     ->id
                                                     first))))]

                                 :many
                                 [k vs]

                                 :embed-one
                                 [k (->> vs
                                         last
                                         lookup
                                         fix-rels)]

                                 :reference-one
                                 [k (-> vs
                                        last 
                                        records-lookup
                                        ->id
                                        first)]

                                 ; no schema definition
                                 [k (last vs)])))
                        (into {})))]
    (->> (vals records-lookup)
         (map fix-rels)
         doall
         (remove (fn [record]
                   (nil? (->id record))))
         (remove (fn [record]
                   (contains? @ids-to-remove (->eid record))))
         vec)))
