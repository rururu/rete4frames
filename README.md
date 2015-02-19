Rete for Frames
====

Clojure RETE implementation for frames

Manners Benchmark Table
----

<table>
<tr><td>Test</td><td>CLIPS v 6.24 (msec)</td><td>rete4frames v 5.2.2 (msec)</td></tr>
<tr><td>manners8</td><td>1.5</td><td>128</td><td>x 85</td></tr>
<tr><td>manners16</td><td>19</td><td>419</td><td>x 22</td></tr>
<tr><td>manners32</td><td>258</td><td>1170</td><td>x 4.5</td></tr>
<tr><td>manners64</td><td>8601</td><td>6457</td><td>x 0.75</td></tr>
<tr><td>manners128</td><td>316109</td><td>55961</td><td>x 0.18</td></tr>
</table>

Test results obtained on the same hardware and OS (Linux Mint 17, Qiana) 19 february 2015.
As can be seen from the results, rete4frames initially far behind and then starts to catch up CLIPS v 6.24.
On the most difficult test rete4frames more then 5 times superior to CLIPS v 6.24.

To get the results run in REPL:

```clj
(require 'rete.core)
(in-ns 'rete.core)
(app "run" "examples/manners.clj" "examples/manners_f8.clj")
```
The benchmark table for Waltz algorithm is in [Waltz Benchmark Table] (https://github.com/rururu/rete4frames/blob/master/doc/waltz_benchmark.md)

To include Rete for Frames into your Leiningen project:

:dependencies [rete "5.2.2-SNAPSHOT"]

Running examples:
```clj
(require 'rete.core)
(in-ns 'rete.core)
(app "run" "examples/mab.clj") ;; Monkey and bananas..
(app "run" "examples/zebra.clj") ;; Who drinks water, who owns zebra..
(app "run" "examples/auto.clj") ;; Automotive Expert System..
(app "run" "examples/sudoku/sudoku.clj" "examples/sudoku/grid3x3-p1.clj") ;; Game "Sudoku"
(app "run" "examples/hypertension.clj") ;; Doctor's expert system
Today: 19.2.2015..
What will do? [reception analyse end]
reception
What is a name of a next patient? (no-patients)
"Alice"
Patient: Alice, age: 70, race: nonblack
Last blood pressure values: systolic: 140, diastolic: 90, date: 18.2.2015
Well, Alice, let's measure your current blood pressure..
What are blood pressure values now? [systolic diastolic]
[140 90]
Good, Alice.
Continue prescribed medication.
                     Waiting for you after 18.3.2015, if all is well.
                     Good bye.
What is a name of a next patient? (no-patients)
...
```
How to include Rete for Frames into your Java programs see example [Eclipse project] (https://github.com/rururu/rete4frames/blob/master/Rete4framesEclipseTest/).

For further information see [Documentation] (https://github.com/rururu/rete4frames/blob/master/doc/intro.md)

Copyright and license
----

Copyright © 2014 Ruslan Sorokin.

Licensed under the EPL (see the file epl.html).
