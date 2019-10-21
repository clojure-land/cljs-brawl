(ns brawl.svg
  (:require [tubax.core :refer [xml->clj]]
            [clojure.string :as s]))


(defn ppth
  "parse path, convert string pairs starting with M L to coord array"
  [path]
  (reduce
   (fn extract [res act]
     (cond
       (or (s/starts-with? act "M") (s/starts-with? act "L"))
       (conj res (map cljs.reader/read-string (s/split (subs act 1) #",")))
       :else
       res))
   []
   (s/split path #" ")))


(defn pshp
  "parse shape, extract color and path from xml node"
  [attrs id]
  {:type "shape"
   :id id
   :color (if (contains? attrs :fill)
            (js/parseInt (subs (attrs :fill) 1) 16)
            0)`
   :path (ppth (attrs :d))})


(defn psvg
  "parse svg recursively"
  [element id]
  (if (not= nil element)
    (cond
      (map? element)
      (let [{:keys [tag attributes content]} element]
        (cond
          (= tag :g)
          (psvg content (attributes :id))
          (= tag :path)
          (concat [(pshp attributes id)] (psvg content id))
          :else
          (psvg content id)))
      (vector? element)
      (reduce #(concat %1 (psvg %2 id)) [] element))))

;;(brawl.svg/psvg (xml/parse "level0.svg") "")

