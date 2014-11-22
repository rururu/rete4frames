Rete for Frames
====

Clojure RETE implementation for frames

Manners Benchmark Table
----

<table>
<tr><td>Test</td><td>CLIPS v 6.24 (msec)</td><td>rete4frames v 5.2.1 (msec)</td></tr>
<tr><td>manners8</td><td>1.1</td><td>67</td><td>x 81</td></tr>
<tr><td>manners16</td><td>12</td><td>354</td><td>x 16</td></tr>
<tr><td>manners32</td><td>266</td><td>1073</td><td>x 3</td></tr>
<tr><td>manners64</td><td>8859</td><td>6326</td><td>x 0.66</td></tr>
<tr><td>manners128</td><td>311823</td><td>58083</td><td>x 0.17</td></tr>
</table>

Test results obtained on the same hardware and OS (Linux Mint 17, Qiana) 22 november 2014.
As can be seen from the results, rete4frames initially far behind and then starts to catch up CLIPS v 6.24.
On the most difficult test rete4frames almost 6 times superior to CLIPS v 6.24.

To get the results run in REPL:

```
(require 'rete.core)
(in-ns 'rete.core)
(app "run" "examples/manners.clj" "examples/manners_f8.clj")
```
The benchmark table for Waltz algorithm is in [Waltz Benchmark Table] (https://github.com/rururu/rete4frames/blob/master/doc/waltz_benchmark.md)

To include Rete for Frames into your Leiningen project:

:dependencies [rete "5.2.0-SNAPSHOT"]

Running examples:
```
(require 'rete.core)
(in-ns 'rete.core)
(app "run" "examples/mab.clj") ;; Monkey and bananas..
(app "run" "examples/zebra.clj") ;; Who drinks water, who owns zebra..
(app "run" "examples/auto.clj") ;; Automotive Expert System..
```
How to include Rete for Frames into your Java programs see example [Eclipse project] (https://github.com/rururu/rete4frames/blob/master/Rete4framesEclipseTest/).

For further information see [Documentation] (https://github.com/rururu/rete4frames/blob/master/doc/intro.md)

Copyright and license
----

Copyright © 2014 Ruslan Sorokin.

Licensed under the EPL (see the file epl.html).
