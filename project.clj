(defproject rete "5.2.4-SNAPSHOT"
  :description "Clojure RETE implementation for frames"
  :url "https://github.com/rururu/rete4frames"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :jvm-opts ["-Xmx2000M"]
  :aot :all
  :main rete.core
  :dependencies [[org.clojure/clojure "1.8.0"]])
