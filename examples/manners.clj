((templates
(context state)
(Guest name sex hobby)
(Count c)
(Seating id pid name1 name2 seat1 seat2 path_done)
(Path id name seat)
(Chosen id name hobby)
(Last seat)
)
(rules
(Mann-Assign-First-Seat
   0
   ?ctx (context state start)
   (Guest name ?n)
   ?cnt (Count c ?c)
   =>
   (modify ?ctx state assign_seats)
   (asser Seating seat1 1
   			name1 ?n
            name2 ?n
            seat2 1
            id ?c
            pid 0
            path_done yes)
   (asser Path id ?c
   			name ?n
            seat 1)
   (modify ?cnt c (inc ?c))
   (println (str "seat 1 " ?n " " ?n " 1 " ?c " 0 1")))

(Mann-Find-Seating
  0
  ?ctx (context state assign_seats)
  (Seating seat1 ?seat1
           seat2 ?seat2
           name2 ?n2
           id ?id
           pid ?pid
           path_done yes)
  (Guest name ?n2
           sex ?s1
           hobby ?h1)
  (Guest name ?g2
           sex ?s2
           hobby ?h1
    (not= ?s1 ?s2))
  ?cnt (Count c ?c)
  (not Path id ?id
           name ?g2)
  (not Chosen id ?id
           name ?g2 hobby ?h1)
  =>
  (modify ?ctx state make_path)
  (asser Seating seat1 ?seat2
           seat2 (inc ?seat2)
           name1 ?n2
           name2 ?g2
           id ?c
           pid ?id
           path_done no)
  (asser Path id ?c
  		   name ?g2
           seat (inc ?seat2))
  (asser Chosen id ?id
  		   name ?g2
           hobby ?h1)
  (modify ?cnt c (inc ?c))
  (println (str "seat " ?seat2 " " ?n2 " " ?g2)))

(Mann-Make-Path
  1
  (context state make_path)
  (Seating id ?id
  		   pid ?pid
           path_done no)
  (Path id ?pid
  		   name ?n1
         seat  ?s)
  (not Path id ?id
       name ?n1)
  =>
  (asser Path id ?id
  		   name ?n1
           seat ?s))

(Mann-Path-Done
  0
  ?ctx (context state make_path)
  ?sea (Seating path_done no)
  =>
  (modify ?ctx state check_done)
  (modify ?sea path_done yes))

(Mann-Are-We-Done
  1
  ?ctx (context state check_done)
  (Last seat ?l_seat)
  (Seating seat2 ?l_seat)
  =>
  (problem-solved)
  (modify ?ctx state print_results)
  (println "Yes, we are done!!"))

(Mann-Continue
  0
  ?ctx (context state check_done)
  =>
  (modify ?ctx state assign_seats))

(Mann-Print-Results
  0
  (context state print_results)
  (Seating id ?id seat2 ?s2)
  (Last seat ?s2)
  ?pth (Path id ?id
            name ?n
            seat  ?s)
  =>
  (retract ?pth)
  (println (str ?n " " ?s)))

(Mann-All-Done
  -1
  (context state print_results)
  =>
  (println "Halt!"))
)
(functions
)
(facts
(Guest name n1 sex m hobby h3)
(Guest name n1 sex m hobby h2)
(Guest name n2 sex m hobby h2)
(Guest name n2 sex m hobby h3)
(Guest name n3 sex m hobby h1)
(Guest name n3 sex m hobby h2)
(Guest name n3 sex m hobby h3)
(Guest name n4 sex f hobby h3)
(Guest name n4 sex f hobby h2)
(Guest name n5 sex f hobby h1)
(Guest name n5 sex f hobby h2)
(Guest name n5 sex f hobby h3)
(Guest name n6 sex f hobby h3)
(Guest name n6 sex f hobby h1)
(Guest name n6 sex f hobby h2)
(Guest name n7 sex f hobby h3)
(Guest name n7 sex f hobby h2)
(Guest name n8 sex m hobby h3)
(Guest name n8 sex m hobby h1)
(Last seat 8)
(Count c 1)
(context state start)
))

