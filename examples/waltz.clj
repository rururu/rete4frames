((templates
  (stage value)
  (line p1 p2)
  (edge p1 p2 joined label plotted)
  (junct p1 p2 p3 base-point type))
(rules
(r-reverse-edges 0
	(stage value duplicate)
	?l (line p1 ?p1 p2 ?p2)
	=>
	(asser edge p1 ?p1 p2 ?p2 joined FALSE label NIL plotted NIL)
  (asser edge p1 ?p2 p2 ?p1 joined FALSE label NIL plotted NIL)
	(retract ?l))

(r-done-reversing -10
	?s (stage value duplicate)
 	=>
  (println (str " FIRE: done-reversing"))
	(modify ?s value detect-junctions))

(r-make-3-junction 10
	(stage value detect-junctions)
	?e1 (edge p1 ?base-point p2 ?p1 joined FALSE)
	?e2 (edge p1 ?base-point p2 ?p2 joined FALSE
        (> ?p1 ?p2))
	?e3 (edge p1 ?base-point p2 ?p3 joined FALSE
        (> ?p2 ?p3))
	=>
	(modify ?e1 joined TRUE)
	(modify ?e2 joined TRUE)
	(modify ?e3 joined TRUE)
  (wz/make-3-junction ?base-point ?p1 ?p2 ?p3))

(r-make-L 0
	(stage value detect-junctions)
	?e1 (edge p1 ?base-point p2 ?p2 joined FALSE)
	?e2 (edge p1 ?base-point p2 ?p3 joined FALSE
        (> ?p2 ?p3))
	(not edge p1 ?base-point p2 ?p4
        ((not= ?p4 ?p2) (not= ?p4 ?p3)))
	=>
	(modify ?e1 joined TRUE)
	(modify ?e2 joined TRUE)
	(asser junct type L
         base-point ?base-point
         p1 ?p2
         p2 ?p3))

(r-done-detecting -10
	?s (stage value detect-junctions)
	=>
  (println (str " FIRE: done-detecting"))
	(modify ?s value find-initial-boundary))

(r-initial-boundary-junction-L 0
	?s (stage value find-initial-boundary)
  (junct type L
           base-point ?base-point
           p1 ?p1
           p2 ?p2)
	?e1 (edge p1 ?base-point p2 ?p1)
	?e2 (edge p1 ?base-point p2 ?p2)
  (not junct base-point ?bp
       (> ?bp ?base-point))
	=>
  (println (str " FIRE: done find-initial-boundary"))
	(modify ?s value find-second-boundary)
  (modify ?e1 label B)
	(modify ?e2 label B))

(r-initial-boundary-junction-arrow 0
	?s (stage value find-initial-boundary)
	(junct type arrow base-point ?bp p1 ?p1 p2 ?p2 p3 ?p3)
	?e1 (edge p1 ?bp p2 ?p1)
	?e2 (edge p1 ?bp p2 ?p2)
	?e3 (edge p1 ?bp p2 ?p3)
  (not junct base-point ?base-point
       (> ?base-point ?bp))
	=>
  (println (str " FIRE: done find-initial-boundary"))
	(modify ?s value find-second-boundary)
  (modify ?e1 label B)
	(modify ?e2 label PLUS)
	(modify ?e3 label B))

(r-second-boundary-junction-L 0
	?s (stage value find-second-boundary)
  (junct type L base-point ?base-point p1 ?p1 p2 ?p2)
	?e1 (edge p1 ?base-point p2 ?p1)
	?e2 (edge p1 ?base-point p2 ?p2)
  (not junct base-point ?bp
       (< ?bp ?base-point))
	=>
  (println (str " FIRE: done find-second-boundary"))
  (modify ?s value labeling)
  (modify ?e1 label B)
  (modify ?e2 label B))

(r-second-boundary-junction-arrow 0
	?s (stage value find-second-boundary)
	(junct type arrow base-point ?bp p1 ?p1 p2 ?p2 p3 ?p3)
	?e1 (edge p1 ?bp p2 ?p1)
	?e2 (edge p1 ?bp p2 ?p2)
	?e3 (edge p1 ?bp p2 ?p3)
  (not junct base-point ?base-point
       (< ?base-point ?bp))
	=>
  (println (str " FIRE: done find-second-boundary"))
	(modify ?s value labeling)
  (modify ?e1 label B)
	(modify ?e2 label PLUS)
	(modify ?e3 label B))

(r-match-edge 0
	(stage value labeling)
	?e1 (edge p1 ?p1 p2 ?p2 label ?l
            [(= ?l PLUS) (= ?l MINUS) (= ?l B)])
	?e2 (edge p1 ?p2 p2 ?p1 label NIL)
	=>
	(modify ?e1 plotted TRUE)
	(modify ?e2 label ?l plotted TRUE))

(r-label-L 0
	(stage value labeling)
	(junct type L base-point ?p1)
	(edge p1 ?p1 p2 ?p2 label ?l
        [(= ?l PLUS) (= ?l MINUS)])
	?e2 (edge p1 ?p1 p2 ?p3 label NIL
            (not= ?p3 ?p2))
	=>
	(modify ?e2 label B))

(r-label-tee-A 5
	(stage value labeling)
	(junct type tee base-point ?bp p1 ?p1 p2 ?p2 p3 ?p3)
	?e1 (edge p1 ?bp p2 ?p1 label NIL)
	?e2 (edge p1 ?bp p2 ?p3)
	=>
  (modify ?e1 label B)
	(modify ?e2 label B))

(r-label-tee-B 0
	(stage value labeling)
	(junct type tee base-point ?bp p1 ?p1 p2 ?p2 p3 ?p3)
	?e1 (edge p1 ?bp p2 ?p1)
	?e2 (edge p1 ?bp p2 ?p3 label NIL)
	=>
  (modify ?e1 label B)
	(modify ?e2 label B))

(r-label-fork-1 0
	(stage value labeling)
	(junct type fork base-point ?bp)
	(edge p1 ?bp p2 ?p1 label PLUS)
	?e2 (edge p1 ?bp p2 ?p2 label NIL
            (not= ?p2 ?p1))
	?e3 (edge p1 ?bp p2 ?p3
            ((not= ?p3 ?p2)
             (not= ?p3 ?p1)))
	=>
	(modify ?e2 label PLUS)
	(modify ?e3 label PLUS))

(r-label-fork-2 0
	(stage value labeling)
	(junct type fork base-point ?bp)
	(edge p1 ?bp p2 ?p1 label B)
	(edge p1 ?bp p2 ?p2 label MINUS
        (not= ?p2 ?p1))
	?e3 (edge p1 ?bp p2 ?p3 label NIL
            ((not= ?p3 ?p2)
             (not= ?p3 ?p1)))
	=>
	(modify ?e3 label B))

(r-label-fork-3 0
	(stage value labeling)
	(junct type fork base-point ?bp)
	(edge p1 ?bp p2 ?p1 label B)
	(edge p1 ?bp p2 ?p2 label B
        (not= ?p2 ?p1))
	?e3 (edge p1 ?bp p2 ?p3 label NIL
            ((not= ?p3 ?p2)
             (not= ?p3 ?p1)))
	=>
	(modify ?e3 label MINUS))

(r-label-fork-4 0
	(stage value labeling)
	(junct type fork base-point ?bp)
	(edge p1 ?bp p2 ?p1 label MINUS)
	(edge p1 ?bp p2 ?p2 label MINUS
        (not= ?p2 ?p1))
	?e3 (edge p1 ?bp p2 ?p3 label NIL
            ((not= ?p3 ?p2)
             (not= ?p3 ?p1)))
	=>
	(modify ?e3 label MINUS))

(r-label-arrow-1A 5
	(stage value labeling)
	(junct type arrow base-point ?bp p1 ?p1 p2 ?p2 p3 ?p3)
	(edge p1 ?bp p2 ?p1 label ?l
        [(= ?l B) (= ?l MINUS)])
	?e2 (edge p1 ?bp p2 ?p2 label NIL)
	?e3 (edge p1 ?bp p2 ?p3)
	=>
	(modify ?e2 label PLUS)
	(modify ?e3 label ?l))

(r-label-arrow-1B 0
	(stage value labeling)
	(junct type arrow base-point ?bp p1 ?p1 p2 ?p2 p3 ?p3)
	(edge p1 ?bp p2 ?p1 label ?l
        [(= ?l MINUS) (= ?l B)])
	?e2 (edge p1 ?bp p2 ?p2)
	?e3 (edge p1 ?bp p2 ?p3 label NIL)
	=>
	(modify ?e2 label PLUS)
	(modify ?e3 label ?l))

(r-label-arrow-2A 5
	(stage value labeling)
	(junct type arrow base-point ?bp p1 ?p1 p2 ?p2 p3 ?p3)
	(edge p1 ?bp p2 ?p3 label ?l
        [(= ?l B) (= ?l MINUS)])
	?e2 (edge p1 ?bp p2 ?p2 label NIL)
	?e3 (edge p1 ?bp p2 ?p1)
	=>
	(modify ?e2 label PLUS)
	(modify ?e3 label ?l))

(r-label-arrow-2B 0
	(stage value labeling)
	(junct type arrow base-point ?bp p1 ?p1 p2 ?p2 p3 ?p3)
	(edge p1 ?bp p2 ?p3 label ?l
        [(= ?l B) (= ?l MINUS)])
	?e2 (edge p1 ?bp p2 ?p2)
	?e3 (edge p1 ?bp p2 ?p1 label NIL)
	=>
	(modify ?e2 label PLUS)
	(modify ?e3 label ?l))

(r-label-arrow-3A 5
	(stage value labeling)
	(junct type arrow base-point ?bp p1 ?p1 p2 ?p2 p3 ?p3)
	(edge p1 ?bp p2 ?p1 label PLUS)
	?e2 (edge p1 ?bp p2 ?p2 label NIL)
	?e3 (edge p1 ?bp p2 ?p3)
	=>
	(modify ?e2 label MINUS)
	(modify ?e3 label PLUS))

(r-label-arrow-3B 0
	(stage value labeling)
	(junct type arrow base-point ?bp p1 ?p1 p2 ?p2 p3 ?p3)
	(edge p1 ?bp p2 ?p1 label PLUS)
	?e2 (edge p1 ?bp p2 ?p2)
	?e3 (edge p1 ?bp p2 ?p3 label NIL)
	=>
	(modify ?e2 label MINUS)
	(modify ?e3 label PLUS))

(r-label-arrow-4A 5
	(stage value labeling)
	(junct type arrow base-point ?bp p1 ?p1 p2 ?p2 p3 ?p3)
	(edge p1 ?bp p2 ?p3 label PLUS)
	?e2 (edge p1 ?bp p2 ?p2 label NIL)
	?e3 (edge p1 ?bp p2 ?p1)
	=>
	(modify ?e2 label MINUS)
	(modify ?e3 label PLUS))

(r-label-arrow-4B 0
	(stage value labeling)
	(junct type arrow base-point ?bp p1 ?p1 p2 ?p2 p3 ?p3)
	(edge p1 ?bp p2 ?p3 label PLUS)
	?e2 (edge p1 ?bp p2 ?p2)
	?e3 (edge p1 ?bp p2 ?p1 label NIL)
	=>
	(modify ?e2 label MINUS)
	(modify ?e3 label PLUS))

(r-label-arrow-5A 5
	(stage value labeling)
	(junct type arrow base-point ?bp p1 ?p1 p2 ?p2 p3 ?p3)
	(edge p1 ?bp p2 ?p2 label MINUS)
	?e2 (edge p1 ?bp p2 ?p1)
	?e3 (edge p1 ?bp p2 ?p3 label NIL)
	=>
	(modify ?e2 label PLUS)
	(modify ?e3 label PLUS))

(r-label-arrow-5B 0
	(stage value labeling)
	(junct type arrow base-point ?bp p1 ?p1 p2 ?p2 p3 ?p3)
	(edge p1 ?bp p2 ?p2 label MINUS)
	?e2 (edge p1 ?bp p2 ?p1 label NIL)
	?e3 (edge p1 ?bp p2 ?p3)
	=>
	(modify ?e2 label PLUS)
	(modify ?e3 label PLUS))

(r-done-labeling -10
	?s (stage value labeling)
	=>
  (println (str " FIRE: done-labeling"))
	(modify ?s value plot-boundaries))

(r-plot-boundaries 5
	(stage value plot-boundaries)
	?e (edge plotted NIL label NIL p1 ?p1 p2 ?p2)
	=>
	(modify ?e plotted TRUE))

(r-done-plot-boundaries 0
	?s (stage value plot-boundaries)
	=>
  (println (str " FIRE: done-plot-boundaries"))
	(modify ?s value plot-remaining-edges))

(r-plot-remaining-edges 5
  (stage value plot-remaining-edges)
  ?e (edge plotted NIL)
  =>
  (modify ?e plotted TRUE))

(r-done-plotting 0
	?s (stage value plot-remaining-edges)
	=>
  (println (str " FIRE: done-plotting"))
	(modify ?s value done))
)
(functions
(ns wz)

(defn get-y [val]
  (mod val 100))

(defn get-x [val]
  (int (/ val 100)))

(defn get-angle [?p1 ?p2]
  (let [?delta-x (- (get-x ?p2) (get-x ?p1))
        ?delta-y (- (get-y ?p2) (get-y ?p1))]
    (if (= ?delta-x 0)
      (if (> ?delta-y 0)
        (/ (Math/PI) 2)
        (/ (Math/PI) -2))
      (if (= ?delta-y 0)
        (if (> ?delta-x 0)
          0.0
          (Math/PI))
        (Math/atan2 ?delta-y ?delta-x)))))

(defn inscribed-angle [?basepoint ?p1 ?p2]
  (let [?angle1 (get-angle ?basepoint ?p1)
        ?angle2 (get-angle ?basepoint ?p2)
        ?temp0 (- ?angle1 ?angle2)
        ?temp1 (if (< ?temp0 0) (- 0 ?temp0) ?temp0)
        ?temp2 (if (> ?temp1 (Math/PI)) (- (* 2 (Math/PI)) ?temp1) ?temp1)]
    (if (< ?temp2 0)
      (- 0 ?temp2)
      ?temp2)))

(defn make-3-junction [?basepoint ?p1 ?p2 ?p3]
  (let [?angle12 (inscribed-angle ?basepoint ?p1 ?p2)
        ?angle13 (inscribed-angle ?basepoint ?p1 ?p3)
        ?angle23 (inscribed-angle ?basepoint ?p2 ?p3)
        ?sum1213 (+ ?angle12 ?angle13)
        ?sum1223 (+ ?angle12 ?angle23)
        ?sum1323 (+ ?angle13 ?angle23)
        [?sum ?shaft ?barb1 ?barb2]
          (if (< ?sum1213 ?sum1223)
            (if (< ?sum1213 ?sum1323)
              [?sum1213 ?p1 ?p2 ?p3]
              [?sum1323 ?p3 ?p1 ?p2])
            (if (< ?sum1223 ?sum1323)
              [?sum1223 ?p2 ?p1 ?p3]
              [?sum1323 ?p3 ?p1 ?p2]))
        ?delta0 (- ?sum (Math/PI))
        ?delta1 (if (< ?delta0 0) (- 0 ?delta0) ?delta0)
        ?type (if (< ?delta1 0.001)
                'tee
                (if (> ?sum (Math/PI))
                  'fork
                  'arrow))
        p1 (int ?barb1)
        p2 (int ?shaft)
        p3 (int ?barb2)
        bp (int ?basepoint)
        frm (list 'junct 'p1 p1 'p2 p2 'p3 p3
          'base-point bp 'type ?type)]
    (rete.core/assert-frame frm)))
)
(facts
(line p1 122 p2 107)
(line p1 107 p2 2207)
(line p1 2207 p2 3204)
(line p1 3204 p2 6404)
(line p1 2216 p2 2207)
(line p1 3213 p2 3204)
(line p1 2216 p2 3213)
(line p1 107 p2 2601)
(line p1 2601 p2 7401)
(line p1 6404 p2 7401)
(line p1 3213 p2 6413)
(line p1 6413 p2 6404)
(line p1 7416 p2 7401)
(line p1 5216 p2 6413)
(line p1 2216 p2 5216)
(line p1 122 p2 5222)
(line p1 5222 p2 7416)
(line p1 5222 p2 5216)
(stage value duplicate)
))
