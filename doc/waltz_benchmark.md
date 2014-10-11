
Waltz Benchmark Table
----

<table>
<tr><td>Test</td><td>CLIPS v 6.24 (msec)</td><td>rete4frames v 5.2.0 (msec)</td><td>factor</td></tr>
<tr><td>waltz12</td><td>2270</td><td>50895</td><td>x 22</td></tr>
<tr><td>waltz25</td><td>10135</td><td>186618</td><td>x 18</td></tr>
<tr><td>waltz37</td><td>23741</td><td>393136</td><td>x 17</td></tr>
<tr><td>waltz50</td><td>45470</td><td>710364</td><td>x 16</td></tr>
</table>

Test results obtained on the same hardware OS (Linux Mint 11, Quiana) 10 october 2014.

To get the results run in REPL:

```
(require 'rete.core)
(in-ns 'rete.core)
(app "run" "examples/waltz.clj" "examples/waltz_f12.clj")
```
For further information see [Documentation] (https://github.com/rururu/rete4frames/blob/master/doc/intro.md)

