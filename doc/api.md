# Application Program Interface (API) #

## 1.Function -main ##
```
(-main <modes> <application-file>)
(-main <modes> <application-file> <facts-file>)
```
Run the application. 
<application-file> - string - a path to a file containing a list of templates, rules, functions and facts.
<facts-file> - string - a path to a file containing a list of facts. If used, this list of facts replaces the list of facts from the <application-file>.
<modes> -string:
	"run:asynch" - run the application asynchronously, that is assert all facts into the working memory before firing any rules. 
	"run:synch"  - run the application synchronously, that is assert first fact and fire rules then assert second fact and fire rules and so on.
	"trace:asynch" - same with a tracing of creation of the rete network and tracing of firing rules and assertion and retraction of facts asynchronously.
	"trace:synch"  - same synchronously.
	"step:asynch"  - same as "trace:asynch" with a stop after firing one rule. Further execution can be continued by calling functions (fire <n>) or (fire) where <n> - a number of steps (firings of one rule). If <n> is ommited then fire till the very end.
	
In the modes with the tracing in a root directory are created three files:
	
	alpha-net-plan.txt	 - describes alpha part of the rete network.
	beta-net-plan.txt	 - describes beta part of the rete network.
	alpha-beta-links.txt - describes the links between alpha part and beta part of the rete network.
	
## 2.Function facts ##
```
(fact)
```
Pretty prints a list of facts in a working memory.

Copyright © 2013 Ruslan Sorokin.
