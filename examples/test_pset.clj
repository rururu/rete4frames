((P1 0
	(Planet name ?x size small on ?y)
	(Orbit color red size big name ?y length ?k width ?m)
	(> ?m ?k)
	=>
    (println (str ?y " better than " ?x))
	(println (+ ?k ?m))
    (asser Planet name ?y size medium)
    (retract :FIDS 0 1))
  
(P2 0
	(Figure name ?x length ?y width ?y)
    =>
    (println (str ?x " is square or circle")))
)
