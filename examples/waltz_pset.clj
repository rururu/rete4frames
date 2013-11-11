((rules
(r-reverse-edges 0
	(stage value duplicate)
	(line p1 ?p1 p2 ?p2)
	=>
    (println (str " FIRE: reverse-edges p1 " ?p1 " p2 " ?p2))
	(asser edge p1 ?p1 p2 ?p2 joined FALSE)
    (asser edge p1 ?p2 p2 ?p1 joined FALSE)
	(retract :FIDS 1))

(r-done-reversing1 -10
	(stage value duplicate)
	=>
	(retract :FIDS 0)
	(asser stage value done-reversing1))
    
(r-done-reversing2 0
	(stage value done-reversing1)
	(not-exists line p1 ?p1 p2 ?p2)
	=>
    (println (str " FIRE: done-reversing"))
	(retract :FIDS 0)
	(asser stage value detect-3-junctions-M))
    
(r1-make-3-junction-M 0
	(stage value detect-3-junctions-M)
	(edge p1 ?base-point p2 ?p1 joined FALSE)
    (> ?base-point ?p1)
	(edge p1 ?base-point p2 ?p2 joined FALSE)
    (> ?p1 ?p2)
	(edge p1 ?base-point p2 ?p3 joined FALSE)
    (not= ?p1 ?p3)
	(not= ?p2 ?p3)
	=>
    (println (str " FIRE: make-3-junction-M bp " ?base-point " p1 " ?p1 " p2 " ?p2 " p3 " ?p3))
	(retract :FIDS 1 2 3)
	(asser edge p1 ?base-point p2 ?p1 joined TRUE)
	(asser edge p1 ?base-point p2 ?p2 joined TRUE)
	(asser edge p1 ?base-point p2 ?p3 joined TRUE))
)
(functions
)
(facts
))
