## Application Program Interface (API) ##

### 1.Function app ###
```
(app <mode> <application-file>)
(app <mode> <application-file> <facts-file>)
```
Run the application.
- application-file (string) - a path to a file containing a list of templates, rules, functions and facts.
- facts-file (string) - a path to a file containing a list of facts. If used, this list of facts replaces the list of facts from the <application-file>.
- mode (string):
- "run"  - run the application synchronously, that is assert first fact and fire rules then assert second fact and fire rules and so on.
- "trace" - same with a tracing of creation of the rete network and tracing of firing rules and assertion and retraction of facts.
- "step"  - same as "trace" with a stop after firing one rule. Further execution can be continued by calling functions (fire N) or (fire) where N - a number of steps (firings of one rule). If N is ommited then fire till the very end.

In the modes with the tracing in a root directory are created three files:

- alpha-net-plan.txt	 - describes alpha part of the rete network.
- beta-net-plan.txt	 - describes beta part of the rete network.
- alpha-beta-links.txt - describes the links between alpha part and beta part of the rete network.

### 2.Function fact-list ###
```
(fact-list)
(fact-list '<type>)
```
Returns a list of facts (of type if supplied) in a working memory.

### 3.Function facts ###
```
(facts)
(facts '<type>)
```
Prints a list of facts (of type if supplied) in a working memory.

### 4.Function ppr ###
```
(ppr <type>)
```
Pretty prints facts of a specific type or all facts (<type> = :all) in a working memory.

### 5. Function run-with ###
```
(run-with <mode> <templates> <rules> <facts>)
```
Create the rete network and run using given mode.
- templates - a list of templates.
- rules - a list of rules.
- facts - a list of facts.

### 6. Functions trace/untrace ###
```
(trace)
(untrace)
```
Switches on/off tracing.

### 7. Functions step ###
```
(step)
(step N)
```
Fires rules 1 time (N times if supplied).

### 8. Functions run-synch/run-asynch ###
```
(run-with-mode <mode> <truff>)
(run-with-mode <mode> <trufs> <facts>)
```
Create the rete network and run run using given mode. Arguments are:
<mode> - "run" or "trace" or "step",
<truff> - a list of a form: ((templates ..) (rules ..) (functions ..) (facts ...)),
<trufs> - a list of a form: ((templates ..) (rules ..) (functions ..)),
<facts> - a list of facts.

### 9. Function create-rete ###
```
(create-rete <templates> <rules>)
```
Create the rete network and clear the working memory.
Arguments are the same as for run-with function.

### 10. Function assert-frame ###
```
(assert-frame <fact>)
```
Assert a fact into the working memory.

### 11. Function retract-fact ###
```
(retract-fact <fact-id>
```
Retract a fact from the working memory.
- fact-id (integer) - index of the fact in the working memory.

### 12. Function modify-fact ###
```
(modify-fact <fact-id> <slot-value-map>)
```
Modify a fact in the working memory.
- fact-id (integer) - index of the fact in the working memory.
- slot-value-map - map of slots and their values.
During modification the old fact is deleted and a new fact created with the changed values ​​of slots according to map.

### 13. Function fire ###
```
(fire <number>)
(fire)
```
Fire a number of active rules. If the number is ommited fire till the very end.

### 14. Function reset ###
```
(reset)
```
Clear and initialize the working memory.

### 15. Function reset ###
```
(strategy-depth)
```
Set conflict resolution strategy to depth.

### 16. Function reset ###
```
(strategy-breadth)
```
Set conflict resolution strategy to breadth.

While embedding Rete for Frames into your code you can use other functions. See [source code] (https://github.com/rururu/rete4frames/blob/master/src/rete/core.clj).

Copyright © 2013 Ruslan Sorokin.
