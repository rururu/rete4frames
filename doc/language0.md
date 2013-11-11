# The Language

The language of this rete implementation is a subset of Clojure language.
For knowledge representation used triples and based on them "object descriptions" as "syntactic sugar".

Production (Rule) Set
----
The production (rule) set is a list of productions:

```
( <production1> <production2> ... <productionN> )
```
Fact set
----
The fact set is a list of facts:

```
( <fact1> <fact2> ... <factN> )
```
Production (Rule)
----
The production (rule) is a list of four parts:

```
( <name> <salience(priority)> <left hand side> <divider(=>)> <right hand side> )
```
The name is a symbol or a string.
The salience(priority) is an integer number, positive or negative.
The left hand side is a list of fact patterns or tests. First element of the left hand side can be only a pattern.
The right hand side is a list of function calls.

Fact and fact pattern
----

The fact is a triple or an object description.
The triple is a list of three parts:

```
( <subject> <predicate> <object> )
```

The subject and object can be symbols, strings, numbers or lists (vectors, maps)
The predicate is a symbol or a string.
The subject and/or object can be variables, that is symbols beginning from the question mark "?".
In this case, the triple is called a triple pattern.

Object description
----

The object description is a representation of the list of triples with the same subject:
```
(( <subject> <predicate1> <object1> ) ( <subject> <predicate2> <object2> ) ... ( <subject> <predicateN> <objectN> ))
```
as a list with this subject on a first place and a rest of the list consisting of concatenated rests of all triples:
```
( <subject> <predicate1> <object1> <predicate2> <object2> ... <predicateN> <objectN> )
```
In this case, the subject is treated as an object, predicates - as this object properties, and objects as property values:
```
( <object> <property1> <value1> <property2> <value2> ... <propertyN> <valueN> )
```

Test
----

The test is a binary predicate in the infix notation. The following is a table of binary predicates and implementing them Clojure functions:
<table>
<tr><td>Predicate</td><td>Implementing Clojure function</td>\</tr>
<tr><td>x = y</td><td>(= x y)</td>\</tr>
<tr><td>x != y</td><td>(not (= x y))</td>\</tr>
<tr><td>x > y</td><td>(> x y)</td>\</tr>
<tr><td>x &lt; y</td><td>(&lt; x y)</td>\</tr>
<tr><td>x >= y</td><td>(>= x y)</td>\</tr>
<tr><td>x &lt;= y</td><td>(&lt;= x y)</td>\</tr>
<tr><td>x and y</td><td>(and x y)</td>\</tr>
<tr><td>x or y</td><td>(or x y)</td>\</tr>
<tr><td>x not y</td><td>(not y)</td>\</tr>
</table>

The table can be changed by redefining function "eval-test". As arguments of predicates can be used any Clojure expressions. These expressions can contain variables, defined earlier inside a rule. Example:

```
(P5 0
    ((?p weight ?w
         height ?h)
      ((+ (/ ?w 2) 12.3) < (* 0.5 ?h)))
    =>
    ((println [:COOL! ?p ?w ?h])
     (println (str (+ (div ?w 2) 12.3) " < " (* 0.5 ?h)))))
```
Note: Function "div" used to denote Clojure division function "/".


