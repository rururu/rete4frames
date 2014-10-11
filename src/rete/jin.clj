(ns rete.jin
  (:require [rete.core :as rete])
  (:gen-class
    :name rete.jin
    :methods [#^{:static true} [reteApp [String String] void]
              #^{:static true} [reteAppString [String String] void]
              #^{:static true} [reteAppFacts [String String String] void]
              #^{:static true} [reteAppStringFacts [String String String] void]
              #^{:static true} [allFacts [] java.util.HashMap]
              #^{:static true} [factsOfType [String] clojure.lang.ChunkedCons]
              #^{:static true} [assertFact [String java.util.HashMap] void]
              #^{:static true} [fireAll [] void]
              #^{:static true} [fire [int] void]
              #^{:static true} [trace [] void]
              #^{:static true} [untrace [] void]
              #^{:static true} [strategyDepth [] void]
              #^{:static true} [strategyBreadth [] void]])
  (:import java.util.HashMap))

(defn symbol-if [s]
  "Convert string into symbol if it begins with quote symbol"
  (if (.startsWith s "'")
    (symbol (.substring s 1))
    s))

(defn -reteApp [mode trff-path-url]
  "Callable from Java function - run rete4frames with modes on templates, rules, functions and facts from file on trff-path or -url"
  (let [trff (slurp trff-path-url)]
    (rete/run-with-mode mode (read-string trff))))

(defn -reteAppString [mode trff]
  "Callable from Java function - run rete4frames with modes on templates, rules, functions and facts from string trff"
  (rete/run-with-mode mode (read-string trff)))

(defn -reteAppFacts [mode trf-path-url f-path-url]
  "Callable from Java function - run rete4frames with modes on templates, rules and functions from file on trf-path or -url, facts from f-path or -url"
  (let [trf (slurp trf-path-url)
        f (slurp f-path-url)]
    (rete/run-with-mode mode (read-string trf) (read-string f))))

(defn -reteAppStringFacts [mode trf f-path-url]
  "Callable from Java function - run rete4frames with modes on templates, rules and functions from string trf, facts from from f-path or -url"
  (let [f (slurp f-path-url)]
    (rete/run-with-mode mode (read-string trf) (read-string f))))

(defn -assertFact [typ slot-value-hm]
  "Callable from Java function - assert fact in form of type and HashMaps of slot values"
  (let [mp (into {} slot-value-hm)
        mp2 (reduce-kv #(assoc %1 (read-string %2) (read-string %3)) {} mp)
        funarg (rete/to-funarg (symbol typ) mp2)]
    (rete/activate-a (rete/ais-for-funarg funarg))))

(defn -fireAll []
  "Callable from Java function - fire rules while exist activations"
  (rete/fire))

(defn -fire [n]
  "Callable from Java function - fire rules n times"
  (rete/fire n))

(defn -trace []
  "Callable from Java function - swith on tracing"
  (rete/trace))

(defn -untrace []
  "Callable from Java function - swith on tracing"
  (rete/untrace))

(defn -factsOfType [typ]
  "Callable from Java function - collection of HashMaps representing facts of specific type"
  (seq (for [[fid typ & svs] (rete/fact-list (symbol typ))]
         (let [hm (HashMap.)]
           (doseq [[k v] (partition 2 svs)]
             (.put hm (name k) v))
           hm))))

(defn -allFacts []
  "Callable from Java function - HashMap with keys of existing facts and vlues of collection of HashMaps representing facts of those type"
  (let [ks (keys @rete/FMEM)
        ksn (map name ks)
        hm (HashMap.)]
    (doseq [kn ksn]
      (if-let [fot (seq (-factsOfType kn))]
        (.put hm kn fot)))
    hm))

(defn -strategyDepth []
  "Callable from Java function - set conflict resolution strategy to depth"
  (rete/strategy-depth))

(defn -strategyBreadth []
  "Callable from Java function - set conflict resolution strategy to breadth"
  (rete/strategy-breadth))





