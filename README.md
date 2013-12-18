Rete for Frames
====

Clojure RETE implementation for frames

Manners Benchmark Table
----

<table>
<tr><td>Test</td><td>CLIPS (msec)</td><td>rete (msec)</td><td>factor</td></tr>
<tr><td>manners8</td><td>1.4</td><td>54</td><td>x 39</td></tr>
<tr><td>manners16</td><td>18</td><td>194</td><td>x 11</td></tr>
<tr><td>manners32</td><td>264</td><td>1133</td><td>x 4</td></tr>
<tr><td>manners64</td><td>9030</td><td>7974</td><td>x 0.9</td></tr>
<tr><td>manners128</td><td>320036</td><td>84685</td><td>x 0.26</td></tr>
</table>

Test results obtained on the same hardware 18 december 2013.
As can be seen from the results, rete initially far behind and then starts to catch up CLIPS.
On the most difficult test rete almost four times superior to CLIPS.

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
