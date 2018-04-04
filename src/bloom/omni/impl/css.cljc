(ns bloom.omni.impl.css
  (:require
    [clojure.string :as string]
    [garden.core :as garden]))

; css class-names allow a-z 0-9 - and _; 
; but, with restrictions for the first character, so, we just stick to a-z
(def hash-alphabet "abcdefghijklmnopqrstuvwxyz")

(defn- abs [i]
  (if (pos? i) i (* -1 i)))

(defn- int->base26 
  "Given an integer, hash using base26"
  [i]
  (let [i (abs i)]
    (if (< (abs i) (count hash-alphabet))
      (get hash-alphabet (abs i))
      (let [remainder (mod i 26)]
        (str (get hash-alphabet remainder) 
             (int->base26 (dec (int (/ i (count hash-alphabet))))))))))

(defn- ->class-name
  "Given a garden style vector, generate a unique css class name."
  [rules]
  (int->base26 (hash rules)))

(defn ->class
  [rules]
  (-> rules
      first
      (clojure.string/replace-first "." "")))

(defn ->styles
  [rules]
  [(str "." (->class-name rules))
   rules])

#?(:clj 
   (defn compile-css [{:keys [pretty-print? styles]}]
     (garden/css 
       {:pretty-print? pretty-print?}
       ((var-get (find-var styles))))))
