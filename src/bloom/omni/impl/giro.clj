(ns bloom.omni.impl.giro
  "Replacement for girouette.processor using rewrite-clj"
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [garden.core :as garden]
   [girouette.garden.util :as util]
   [girouette.tw.default-api :as giro.tw]
   [malli.core :as m]
   [malli.error :as me]
   [nextjournal.beholder :as beholder]
   [rewrite-clj.node :as zn]
   [rewrite-clj.zip :as z])
  (:import
   (java.io File)
   (java.nio.file Path)))

(defn- string->classes [s]
  (->> (str/split s #"\s+")
       (remove str/blank?)))

(defn- kw->classes [kw]
  (->> (name kw)
       (re-seq #"\.[^\.#]+")
       (map (fn [s] (subs s 1)))))

;; Walks every leaf node in the zipper, collecting strings and keywords.
(defn- gather-classes-comprehensive [zloc]
  (let [css-classes (atom #{})]
    (loop [loc zloc]
      (when-not (z/end? loc)
        (when (and (z/sexpr-able? loc)
                   (not (zn/inner? (z/node loc))))
          (let [v (z/sexpr loc)]
            (cond
              (string? v)  (swap! css-classes into (string->classes v))
              (keyword? v) (swap! css-classes into (kw->classes v)))))
        (recur (z/next loc))))
    @css-classes))

(defn- gather-css-classes [file]
  {:css-classes (->> (z/of-file (str file))
                     gather-classes-comprehensive
                     sort
                     vec)})

#_(gather-css-classes (io/file "../../example/reagent-demo/src/acme/frontend/app.cljc"))



;; {File {:css-classes ["flex" "flex-1" "p-4"]}}
(def file-data (atom {}))

(defn- spit-output [config]
  (let [{:keys [output-file garden-fn base-css-rules]} config
        file-parent (-> (io/file output-file) (.getParent) (io/file))]
    (when-not (.exists file-parent)
      (.mkdirs file-parent))
    (let [all-css-classes (into #{} (mapcat :css-classes) (vals @file-data))
          predef-garden   (into [] (mapcat (fn [symb] @(requiring-resolve symb))) base-css-rules)
          all-garden-defs (-> predef-garden
                              (into (->> (keep garden-fn all-css-classes)
                                         (sort util/rule-comparator))))]
      (spit output-file (garden/css all-garden-defs)))))

(defn- on-file-changed [^File file change-type]
  (try
    (case change-type
      :delete           (swap! file-data dissoc file)
      (:create :modify) (swap! file-data assoc file (gather-css-classes file)))
    (catch Exception _
      (println (str file ": parse error!")))))

(def ^:private process-schema
  [:map
   [:source-paths [:sequential :string]]
   [:file-filters [:sequential :string]]
   [:output-file :string]
   [:garden-fn :any]
   [:base-css-rules [:vector [:fn qualified-symbol?]]]
   [:watch? :boolean]])

;; Entry point of the simple processor tool.
(defn process
  [{:keys [source-paths file-filters output-file garden-fn base-css-rules watch?]
    :as opts}]
  (let [config (merge {:output-file    "girouette.css"
                       :base-css-rules []
                       :file-filters   [".cljs" ".cljc"]
                       :source-paths   ["src"]
                       :watch?         false
                       :garden-fn      giro.tw/tw-v3-class-name->garden}
                      opts)
        input-file? (fn [^File file]
                      (let [path (.getPath file)]
                        (some (fn [ext]
                                (str/ends-with? path ext))
                              file-filters)))]

    (when-let [err (m/explain process-schema config)]
      (throw (ex-info "Invalid parameters:" (me/humanize err))))

    (doseq [^File file (->> source-paths
                            (map io/file)
                            (mapcat file-seq)
                            (filter input-file?))]
      (on-file-changed file :create))

    (spit-output config)

    (when watch?
      (apply
       beholder/watch
       (fn [{:keys [type ^Path path]}]
         (let [file ^File (.toFile path)]
           (when (input-file? file)
             (on-file-changed file type)
             (spit-output config))))
       source-paths))))
