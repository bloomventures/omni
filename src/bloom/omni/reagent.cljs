(ns bloom.omni.reagent
  (:require
    [reagent.dom :as rdom]
    [reagent.impl.protocols :as reagent.p]
    [reagent.impl.template :as reagent.t]
    [goog.object :as o]))

;; exists just to merge :tw props into :className
;; girouette style processor is modded in zig.styles to look for :tw
(defn modded-reagent-compiler []
  (let [id (gensym)
        fn-to-element (fn [tag v compiler]
                        (reagent.t/reag-element tag v compiler))]
    (reify reagent.p/Compiler
      ;; This is used to as cache key to cache component fns per compiler
      (get-id [this] id)
      (parse-tag [this tag-name tag-value]
        (reagent.t/cached-parse this tag-name tag-value))
      (as-element [this x]
        (reagent.t/as-element this x fn-to-element))
      (make-element [this argv component jsprops first-child]
        ;; merge :tw prop into :className
        (when (o/get jsprops "tw")
          (doto jsprops
            (o/set "className"
                   (str (o/get jsprops "className")
                        " " (o/get jsprops "tw")))
            (o/remove "tw")))
        (reagent.t/make-element this argv component jsprops first-child)))))

(defn render
  [component]
  (rdom/render
    component
    (js/document.getElementById "app")
    (modded-reagent-compiler)))
