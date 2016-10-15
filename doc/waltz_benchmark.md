
Waltz Benchmark Table
----

<table>
<tr><td>Test</td><td>CLIPS v 6.24 (msec)</td><td>rete4frames v 5.3.0 (msec)</td><td>factor</td></tr>
<tr><td>waltz12</td><td>1931</td><td>50458</td><td>x 26</td></tr>
<tr><td>waltz25</td><td>9557</td><td>148496</td><td>x 16</td></tr>
<tr><td>waltz37</td><td>22707</td><td>283529</td><td>x 12</td></tr>
<tr><td>waltz50</td><td>45037</td><td>476858</td><td>x 11</td></tr>
</table>

Test results obtained on the same hardware and OS 7 october 2016.

To get the results run in REPL:

```clj
(require 'rete.core)
(in-ns 'rete.core)
(app "run" "examples/waltz.clj" "examples/waltz_f12.clj")
```
For further information see [Documentation] (https://github.com/rururu/rete4frames/blob/master/doc/intro.md)

