## Application Program Interface (API) ##

### 1.Function -main ###
```
(-main <modes> <application-file>)
(-main <modes> <application-file> <facts-file>)
```
Run the application. 
- application-file (string) - a path to a file containing a list of templates, rules, functions and facts.
- facts-file (string) - a path to a file containing a list of facts. If used, this list of facts replaces the list of facts from the <application-file>.
- modes (string):
- "run:asynch" - run the application asynchronously, that is assert all facts into the working memory before firing any rules. 
- "run:synch"  - run the application synchronously, that is assert first fact and fire rules then assert second fact and fire rules and so on.
- "trace:asynch" - same with a tracing of creation of the rete network and tracing of firing rules and assertion and retraction of facts asynchronously.
- "trace:synch"  - same synchronously.
- "step:asynch"  - same as "trace:asynch" with a stop after firing one rule. Further execution can be continued by calling functions (fire <n>) or (fire) where <n> - a number of steps (firings of one rule). If <n> is ommited then fire till the very end.
	
In the modes with the tracing in a root directory are created three files:
	
- alpha-net-plan.txt	 - describes alpha part of the rete network.
- beta-net-plan.txt	 - describes beta part of the rete network.
- alpha-beta-links.txt - describes the links between alpha part and beta part of the rete network.
	
### 2.Function facts ###
```
(facts)
```
Pretty prints a list of facts in a working memory.

### 3.Function fact-list ###
```
(fact-list)
```
Returns a list of facts in a working memory.

### 4. Function run-with ###
```
(run-with <modes> <templates> <rules> <facts>)
```
Create the rete network and run using given modes.
- templates - a list of templates.
- rules - a list of rules.
- facts - a list of facts.

### 5. Functions trace/untrace ###
```
(trace)
(untrace)
```
Switches on/off tracing.

### 6. Functions run-synch/run-asynch ###
```
(run-synch <templates> <rules> <facts>)
(run-asynch <templates> <rules> <facts>)
```
Create the rete network and run synchronously/asynchronously.
Arguments are the same as for run-with function.

### 7. Function create-rete ###
```
(create-rete <templates> <rules>)
```
Create the rete network and clear the working memory.
Arguments are the same as for run-with function.

### 8. Function assert-frame ###
```
(assert-frame <fact>)
```
Assert a fact into the working memory.

### 9. Function retract-fact ###
```
(retract-fact <fact-id>
```
Retract a fact from the working memory.
- fact-id (integer) - index of the fact in the working memory.

### 10. Function modify-fact ###
```
(modify-fact <fact-id> <slot-value-map>)
```
Modify a fact in the working memory.
- fact-id (integer) - index of the fact in the working memory.
- <slot-value-map> - map of slots and their values.
During modification the old fact is deleted and a new fact created with the changed values ​​of slots according to map.

### 11. Function fire ###
```
(fire <number>)
(fire)
```
Fire a number of active rules. If the number is ommited fire till the very end.

### 12. Function reset ###
```
(reset)
```
Clear and initialize the working memory.

While embedding Rete for Frames into your code you can use other functions. See [source code] (https://github.com/rururu/rete4frames/blob/master/src/rete/core.clj).

Copyright © 2013 Ruslan Sorokin.
