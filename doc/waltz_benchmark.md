
Waltz Benchmark Table
----

<table>
<tr><td>Test</td><td>CLIPS (msec)</td><td>rete4frames (msec)</td><td>factor</td></tr>
<tr><td>waltz12</td><td>2270</td><td>93376</td><td>x 41</td></tr>
<tr><td>waltz25</td><td>10135</td><td>328176</td><td>x 32</td></tr>
<tr><td>waltz37</td><td>23741</td><td>720524</td><td>x 30</td></tr>
<tr><td>waltz50</td><td>45470</td><td>1282871</td><td>x 28</td></tr>
</table>

Test results obtained on the same hardware OS (Linux Mint, Petra) 13 june 2014.

To get the results run in REPL:

```
(require 'rete.core)
(in-ns 'rete.core)
(app "run:asynch" "examples/waltz.clj" "examples/waltz_f12.clj")
```
For further information see [Documentation] (https://github.com/rururu/rete4frames/blob/master/doc/intro.md)

