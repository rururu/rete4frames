Rete for Frames
====

Clojure RETE implementation for frames

Manners Benchmark Table
----

<table>
<tr><td>Test</td><td>CLIPS (msec)</td><td>rete4frames (msec)</td><td>factor</td></tr>
<tr><td>manners8</td><td>1.4</td><td>80</td><td>x 57</td></tr>
<tr><td>manners16</td><td>19</td><td>297</td><td>x 16</td></tr>
<tr><td>manners32</td><td>254</td><td>1289</td><td>x 5</td></tr>
<tr><td>manners64</td><td>8531</td><td>8044</td><td>x 0.9</td></tr>
<tr><td>manners128</td><td>314148</td><td>87155</td><td>x 0.28</td></tr>
</table>

Test results obtained on the same hardware and OS (Linus Mint, Petra) 12 may 2014.
As can be seen from the results, rete4frames initially far behind and then starts to catch up CLIPS.
On the most difficult test rete4frames more then three times superior to CLIPS.

To get the results run in REPL:

```
(require 'rete.core)
(in-ns 'rete.core)
(-main "run:asynch" "examples/manners.clj" "examples/manners_f8.clj")
```
The benchmark table for Waltz algorithm is in [Waltz Benchmark Table] (https://github.com/rururu/rete4frames/blob/master/doc/waltz_benchmark.md)

To include Rete for Frames into your Leiningen project:

```
:dependencies [[rete "4.3.0-SNAPSHOT"]]
```
How to include Rete for Frames into your Java programs see example [Eclipse project] (https://github.com/rururu/rete4frames/blob/master/Rete4framesEclipseTest/).

For further information see [Documentation] (https://github.com/rururu/rete4frames/blob/master/doc/intro.md)

Copyright and license
----

Copyright © 2014 Ruslan Sorokin.

Licensed under the EPL (see the file epl.html).
