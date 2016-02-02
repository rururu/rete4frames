
Waltz Benchmark Table
----

<table>
<tr><td>Test</td><td>CLIPS v 6.24 (msec)</td><td>rete4frames v 5.2.3 (msec)</td><td>factor</td></tr>
<tr><td>waltz12</td><td>1943</td><td>30336</td><td>x 16</td></tr>
<tr><td>waltz25</td><td>9439</td><td>92498</td><td>x 9.8</td></tr>
<tr><td>waltz37</td><td>22881</td><td>182856</td><td>x 8.0</td></tr>
<tr><td>waltz50</td><td>44483</td><td>308702</td><td>x 7.0</td></tr>
</table>

Test results obtained on the same hardware and OS (Linux Mint 17, Quiana) 2 february 2016.

To get the results run in REPL:

```clj
(require 'rete.core)
(in-ns 'rete.core)
(app "run" "examples/waltz.clj" "examples/waltz_f12.clj")
```
For further information see [Documentation] (https://github.com/rururu/rete4frames/blob/master/doc/intro.md)

