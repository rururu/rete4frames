
Waltz Benchmark Table
----

<table>
<tr><td>Test</td><td>CLIPS (msec)</td><td>rete (msec)</td><td>factor</td></tr>
<tr><td>waltz12</td><td>2025</td><td>80991</td><td>x 40</td></tr>
<tr><td>waltz25</td><td>10400</td><td>296088</td><td>x 28</td></tr>
<tr><td>waltz37</td><td>28035</td><td>628024</td><td>x 22</td></tr>
<tr><td>waltz50</td><td>66215</td><td>1167842</td><td>x 18</td></tr>
</table>

Test results obtained on the same hardware 18 december 2013.
As can be seen from the results, rete initially far behind and then starts to catch up CLIPS.

To get the results run in REPL:

```
(require 'rete.core)
(in-ns 'rete.core)
(-main "run:asynch" "examples/waltz.clj" "examples/waltz_f12.clj")
```
For further information see [Documentation] (https://github.com/rururu/rete4frames/blob/master/doc/intro.md)

