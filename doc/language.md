# The Language

The language of this rete implementation is a subset of Clojure language. Also it is similar to CLIPS language.
It lacks many of CLIPS features, to mention few - multislots, slot value types, COOL. Syntax is also simplified.
The rule engine of rete4frames includes only two conflict resolution strategies - depth (default) and breadth. Depth strategy means that among the rules with the highest priority fired one, which has been activated by most recent facts. Breadth strategy means that among the rules with the highest priority fired one, which has been activated by most old facts.

Application description on this language is a list consisting of four mandatory lists: templates, rules, functions and facts:
```clj
((templates t1 t2 ... tn )
 (rules r1 r2 ... rm )
 (functions d1 d2 ... dl )
 (facts f1 f2 ... fk ))
```
Facts can be loaded into the application from a separate file.

Frames, Facts and Patterns
----
The frame is a basic concept for the representation of knowledge in the language. It is used to represent the facts and patterns of facts.
The frame has a type and slots. The slot has a name and a value. The frame represented as a list.
First element of the list is the type of the frame. Other elements of the list are slot names and values alternately:
```clj
(type slot1 value1 slot2 value2 ... )
```
Values of slots can be constants and variables. Variables are represented as symbols with the question mark prefix. Constants can be of any Clojure data type.
If a frame contains variables it is a pattern, otherwise it is a fact.
Example of the fact:
```clj
(monkey location t5-7 on-top-of green-couch holding blank)
```
Example of the pattern:
```clj
(monkey location ?place on-top-of ?on2 holding blank)
```

Templates
----
The template is a description of a frame type. It is a list of the group name (type) and names of the slots.
Example:
```clj
(monkey
  location
  on-top-of
  holding)
```

Rules
----
The rule is a description of a transformation. It is represented by a list of the form:
```clj
(<name>
  <salience>
  <condition1>
  <condition2>
   ...
  <conditionN>
  =>
  <function_body>)
```
The name is a symbol or a string.
The salience(priority) is an integer number, positive or negative.
Conditions are bare patterns, patterns with tests or negative conditions.
The function body is an ordinary clojure function body which may contain, apart from everything else, the function call "asser", "retratct" and "modify".
The rule "fires" when all conditions are fulfilled. In this case the function body accomplished with values for variables obtained during pattern-matching of conditional part.
First condition in the rule cannot be negative.
Example of the rule:
```clj
(walk-holding-object 0
  ?goal (goal-is-to action walk-to argument1 ?place)
  ?monkey (monkey location ?loc on-top-of floor holding ?obj
                  ((not= ?loc ?place)
                   (not= ?obj blank)))
  =>
  (println (str "Monkey walks to " ?place " holding the " ?obj "." ))
  (modify ?monkey location ?place)
  (retract ?goal))
```
The part of the rule before the symbol "=>" is named "left hand side of the rule".
The part of the rule after the symbol "=>" is named "right hand side of the rule".

Conditions
----
The condition is a bare pattern or a pattern with a test.
The negative condition is a condition preceding with the symbol "not".
Negative conditions must be after all positive conditions.
If some condition contains a test the test is a last element of the list.
The test is a predicate call or a list of tests or a vector of tests.
The predicate is any Clojure function that returns true or false values, and also nil and not nil values that can be considered as true and false.
The list of tests is interpreted as conjunction of tests. The vector of tests is interpreted as disjunction of tests.
The condition is fulfilled if the pattern is match some fact (not match for the negative condition) and the test returns true or not nil value.
If the test is absent, the pattern matching (not matching) is sufficient for the condition fulfilment.
The positive (not negative) condition can be preceded with a variable which is used in a right hand side of rule in calls to functions "modify" and "retract".
A value of the such variable is a fact that have been matched with the pattern.
The negative condition is fulfilled if the pattern does not match any fact in the working memory and following test is true or not nil if supplied.
Example of the condition:
```clj
  (avh a color v blue h ?c5
       ((not= ?c5 ?c4)
        (not= ?c5 ?c3)
        (not= ?c5 ?c2)
        (not= ?c5 ?c1)
        [(= ?c5 (- ?n4 1)) (= ?c5 (+ ?n4 1))]))
```
Example of the negative conditions:
```clj
  (not thing name ladder location ?place)
  (not goal-is-to action move argument1 ladder argument2 ?place)
```
```clj
  (not edge p1 ?base-point p2 ?p4
        ((not= ?p4 ?p2) (not= ?p4 ?p3)))
```

Function Body
----
The function body is an ordinary clojure function body which may contain, apart from everything else, the function call "asser", "retratct" and "modify", "modify", "fact-id" and "problem-solved". It will be added with parameters list and compiled into a function object during creation of beta network. When the rule "fires" this function evaluated with values for variables obtained during pattern-matching of conditional part.
The function "asser" has arguments representing a pattern of new fact that will be put into the working memory during its execution.

The function "retract" has arguments representing  fact variables from the left hand side of the rule.
Facts, associated with these variables, will be removed from the working memory during its execution.
The function "modify" has a first argument representing a fact variable from the left hand side of the rule. Rest arguments are slots and their new values.
Fact, associated with this variable, will be updated in the working memory with new values of slots during its execution.
The function "fact-id" has one argument that must be condition variable. This function returns a number of a fact that have been matched with corresponding condition during the rule activation. The function "fact-id" has one argument that must be a condition variable. This function returns an identifier (integer number) of a fact, which has been matched with corresponding condition during the rule activation. Then it can be used to get the fact itself using API function "frame-by-id".
The function "problem-solved" has no arguments and can be used to clear all remaining activations from the conflict set, when it become clear that, they no needed anymore.

Remark: Because Clojure functions can not have more than 20 parametrs, you can split rules with more then 20 variables in the right hand side on several rules with the same left hand side. See [zebra.clj] (https://github.com/rururu/rete4frames/blob/master/examples/zebra.clj) example.

Functions
----
The functions section contains ordinary clojure function definitions optionally prepended with namespace definition (ns <namespace>)
See examples of function definitions in example [waltz.clj] (https://github.com/rururu/rete4frames/blob/master/examples/waltz.clj).

Comments
----
Files can contain comments prepended with semicolon

Application program interface described in [API] (https://github.com/rururu/rete4frames/blob/master/doc/api.md).

Copyright © 2014 Ruslan Sorokin.

