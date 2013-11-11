((templates
(stage 
   value)
   
(edge
   p1
   p2
   joined
   label
   plotted)  
)   
(rules 
(r-match-edge 0
	(stage ?s value duplicate)
	(edge ?e1 p1 ?p1 p2 ?p2 label ?label)
    ((wz/mor (= ?label MINUS) (= ?label B) (= ?label PLUS)) = true)
	=>
    (println (str "r-m-e " ?e1 " " ?label " " ?p1)))
)
(facts
(edge ?e p1 P1 p2 P2 label B)
(edge ?e p1 P3 p2 P4 label PLUS)
(edge ?e p1 P3 p2 P4 label B)
(edge ?e p1 P5 p2 P6 label MINUS)
(edge ?e p1 P5 p2 P6 label A)
(stage ?s value duplicate)
)
(functions
(ns wz)
(defn mor [x y z]
  (or x y z))
))
