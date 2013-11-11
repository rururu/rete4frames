((Mann-Assign-First-Seat
   0
   ((context state start)
     (?g guest_name ?n)
     (count c ?c))
   =>
   ((retract context state start)
     (asser context state assign_seats)
     (asser ?c seating_seat1 1
            seating_name1 ?n
            seating_name2 ?n
            seating_seat2 1
            seating_pid 0
            seating_path_done yes)
     (asser ?c path_name ?n
            path_seat 1)
     (retract count c ?c)
     (asser count c (inc ?c))
     (println (str "seat 1 " ?n " " ?n " 1 " ?c " 0 1"))))
  
  (Mann-Find-Seating
    0
    ((context state assign_seats)
      (?id seating_seat1 ?seat1
           seating_seat2 ?seat2
           seating_name2 ?n2
           seating_pid ?pid
           seating_path_done yes)
      (?gst1 guest_name ?n2
             guest_sex ?s1
             guest_hobby ?h1)
      (?gst2 guest_name ?g2
             guest_sex ?s2
             guest_hobby ?h1)
      (?s1 != ?s2)
      (count c ?c)
      (true not (exist ?id path_name ?g2))
      (true not (exist ?id choosen_name ?g2
                       choosen_hobby ?h1)))
    =>
    ((retract context state assign_seats)
      (asser context state make_path)
      (asser  ?c seating_seat1 ?seat2
              seating_seat2 (inc ?seat2)
              seating_name1 ?n2
              seating_name2 ?g2
              seating_pid ?id
              seating_path_done no)
      (asser ?c path_name ?g2
             path_seat  (inc ?seat2))
      (asser ?id choosen_name ?g2
             choosen_hobby ?h1)
      (retract count c ?c)
      (asser count c (inc ?c))
      (println (str "seat " ?seat2 " " ?n2 " " ?g2))))

  (Mann-Are-We-Done
    1
    ((context state check_done)
      (?ls last_seat ?l_seat)
      (?id seating_seat2 ?l_seat))
    =>
    ((retract context state check_done)
      (asser context state print_results)
      (println "Yes, we are done!!")))
  
  (Mann-Continue
    0
    ((context state check_done))
    =>
    ((retract context state check_done)
      (asser context state assign_seats)))
  
  (Mann-Make-Path
    1
    ((context state make_path)
      (?id seating_pid ?pid
           seating_path_done no)
      (?pid path_name ?n1
            path_seat  ?s)
      (true not (exist ?id path_name ?n1)))
    =>
    ((asser ?id path_name ?n1
            path_seat ?s)))
  
  (Mann-Path-Done
    0
    ((context state make_path)
      (?id seating_path_done no))
    =>
    ((retract context state make_path)
      (asser context state check_done)
      (retract ?id seating_path_done no)
      (asser ?id seating_path_done yes)))
  
  (Mann-All-Done
    0
    ((context state print_results))
    =>
    ((println "Halt!")))
  
  (Mann-Print-Results
    1
    ((context state print_results)
      (?id seating_seat2 ?s2)
      (?ls last_seat ?s2)
      (?id path_name ?n
           path_seat  ?s))
    =>
    ((retract ?id path_name ?n
              path_seat  ?s)
      (println (str ?n " " ?s)))))
      