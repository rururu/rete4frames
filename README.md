Rete for Frames
====

Clojure RETE implementation for frames

Manners Benchmark Table
----

<table>
<tr><td>Test</td><td>CLIPS v 6.24 (msec)</td><td>rete4frames v 5.2.4 (msec)</td></tr>
<tr><td>manners8</td><td>1.4</td><td>62</td><td>x 44</td></tr>
<tr><td>manners16</td><td>18</td><td>183</td><td>x 10</td></tr>
<tr><td>manners32</td><td>259</td><td>920</td><td>x 3.6</td></tr>
<tr><td>manners64</td><td>8664</td><td>5869</td><td>x 0.68</td></tr>
<tr><td>manners128</td><td>316599</td><td>55391</td><td>x 0.17</td></tr>
</table>

Test results obtained on the same hardware and OS (Linux Mint 17, Qiana) 2 february 2016.
As can be seen from the results, rete4frames initially far behind and then starts to catch up CLIPS v 6.24.
On the most difficult test rete4frames almost 6 times faster than CLIPS v 6.24.

[Leiningen](https://github.com/technomancy/leiningen) dependency information:

```clj
 [rete "5.2.5-SNAPSHOT"]
```
[Maven](http://maven.apache.org/) dependency information:

```xml
<dependency>
  <groupId>rete</groupId>
  <artifactId>rete</artifactId>
  <version>5.2.5-SNAPSHOT</version>
</dependency>
```
To get the Manners Benchmark results run in REPL:

```clj
(require 'rete.core)
(in-ns 'rete.core)
(app "run" "examples/manners.clj" "examples/manners_f8.clj")
```
The benchmark table for Waltz algorithm is in [Waltz Benchmark Table] (https://github.com/rururu/rete4frames/blob/master/doc/waltz_benchmark.md)

Running examples:
```clj
(require 'rete.core)
(in-ns 'rete.core)
(app "run" "examples/mab.clj") ;; Monkey and bananas..
(app "run" "examples/zebra.clj") ;; Who drinks water, who owns zebra..
(app "run" "examples/auto.clj") ;; Automotive Expert System..
(app "run" "examples/sudoku/sudoku.clj" "examples/sudoku/grid3x3-p1.clj") ;; Game "Sudoku"
(app "run" "examples/hypertension.clj") ;; Doctor's expert system
Today: 19.2.2015..
What will do? [reception analyse end]
reception
What is a name of a next patient? (no-patients)
"Alice"
Patient: Alice, age: 70, race: nonblack
Last blood pressure values: systolic: 140, diastolic: 90, date: 18.2.2015
Well, Alice, let's measure your current blood pressure..
What are blood pressure values now? [systolic diastolic]
[140 90]
Good, Alice.
Continue prescribed medication.
                     Waiting for you after 18.3.2015, if all is well.
                     Good bye.
What is a name of a next patient? (no-patients)
...
```
Yet another example: "Real" air traffic control system - client-server ClojureScript-Clojure application based on "Flightradar24"  web service (http://www.flightradar24.com/), "Leaflet" JavaScript libraty (http://leafletjs.com/), httpkit, compojure, core.async and others Clojure libraries. This example is in a separate repository https://github.com/rururu/rete4flights.

And one more: es-boat - a prototype of an expert system for coastal navigation. It uses [Protege-3.5 ontology editor] (http://protege.stanford.edu/) as a knowledge representation system and server-side GUI, [OpenStreetMap] (https://wiki.openstreetmap.org/wiki/API) API, Leaflet JavaScript library, [Cesium] (https://cesiumjs.org/) WebGL virtual globe and map engine, [GeoNames] (http://www.geonames.org/) geographical database and Wikipedia. Link to this prpject: https://github.com/rururu/es-boat

Integrated Development Environment
----

IDE based on [Protege-3.5 ontology editor] (http://protege.stanford.edu):

Simple start IDE:
```clj
$ cd <..>/rete4frames
$ lein run
```
Start IDE for developers
```clj
$ cd <..>/rete4frames
$ lein repl
...
rete.protege=> (-main)
```

![screenshot](screenshot.jpg)

3.30 minute IDE [screencast] (https://www.youtube.com/watch?v=2Q9Y_jUDF8U).

How to include Rete for Frames into your Java programs see example [Eclipse project] (https://github.com/rururu/rete4frames/blob/master/Rete4framesEclipseTest/).

For further information see [Documentation] (https://github.com/rururu/rete4frames/blob/master/doc/intro.md)

To get this functionality in ClojureScript see [cljs-rete4f] (https://github.com/rururu/cljs-rete4f)

Copyright and license
----

Copyright © 2014-2016 Ruslan Sorokin.

Licensed under the EPL (see the file epl.html).
[License of Protege-3.5] (https://github.com/rururu/rete4frames/blob/master/LICENSE_PROTEGE)
