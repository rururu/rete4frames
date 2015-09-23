
Waltz Benchmark Table
----

<table>
<tr><td>Test</td><td>CLIPS v 6.24 (msec)</td><td>rete4frames v 5.2.3 (msec)</td><td>factor</td></tr>
<tr><td>waltz12</td><td>1943</td><td>31351</td><td>x 16</td></tr>
<tr><td>waltz25</td><td>9439</td><td>98946</td><td>x 10</td></tr>
<tr><td>waltz37</td><td>22881</td><td>195119</td><td>x 8.5</td></tr>
<tr><td>waltz50</td><td>44483</td><td>338014</td><td>x 7.7</td></tr>
</table>

Test results obtained on the same hardware and OS (Linux Mint 17, Quiana) 23 september 2015.

To get the results run in REPL:

```clj
(require 'rete.core)
(in-ns 'rete.core)
(app "run" "examples/waltz.clj" "examples/waltz_f12.clj")
```
For further information see [Documentation] (https://github.com/rururu/rete4frames/blob/master/doc/intro.md)

