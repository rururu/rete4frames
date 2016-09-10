(ns rete.protege
  (:require rete.core)
  (:gen-class))

(defn -main []
  (println "\nProtege-3.5")
  (println "A free, open-source ontology editor and framework for building intelligent systems")
  (println "(http://protege.stanford.edu/)\n")
  (edu.stanford.smi.protege.Application/main (into-array ["pro-projects/RuleEngine.pprj"])))
