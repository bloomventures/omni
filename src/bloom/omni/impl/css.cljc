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
  "Given a garden style vector, generate a unique css class-name."
  [rules]
  (int->base26 (hash rules)))

(def styles (atom {}))

(defn- add-style! [class-name rules]
  (swap! styles assoc class-name 
         [(str "." class-name)
          rules]))

(defn register-style! [rules]
  (let [class-name (->class-name rules)]
    (add-style! class-name rules)
    class-name))

(defn compile-css [{:keys [main pretty-print?]}]
  (reset! styles {})
  (require (symbol main) :reload-all)
  (garden/css 
    {:pretty-print? pretty-print?}
    (vals @styles)))

#?(:clj 
   (defn compile! [{:keys [output-to] :as css-config}]

     (.mkdir (java.io.File. (.getParent (java.io.File. output-to))))

     (spit output-to (compile-css css-config))))
