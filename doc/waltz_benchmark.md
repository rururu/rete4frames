
Waltz Benchmark Table
----

<table>
<tr><td>Test</td><td>CLIPS v 6.24 (msec)</td><td>rete4frames v 5.0.0 (msec)</td><td>rete4frames v 5.1.0 (msec)</td><td>factor</td></tr>
<tr><td>waltz12</td><td>2270</td><td>93376</td><td>56800</td><td>x 25</td></tr>
<tr><td>waltz25</td><td>10135</td><td>328176</td><td>199493</td><td>x 19</td></tr>
<tr><td>waltz37</td><td>23741</td><td>720524</td><td>404355</td><td>x 17</td></tr>
<tr><td>waltz50</td><td>45470</td><td>1282871</td><td>709458</td><td>x 15</td></tr>
</table>

Test results obtained on the same hardware OS (Linux Mint 16, Petra) 19 august 2014.

To get the results run in REPL:

```
(require 'rete.core)
(in-ns 'rete.core)
(app "run:asynch" "examples/waltz.clj" "examples/waltz_f12.clj")
```
For further information see [Documentation] (https://github.com/rururu/rete4frames/blob/master/doc/intro.md)

