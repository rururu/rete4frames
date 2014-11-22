
Waltz Benchmark Table
----

<table>
<tr><td>Test</td><td>CLIPS v 6.24 (msec)</td><td>rete4frames v 5.2.0 (msec)</td><td>factor</td></tr>
<tr><td>waltz12</td><td>1926</td><td>35771</td><td>x 19</td></tr>
<tr><td>waltz25</td><td>9490</td><td>116097</td><td>x 12</td></tr>
<tr><td>waltz37</td><td>22077</td><td>234100</td><td>x 11</td></tr>
<tr><td>waltz50</td><td>443587</td><td>405225</td><td>x 9</td></tr>
</table>

Test results obtained on the same hardware OS (Linux Mint 11, Quiana) 22 november 2014.

To get the results run in REPL:

```
(require 'rete.core)
(in-ns 'rete.core)
(app "run" "examples/waltz.clj" "examples/waltz_f12.clj")
```
For further information see [Documentation] (https://github.com/rururu/rete4frames/blob/master/doc/intro.md)

