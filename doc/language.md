# The Language

The language of this rete implementation is a subset of Clojure language. Also it is similar to CLIPS language.
It lacks many of CLIPS features, to mention few - multislots, slot value types, strategies, COOL. Syntax also is simplified.
Application description on this language is a list consisting of four mandatory lists: templates, rules, functions and facts:
```
((templates t1 t2 ... tn )
 (rules r1 r2 ... rm )
 (functions d1 d2 ... dl )
 (facts f1 f2 ... fk ))
```
Facts can be loaded into the application from a separate file.

Frames, Facts and Patterns
----
The frame is a basic concept for the representation of knowledge in the language. It is used to represent the facts and patterns of facts.
The frame has a type and slots. The slot has  a name and a value. The frame represented as a list.
First element of the list is the type of the frame. Other elements of the list are slot names and values alternately:
```
(type slot1 value1 slot2 value2 ... )
```
Values of slots can be constants and variables. Variables are represented as symbols with the question mark prefix. Constants can be of any Clojure data type.
If a frame contains variables it is a pattern, otherwise it is a fact.
Example of the fact:
```
(monkey location t5-7 on-top-of green-couch holding blank)
```
Example of the pattern:
```
(monkey location ?place on-top-of ?on2 holding blank)
```

Templates
----
The template is a description of a frame type. It is a list of the group name (type) and names of the slots.
Example:
```
(monkey
  location
  on-top-of
  holding)
```

Rules
----
The rule is a description of a transformation. It is represented by a list of the form:
```
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
Conditions are bare patterns or patterns with tests.
The function body is an ordinary clojure function body which may contain, apart from everything else, the function call "asser", "retratct" and "modify".
The rule "fires" when all conditions are fulfilled. In this case the function body accomplished with values for variables obtained during pattern-matching of conditional part.
Example of the rule:
```
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
The condition is a bare patterns or a pattern with test.
If some condition contains a test the test is a last element of the list.
The test is a predicate call or a list of tests or a vector of tests.
The predicate is any Clojure function that returns true or false values, and also nil and not nil values that can be considered as true and false.
The list of tests is interpreted as conjunction of tests. The vector of tests is interpreted as disjunction of tests.
The condition is fulfilled if the pattern is match some fact and the test returns true or not nil value.
If the test is absent, the pattern matching is sufficient for the condition fulfilment.
The condition can be preceded with a variable which is used in a right hand side of rule in calls to functions "modify" and "retract".
A value of the such variable is a fact that have been matched with the pattern.
Example of the condition:
```
  (avh a color v blue h ?c5
       ((not= ?c5 ?c4)
        (not= ?c5 ?c3)
        (not= ?c5 ?c2)
        (not= ?c5 ?c1)
        [(= ?c5 (- ?n4 1)) (= ?c5 (+ ?n4 1))]))
```

Function Body
----
The function body is an ordinary clojure function body which may contain, apart from everything else, the function call "asser", "retratct" and "modify". It will be added with parameters list and compiled into a function object during creation of beta network. When the rule "fires" this function evaluated with values for variables obtained during pattern-matching of conditional part.
The function "asser" has arguments representing a pattern of new fact that will be put into the working memory during its execution.
The function "retract" has arguments representing  fact variables from the left hand side of the rule.
Facts, associated with these variables, will be removed from the working memory during its execution.
The function "modify" has a first argument representing  a fact variable from the left hand side of the rule. Rest arguments are slots and their new values.
Fact, associated with this variable, will be updated in the working memory with new values of slots during its execution.

Remark: Because Clojure functions can not have more than 20 parametrs, you can split rules with more then 20 variables in the right hand side on several rules with the same left hand side. See [zebra.clj] (https://github.com/rururu/rete4frames/blob/master/examples/zebra.clj) example.

Functions
----
The functions section contains ordinary clojure function definitions optionally prepended with namespace definition (ns <namespace>)
See examples of function definitions in example [waltz.clj] (https://github.com/rururu/rete4frames/blob/master/examples/waltz.clj).

Application program interface described in [API] (https://github.com/rururu/rete4frames/blob/master/doc/api.md).

Copyright © 2013 Ruslan Sorokin.

