(defproject rete "5.2.5-SNAPSHOT"
  :description "Clojure RETE implementation for frames"
  :url "https://github.com/rururu/rete4frames"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [protege "3.5.0"]
                 [protege/standard-extensions "3.5.0"]
                 [protege/looks "3.5.0"]
                 [protege/unicode_panel "3.5.0"]
                 [protege/JGo "3.5.0"]
                 [protege/JGoLayout "3.5.0"]
                 [protege/ClojureTab "1.5.0"]]
  :javac-options ["-target" "1.7" "-source" "1.7" "-Xlint:-options"]
  :repositories {"local" ~(str (.toURI (java.io.File. "repo")))}
  :aot [rete.protege]
  :main rete.protege)

