## Java Interface ##

Java interface consists of static methods in rete.jin. class.
<b>
Important note: you need to put your calls of rete.jin. methods into Var.pushThreadBindings()/ Var.popThreadBindings() wrapper to prevent exception on modification of namespace variable *ns* from root. See example in [Eclipse project] (https://github.com/rururu/rete4frames/blob/master/Rete4framesEclipseTest/src/test/ReteTest.java).
</b>

### 1. static void rete.jin.reteApp(String modes, String trff-path-or-url) ###

Run the application.
- trff-path-or-url (String) - a path (url) to a file containing a list of templates, rules, functions and facts.
- modes (String):
- "run:asynch" - run the application asynchronously, that is assert all facts into the working memory before firing any rules.
- "run:synch"  - run the application synchronously, that is assert first fact and fire rules then assert second fact and fire rules and so on.
- "trace:asynch" - same with a tracing of creation of the rete network and tracing of firing rules and assertion and retraction of facts asynchronously.
- "trace:synch"  - same synchronously.
- "step:asynch"  - same as "trace:asynch" with a stop after firing one rule. Further execution can be continued by calling methods fire(<n>) where <n> - a number of steps (firings of one rule) or or fireAll(), then fire till the very end.

In the modes with the tracing in a root directory are created three files:

- alpha-net-plan.txt	 - describes alpha part of the rete network.
- beta-net-plan.txt	 - describes beta part of the rete network.
- alpha-beta-links.txt - describes the links between alpha part and beta part of the rete network.

### 2. static void rete.jin.reteAppFacts(String modes, String trf-path-or-url, String facts-path-or-url) ###

Run the application.
- trf-path-or-url (String) - a path (url) to a file containing a list of templates, rules and functions.
- facts-path-or-url (String) - a path (url) to a file containing a list of facts.
- modes (String) - same as above.

### 3. static void rete.jin.reteAppString(String modes, String trff) ###

Run the application.
- trff (String) - a string containing a list of templates, rules, functions and facts.
- modes (String) - same as above.

### 4. static void rete.jin.reteAppStringFacts(String modes, String trf, String facts-path-or-url) ###

Run the application.
- trf (String) - a string containing a list of templates, rules and functions.
- facts-path-or-url (String) - a path (url) to a file containing a list of facts.
- modes (String) - same as above.

### 5. static java.util.HashMap rete.jin.allFacts() ###

Returns current content of a fact memory in a form of java.util.HashMap. KeySet of the HashMap consists of string names of types of facts in the fact memory. Values of the HashMap are collections of HashMaps representing facts of types corresponding to keys. KeySet of the HashMap representing a fact consists of names of slots. Values of this HashMap are values of slots.

### 6. static java.util.Collection rete.jin.factsOfType(String type-of-facts) ###

Returns a collection of facts of the given type.
- type-of-facts (String) - a string name of the fact type.

### 7. static void rete.jin.assertFact (String type-of-fact, java.util.HashMap slots-values) ###

Assert a fact into the fact memory.
- type-of-facts (String) - a string name of the fact type.
- slots-values (java.util.HashMap) - a HashMap representing the fact. Its keys are names of the fact slots. Its values are values of the fact slots. Note: values of the Clojure symbol type must be represented as strings with preceding single quote mark to distinguish them from those of the Clojure string type.


### 8. static void rete.jin.fireAll() ###

Fire active rules while they exist in the fact memory.

### 9. static void rete.jin.fire(int number-of-rules) ###

Fire a definite number of rules.

### 10. static void rete.jin.trace() ###

Switch tracing on.

### 11. static void rete.jin.untrace() ###

Switch tracing off.

### 12. static void rete.jin.strategyDepth() ###

Set conflict resolution strategy to depth.

### 13. static void rete.jin.strategyBreadth() ###

Set conflict resolution strategy to breadth.

Example of using Java interface to rete4frames is in [Eclipse project] (https://github.com/rururu/rete4frames/blob/master/Rete4framesEclipseTest/src/test/ReteTest.java).

Copyright © 2014 Ruslan Sorokin.
