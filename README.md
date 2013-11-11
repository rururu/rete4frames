Rete for Frames
====

Clojure RETE implementation for frames

Manners Benchmark Table
----

<table>
<tr><td>Test</td><td>CLIPS (msec)</td><td>rete (msec)</td><td>factor</td></tr>
<tr><td>manners8</td><td>1.4</td><td>65</td><td>x 46</td></tr>
<tr><td>manners16</td><td>18</td><td>244</td><td>x 14</td></tr>
<tr><td>manners32</td><td>264</td><td>1412</td><td>x 5</td></tr>
<tr><td>manners64</td><td>9030</td><td>9588</td><td>x 1</td></tr>
<tr><td>manners128</td><td>320036</td><td>98637</td><td>x 0.3</td></tr>
</table>

Test results obtained on the same hardware.
As can be seen from the results, rete initially far behind and then starts to catch up CLIPS.
On the most difficult test rete three times superior to CLIPS.

To get the results run in REPL:

```
(require 'rete.core)
(in-ns 'rete.core)
(-main "run:asynch" "examples/manners.clj" "examples/manners_f8.clj")
```
The benchmark table for Waltz algorithm is in [Waltz Benchmark Table] (https://github.com/rururu/rete4frames/blob/master/doc/waltz_benchmark.md)

For further information see [Documentation] (https://github.com/rururu/rete4frames/blob/master/doc/intro.md)

Copyright and license
----

Copyright © 2013 Ruslan Sorokin.

Licensed under the EPL (see the file epl.html).
