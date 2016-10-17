## Application Program Interface (API) ##

### 1.Function app ###
```clj
(app <mode> <application-file>)
(app <mode> <application-file> <facts-file>)
```
Run the application.
- application-file (string) - a path to a file containing a list of templates, rules, functions and facts.
- facts-file (string) - a path to a file containing a list of facts. If used, this list of facts replaces the list of facts from the <application-file>.
- mode (string):
- "run"  - run the application synchronously, that is assert first fact and fire rules then assert second fact and fire rules and so on.
- "trace" - same with a tracing of creation of the rete network and tracing of firing rules and assertion and retraction of facts.
- "trace-long" - same verbose.
- "step"  - same as "trace" with a stop after firing one rule. Further execution can be continued by calling functions (fire N) or (fire) where N - a number of steps (firings of one rule). If N is ommited then fire till the very end.

In the modes with the tracing in a root directory are created three files:

- alpha-net-plan.txt	 - describes alpha part of the rete network.
- beta-net-plan.txt	 - describes beta part of the rete network.
- alpha-beta-links.txt - describes the links between alpha part and beta part of the rete network.

### 2.Function fact-list ###
```clj
(fact-list)
(fact-list '<type>)
```
Returns a list of facts (of type if supplied) in a working memory.

### 3.Function facts ###
```clj
(facts)
(facts '<type>)
```
Prints a list of facts (of type if supplied) in a working memory.

### 4.Function ppr ###
```clj
(ppr <type>)
```
Pretty prints facts of a specific type or all facts (<type> = :all) in a working memory.

### 5. Function run-with ###
```clj
(run-with <mode> <templates> <rules> <facts>)
```
Create the rete network and run using given mode.
- templates - a list of templates.
- rules - a list of rules.
- facts - a list of facts.

### 6. Functions trace/untrace ###
```clj
(trace)
(untrace)
```
Switches on/off tracing.

### 7. Functions step ###
```clj
(step)
(step N)
```
Fires rules 1 time (N times if supplied).

### 8. Functions run-synch/run-asynch ###
```clj
(run-with-mode <mode> <truff>)
(run-with-mode <mode> <trufs> <facts>)
```
Create the rete network and run run using given mode. Arguments are:
<mode> - "run" or "trace" or "step",
<truff> - a list of a form: ((templates ..) (rules ..) (functions ..) (facts ...)),
<trufs> - a list of a form: ((templates ..) (rules ..) (functions ..)),
<facts> - a list of facts.

### 9. Function create-rete ###
```clj
(create-rete <templates> <rules>)
```
Create the rete network and clear the working memory.
Arguments are the same as for run-with function.

### 10. Function assert-frame ###
```clj
(assert-frame <fact>)
```
Assert a fact into the working memory.

### 11. Function retract-fact ###
```clj
(retract-fact <fact-id>)
```
Retract a fact from the working memory.
- fact-id (integer) - index of the fact in the working memory.

### 12. Function modify-fact ###
```clj
(modify-fact <fact-id> <slot-value-map>)
```
Modify a fact in the working memory.
- fact-id (integer) - index of the fact in the working memory.
- slot-value-map - map of slots and their values.
During modification the old fact is deleted and a new fact created with the changed values ​​of slots according to map.

### 13. Function fire ###
```clj
(fire <number>)
(fire)
```
Fire a number of active rules. If the number is ommited fire till the very end.

### 14. Function reset ###
```clj
(reset)
```
Clear and initialize the working memory.

### 15. Function strategy-depth ###
```clj
(strategy-depth)
```
Set conflict resolution strategy to depth.

### 16. Function strategy-breadth ###
```clj
(strategy-breadth)
```
Set conflict resolution strategy to breadth.

### 17. Function run-loaded-facts ###
```clj
(run-loaded-facts <path>)
```
Load facts from path, assert all of them into working memory and run,
- path - string representing a path to a file.

### 18. Function only-load-facts ###
```clj
(only-load-facts <path>)
```
Load facts from path and assert all of them into working memory,
- path - string representing a path to a file.

### 19. Function save-facts ###
```clj
(save-facts <path>)
(save-facts <types> <path>)
```
Save all facts or facts of types to a file on a path in a format suitable to load,
- path - string representing a path to a file,
- types - collection of types of facts.

### 20. Function slot-value ###
```clj
(slot-value <slot> <fact>)
```
Returns value of a slot of a fact,
- slot - symbol representing a slot of a fact,
- fact - list representing a fact (as of item of result of the function fact-list).

### 21. Function facts-with-slot-value ###
```clj
(facts-with-slot-value <slot> <function> <value>)
(facts-with-slot-value <type> <slot> <function> <value>)
(facts-with-slot-value <type> <slot> <function> <value> <facts>)

```
Returns list of all facts or facts of type with slot values for which (function slot-value value) = true/not nil,
- slot - symbol representing a slot of a fact,
- function - symbol - predicate for selection of facts,
- type - symbol representing a type of a fact,
- value - any value,
- facts - list - preselected facts.

Examples:
```clj
(facts-with-slot-value 'PersonalData 'name = "Alice")
(facts-with-slot-value 'BloodPressure 'systolic > 140)
(facts-with-slot-value 'BloodPressure 'systolic > 140 (facts-with-slot-value 'BloodPressure 'diastolic > 90))
```
### 22. Function frame-by-id ###
```clj
(frame-by-id <fact-id>)
```
Returns a fact,
- fact-id - identifier (integer-number) of a fact as frame.

While embedding Rete for Frames into your code you can use other functions. See [source code] (https://github.com/rururu/rete4frames/blob/master/src/rete/core.clj).

Copyright © 2016 Ruslan Sorokin.
