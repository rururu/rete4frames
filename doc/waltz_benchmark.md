
Waltz Benchmark Table
----

<table>
<tr><td>Test</td><td>CLIPS v 6.24 (msec)</td><td>rete4frames v 5.2.2 (msec)</td><td>factor</td></tr>
<tr><td>waltz12</td><td>1919</td><td>30780</td><td>x 16</td></tr>
<tr><td>waltz25</td><td>9397</td><td>96754</td><td>x 10</td></tr>
<tr><td>waltz37</td><td>22106</td><td>189585</td><td>x 8.6</td></tr>
<tr><td>waltz50</td><td>42751</td><td>327216</td><td>x 7.7</td></tr>
</table>

Test results obtained on the same hardware OS (Linux Mint 11, Quiana) 19 february 2015.

To get the results run in REPL:

```clj
(require 'rete.core)
(in-ns 'rete.core)
(app "run" "examples/waltz.clj" "examples/waltz_f12.clj")
```
For further information see [Documentation] (https://github.com/rururu/rete4frames/blob/master/doc/intro.md)

