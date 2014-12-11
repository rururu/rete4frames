
Waltz Benchmark Table
----

<table>
<tr><td>Test</td><td>CLIPS v 6.24 (msec)</td><td>rete4frames v 5.2.1 (msec)</td><td>factor</td></tr>
<tr><td>waltz12</td><td>2936</td><td>26503</td><td>x 9</td></tr>
<tr><td>waltz25</td><td>11776</td><td>77234</td><td>x 7</td></tr>
<tr><td>waltz37</td><td>24009</td><td>145889</td><td>x 6</td></tr>
<tr><td>waltz50</td><td>35674</td><td>241508</td><td>x 7</td></tr>
</table>

Test results obtained on the same hardware OS (Linux Mint 11, Quiana) 11 december 2014.

To get the results run in REPL:

```
(require 'rete.core)
(in-ns 'rete.core)
(app "run" "examples/waltz.clj" "examples/waltz_f12.clj")
```
For further information see [Documentation] (https://github.com/rururu/rete4frames/blob/master/doc/intro.md)

