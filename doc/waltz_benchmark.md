
Waltz Benchmark Table
----

<table>
<tr><td>Test</td><td>CLIPS (msec)</td><td>rete4frames (msec)</td><td>factor</td></tr>
<tr><td>waltz12</td><td>2258</td><td>91195</td><td>x 40</td></tr>
<tr><td>waltz25</td><td>10270</td><td>346225</td><td>x 34</td></tr>
<tr><td>waltz37</td><td>23912</td><td>713376</td><td>x 30</td></tr>
<tr><td>waltz50</td><td>45312</td><td>1307099</td><td>x 29</td></tr>
</table>

Test results obtained on the same hardware OS (Linus Mint, Petra) 12 may 2014.

To get the results run in REPL:

```
(require 'rete.core)
(in-ns 'rete.core)
(-main "run:asynch" "examples/waltz.clj" "examples/waltz_f12.clj")
```
For further information see [Documentation] (https://github.com/rururu/rete4frames/blob/master/doc/intro.md)

