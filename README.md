Rete for Frames
====

Clojure RETE implementation for frames

Manners Benchmark Table
----

<table>
<tr><td>Test</td><td>CLIPS v 6.24 (msec)</td><td>rete4frames (msec)</td><td>factor</td></tr>
<tr><td>manners8</td><td>1.4</td><td>85</td><td>x 61</td></tr>
<tr><td>manners16</td><td>18</td><td>259</td><td>x 14</td></tr>
<tr><td>manners32</td><td>266</td><td>1393</td><td>x 5</td></tr>
<tr><td>manners64</td><td>8528</td><td>11251</td><td>x 1.3</td></tr>
<tr><td>manners128</td><td>311231</td><td>135651</td><td>x 0.44</td></tr>
</table>

Test results obtained on the same hardware and OS (Linux Mint, Petra) 13 june 2014.
As can be seen from the results, rete4frames initially far behind and then starts to catch up CLIPS v 6.24.
On the most difficult test rete4frames more then two times superior to CLIPS v 6.24.
(I must say CLIPS 6.30 is two orders faster than 6.24)

To get the results run in REPL:

```
(require 'rete.core)
(in-ns 'rete.core)
(app "run:asynch" "examples/manners.clj" "examples/manners_f8.clj")
```
The benchmark table for Waltz algorithm is in [Waltz Benchmark Table] (https://github.com/rururu/rete4frames/blob/master/doc/waltz_benchmark.md)

To include Rete for Frames into your Leiningen project:

```
:dependencies [[rete "4.3.1-SNAPSHOT"]]
```
Running examples:
```
(require 'rete.core)
(in-ns 'rete.core)
(app "run:asynch" "examples/mab.clj") ;; Monkey and bananas..
(app "run:synch" "examples/zebra.clj") ;; Who drinks water, who owns zebra..
```
How to include Rete for Frames into your Java programs see example [Eclipse project] (https://github.com/rururu/rete4frames/blob/master/Rete4framesEclipseTest/).

For further information see [Documentation] (https://github.com/rururu/rete4frames/blob/master/doc/intro.md)

Copyright and license
----

Copyright © 2014 Ruslan Sorokin.

Licensed under the EPL (see the file epl.html).
