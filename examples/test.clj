((templates
	(Planet name size on)
	(Orbit color size name length width)
)
(rules
(P1 0
	?f1 (Planet name ?x size small on ?y)
	?f2 (Orbit color red size big name ?y length ?k width ?m
     [(> ?m ?k) (= ?m 33)])
	=>
    (println (str ?y " better than " ?x))
	(println (+ ?k ?m))
    (modify ?f2 color green length (* ?k ?m))
    (retract ?f1))
)
(functions
)
(facts
(Orbit name orb2
    size big
	color red
    width 22
    length 17)
(Planet name moon
    on orb2
	size small)
))
