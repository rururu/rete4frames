((Boi-Move-Time 
   4
   ((clock time1 ?t1
           time1 ?t2)
     (?t2 > ?t1))
   =>
   ((println [:TIME ?t2])
     (retract clock time1 ?t1)))
  
 (Boi-Temp-Change
   0
   ((?ob status ?s
         temp ?e1
         time1 ?t1)
     (mess object ?ob
           temp ?e2
           time1 ?t2)
     (?t2 > ?t1))
   =>
   ((println [:TEMP-CHANGE ?ob ?e2 ?t2])
     (retract mess object ?ob
              temp ?e2
              time1 ?t2)
     (retract ?ob temp ?e1
              time1 ?t1)
     (asser ?ob temp ?e2
            time1 ?t2)))
 
 (Boi-Status-Change
   0
   ((?ob status WAIT
         temp ?e
         time1 ?t)
     (?e > 45))
   =>
   ((println [:STATUS-CHANGE ?ob "START" ?t])
     (retract ?ob status WAIT)
     (asser ?ob status START))))