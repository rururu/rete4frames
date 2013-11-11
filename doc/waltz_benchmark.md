
Waltz Benchmark Table
----

<table>
<tr><td>Test</td><td>CLIPS (msec)</td><td>rete (msec)</td><td>factor</td></tr>
<tr><td>waltz12</td><td>2025</td><td>181174</td><td>x 90</td></tr>
<tr><td>waltz25</td><td>10400</td><td>686950</td><td>x 66</td></tr>
<tr><td>waltz37</td><td>28035</td><td>1479687</td><td>x 53</td></tr>
<tr><td>waltz50</td><td>66215</td><td>2677225</td><td>x 40</td></tr>
</table>

Test results obtained on the same hardware.
As can be seen from the results, rete initially far behind and then starts to catch up CLIPS.
On the most difficult test rete three times superior to CLIPS.

To get the results run in REPL:

```
(require 'rete.core)
(in-ns 'rete.core)
(-main "run:asynch" "examples/waltz.clj" "examples/waltz_f12.clj")
```
For further information see [Documentation] (https://github.com/rururu/rete/blob/master/doc/intro.md)

Copyright and license
----

Copyright © 2013 Ruslan Sorokin.

Licensed under the EPL (see the file epl.html).
