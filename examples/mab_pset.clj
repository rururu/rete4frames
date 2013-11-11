((hold-chest-to-put-on-floor
   0
   ((?g goal-is-to-action unlock
        goal-is-to-argument1 ?chest)
     (?t thing-name ?chest
         thing-on-top-of ?t2
         thing-weight light)
     (?t2 != floor)
     (?m monkey-holding ?t3)
     (?t3 != ?chest)
     (true not (exist ?g2 goal-is-to-action hold
                      goal-is-to-argument1 ?chest)))
   =>
   ((asser (gensym "Goal")
           goal-is-to-action hold
           goal-is-to-argument1 ?chest)))
  
  (put-chest-on-floor
    0
    ((?g goal-is-to-action unlock
         goal-is-to-argument1 ?chest)
      (?m monkey-location ?place
          monkey-on-top-of ?on
          monkey-holding ?chest)
      (?t thing-name ?chest
          thing-location ?place2
          thing-on-top-of ?on2))
    =>
    ((println (str "Monkey throws the " ?chest " off the " ?on " onto the floor."))
      (retract ?m monkey-holding ?chest)
      (asser ?m monkey-holding blank)
      (retract ?t thing-location ?place2
               thing-on-top-of ?on2)
      (asser ?t thing-location ?place
             thing-on-top-of floor)))
  
  (get-key-to-unlock
    0
    ((?g goal-is-to-action unlock
         goal-is-to-argument1 ?obj)
      (?t thing-name ?obj
          thing-on-top-of floor)
      (?c chest-name ?obj
          chest-unlocked-by ?key)
      (?m monkey-holding ?obj2)
      (?obj2 != ?key)
      (true not (exist ?g2 goal-is-to-action hold
                       goal-is-to-argument1 ?key)))
    =>
    ((asser (gensym "Goal")
           goal-is-to-action hold
           goal-is-to-argument1 ?key)))
  
  (move-to-chest-with-key
    0
    ((?g goal-is-to-action unlock
         goal-is-to-argument1 ?chest)
      (?m monkey-location ?mplace
          monkey-holding ?key)
      (?t thing-name ?chest
          thing-location ?cplace
          thing-on-top-of floor)
      (?mplace != ?cplace)
      (?c chest-name ?chest
          chest-unlocked-by ?key)
      (true not (exist ?g2 goal-is-to-action walk-to
                       goal-is-to-argument1 ?cplace)))
    =>
    ((asser (gensym "Goal")
           goal-is-to-action walk-to
           goal-is-to-argument1 ?cplace)))
  
  (unlock-chest-with-key
    0
    ((?g goal-is-to-action unlock
         goal-is-to-argument1 ?name)
      (?c chest-name ?name
          chest-contents ?contents
          chest-unlocked-by ?key)
      (?t thing-name ?name
          thing-location ?place
          thing-on-top-of ?on)
      (?m monkey-location ?place
          monkey-on-top-of ?on
          monkey-holding ?key))
    =>
    ((println (str "Monkey opens the " ?name " with the " ?key " revealing the " ?contents "." ))
      (retract ?c chest-contents ?contents)
      (asser ?c chest-contents nothings)
      (asser (gensym "Thing")
             thing-name ?contents
             thing-location ?place
             thing-on-top-of ?name
             thing-weight light)
      (retract ?g goal-is-to-action unlock
               goal-is-to-argument1 ?name)))
  
  (unlock-chest-to-hold-object
    0
    ((?g goal-is-to-action hold
         goal-is-to-argument1 ?obj)
      (?c chest-name ?chest
          chest-contents ?obj)
      (true not (exist ?g2 goal-is-to-action unlock
                       goal-is-to-argument1 ?chest)))
    
    =>
    ((asser (gensym "Goal")
            goal-is-to-action unlock
            goal-is-to-argument1 ?chest)))
  
  (use-ladder-to-hold
    0
    ((?g goal-is-to-action hold
         goal-is-to-argument1 ?obj)
      (?t thing-name ?obj
          thing-location ?place
          thing-on-top-of ceiling
          thing-weight light)
      (true not (exist ?t2 thing-name ladder
                       thing-location ?place))
      (true not (exist ?g2 goal-is-to-action move
                       goal-is-to-argument1 ladder
                       goal-is-to-argument2 ?place)))
      =>
      ((asser (gensym "Goal")
              goal-is-to-action move
              goal-is-to-argument1 ladder
              goal-is-to-argument2 ?place)))
  
  (climb-ladder-to-hold
    0
    ((?g goal-is-to-action hold
         goal-is-to-argument1 ?obj)
      (?t thing-name ?obj
          thing-location ?place
          thing-on-top-of ceiling
          thing-weight light)
      (?t2 thing-name ladder
           thing-location ?place
           thing-on-top-of floor)
      (?m monkey-on-top-of ?on)
      (?on != ladder)
      (true not (exist ?g2 goal-is-to-action on
                       goal-is-to-argument1 ladder)))
    =>
    ((asser (gensym "Goal")
            goal-is-to-action on
            goal-is-to-argument1 ladder)))
  
  (grab-object-from-ladder
    0
    ((?g goal-is-to-action hold
         goal-is-to-argument1 ?name)
      (?t thing-name ?name
          thing-location ?place
          thing-on-top-of ceiling
          thing-weight light)
      (?t2 thing-name ladder
           thing-location ?place)
      (?m monkey-location ?place
          monkey-on-top-of ladder
          monkey-holding blank))
    =>
    ((println (str "Monkey grabs the " ?name "."))
      (retract ?t thing-location ?place
               thing-on-top-of ceiling)
      (asser ?t thing-location held
             thing-on-top-of held)
      (retract ?m monkey-holding blank)
      (asser ?m monkey-holding ?name)
      (retract ?g goal-is-to-action hold
               goal-is-to-argument1 ?name)))
  
  (climb-to-hold
    0
    ((?g goal-is-to-action hold
         goal-is-to-argument1 ?obj)
      (?t thing-name ?obj
          thing-location ?place
          thing-on-top-of ?on
          thing-weight light)
      (?on != ceiling)
      (?m monkey-location ?place
          monkey-on-top-of ?on2)
      (?on2 != ?on)
      (true not (exist ?g2 goal-is-to-action on
                       goal-is-to-argument1 ?on)))
      =>
      ((asser (gensym "Goal")
              goal-is-to-action on
              goal-is-to-argument1 ?on)))
  
  (walk-to-hold
    0
    ((?g goal-is-to-action hold
         goal-is-to-argument1 ?obj)
      (?t thing-name ?obj
          thing-location ?place
          thing-on-top-of ?on
          thing-weight light)
      (?on != ceiling)
      (?m monkey-location ?mplace)
      (?mplace != ?place)
      (true not (exist ?g2 goal-is-to-action walk-to
                       goal-is-to-argument1 ?place)))
    =>
    ((asser (gensym "Goal")
            goal-is-to-action walk-to
            goal-is-to-argument1 ?place)))
  
  (drop-to-hold
    0
    ((?g goal-is-to-action hold
         goal-is-to-argument1 ?obj)
      (?t thing-name ?obj
          thing-location ?place
          thing-on-top-of ?on
          thing-weight light)
      (?m monkey-location ?place
          monkey-on-top-of ?on
          monkey-holding ?obj2)
      (?obj2 != blank)
      (true not (exist ?g2 goal-is-to-action hold
                       goal-is-to-argument1 blank)))
    =>
    ((asser (gensym "Goal")
            goal-is-to-action hold
            goal-is-to-argument1 blank)))
  
  (grab-object
    0
    ((?g goal-is-to-action hold
         goal-is-to-argument1 ?name)
      (?t thing-name ?name
          thing-location ?place
          thing-on-top-of ?on
          thing-weight light)
      (?m monkey-location ?place
          monkey-on-top-of ?on
          monkey-holding blank))
    =>
    ((println (str "Monkey grabs the " ?name "."))
      (retract ?t thing-location ?place
               thing-on-top-of ?on)
      (asser ?t thing-location held
             thing-on-top-of held)
      (retract ?m monkey-holding blank)
      (asser ?m monkey-holding ?name)
      (retract ?g goal-is-to-action hold
               goal-is-to-argument1 ?name)))
  
  (drop-object
    0
    ((?g goal-is-to-action hold
         goal-is-to-argument1 blank)
      (?m monkey-location ?place
          monkey-on-top-of ?on
          monkey-holding ?name)
      (?name != blank)
      (?t thing-name ?name
          thing-location ?place2
          thing-on-top-of ?on2))
    =>
    ((println (str "Monkey drops the " ?name "."))
      (retract ?m monkey-holding ?name)
      (asser ?m monkey-holding blank)
      (retract ?t thing-location ?place2
               thing-on-top-of ?on2)
      (asser ?t thing-location ?place
             thing-on-top-of ?on)
      (retract ?g goal-is-to-action hold
               goal-is-to-argument1 blank)))
  
  (unlock-chest-to-move-object
    0
    ((?g goal-is-to-action move
         goal-is-to-argument1 ?obj
         goal-is-to-argument2 ?place)
      (?c chest-name ?chest
          chest-contents ?obj)
      (true not (exist ?g2 goal-is-to-action unlock
                       goal-is-to-argument1 ?chest)))
    =>
    ((asser (gensym "Goal")
            goal-is-to-action unlock
            goal-is-to-argument1 ?chest)))
  
  (hold-object-to-move
    0
    ((?g goal-is-to-action move
         goal-is-to-argument1 ?obj
         goal-is-to-argument2 ?place)
      (?t thing-name ?obj
          thing-location ?place2
          thing-weight light)
      (?place2 != ?place)
      (?m monkey-holding ?obj2)
      (?obj2 != ?obj)
      (true not (exist ?g2 goal-is-to-action hold
                       goal-is-to-argument1 ?obj)))
    =>
    ((asser (gensym "Goal")
            goal-is-to-action hold
            goal-is-to-argument1 ?obj)))
  
  (move-object-to-place
    0
    ((?g goal-is-to-action move
         goal-is-to-argument1 ?obj
         goal-is-to-argument2 ?place)
      (?m monkey-location ?place2
          monkey-holding ?obj)
      (?place2 != ?place)
      (true not (exist ?g2 goal-is-to-action walk-to
                       goal-is-to-argument1 ?place)))
    =>
    ((asser (gensym "Goal")
            goal-is-to-action walk-to
            goal-is-to-argument1 ?place)))
  
  (drop-object-once-moved
    0
    ((?g goal-is-to-action move
         goal-is-to-argument1 ?name
         goal-is-to-argument2 ?place)
      (?m monkey-location ?place
          monkey-holding ?obj)
      (?t thing-name ?name
          thing-location ?place2
          thing-on-top-of ?on
          thing-weight light))
    =>
    ((println (str "Monkey drops the " ?name "."))
      (retract ?m monkey-holding ?obj)
      (asser ?m monkey-holding blank)
      (retract ?t thing-location ?place2
               thing-on-top-of ?on)
      (asser ?t thing-location ?place
             thing-on-top-of floor)
      (retract ?g goal-is-to-action move
               goal-is-to-argument1 ?name
               goal-is-to-argument2 ?place)))
  
  (already-moved-object
    0
    ((?g goal-is-to-action move
         goal-is-to-argument1 ?obj
         goal-is-to-argument2 ?place)
      (?t thing-name ?obj
          thing-location ?place))
    =>
    ((retract ?g goal-is-to-action move
              goal-is-to-argument1 ?obj
              goal-is-to-argument2 ?place)))
  
  (already-at-place
    0
    ((?g goal-is-to-action walk-to
         goal-is-to-argument1 ?place)
      (?m monkey-location ?place))
    =>
    ((retract ?g goal-is-to-action walk-to
              goal-is-to-argument1 ?place)))
  
  (get-on-floor-to-walk
    0
    ((?g goal-is-to-action walk-to
         goal-is-to-argument1 ?place)
      (?m monkey-location ?place2
          monkey-on-top-of ?on)
      (?place2 != ?place)
      (?on != floor)
      (true not (exist ?g2 goal-is-to-action on
                       goal-is-to-argument1 floor)))
    =>
    ((asser (gensym "Goal")
            goal-is-to-action on
            goal-is-to-argument1 floor)))
  
  (walk-holding-nothing
    0
    ((?g goal-is-to-action walk-to
         goal-is-to-argument1 ?place)
      (?m monkey-location ?place2
          monkey-on-top-of floor
          monkey-holding blank)
      (?place2 != ?place))
    =>
    ((println (str "Monkey walks to " ?place "."))
      (retract ?m monkey-location ?place2)
      (asser ?m monkey-location ?place)
      (retract ?g goal-is-to-action walk-to
               goal-is-to-argument1 ?place)))
  
  (walk-holding-object
    0
    ((?g goal-is-to-action walk-to
         goal-is-to-argument1 ?place)
      (?m monkey-location ?place2
          monkey-on-top-of floor
          monkey-holding ?obj)
      (?place2 != ?place)
      (?obj != blank))
    =>
    ((println (str "Monkey walks to " ?place " holding the " ?obj "."))
      (retract ?m monkey-location ?place2)
      (asser ?m monkey-location ?place)
      (retract ?g goal-is-to-action walk-to
         goal-is-to-argument1 ?place)))
  
  (jump-onto-floor
    0
    ((?g goal-is-to-action on
         goal-is-to-argument1 floor)
      (?m monkey-on-top-of ?on)
      (?on != floor))
    =>
    ((println (str "Monkey jumps off the " ?on " onto the floor."))
      (retract ?m monkey-on-top-of ?on)
      (asser ?m monkey-on-top-of floor)
      (retract ?g goal-is-to-action on
         goal-is-to-argument1 floor)))
  
  (walk-to-place-to-climb
    0
    ((?g goal-is-to-action on
         goal-is-to-argument1 ?obj)
      (?t thing-name ?obj
          thing-location ?place)
      (?m monkey-location ?place2)
      (?place2 != ?place)
      (true not (exist ?g2 goal-is-to-action walk-to
                       goal-is-to-argument1 ?place)))
    =>
    ((asser (gensym "Goal")
            goal-is-to-action walk-to
            goal-is-to-argument1 ?place)))
  
  (drop-to-climb
    0
    ((?g goal-is-to-action on
         goal-is-to-argument1 ?obj)
      (?t thing-name ?obj
          thing-location ?place)
      (?m monkey-location ?place
          monkey-holding ?obj2)
      (?obj2 != blank)
      (true not (exist ?g2 goal-is-to-action hold
                       goal-is-to-argument1 blank)))
    =>
    ((asser (gensym "Goal")
            goal-is-to-action hold
            goal-is-to-argument1 blank)))
  
  (climb-indirectly
    0
    ((?g goal-is-to-action on
         goal-is-to-argument1 ?obj)
      (?t thing-name ?obj
          thing-location ?place
          thing-on-top-of ?on)
      (?m monkey-location ?place
          monkey-on-top-of ?on2
          monkey-holding blank)
      (?on2 != ?on)
      (?on2 != ?obj)
      (true not (exist ?g goal-is-to-action on
                       goal-is-to-argument1 ?on)))
    =>
    ((asser (gensym "Goal")
            goal-is-to-action on
            goal-is-to-argument1 ?on)))
  
  (climb-directly
    0
    ((?g goal-is-to-action on
         goal-is-to-argument1 ?obj)
      (?t thing-name ?obj
          thing-location ?place
          thing-on-top-of ?on)
      (?m monkey-location ?place
          monkey-on-top-of ?on
          monkey-holding blank))
    =>
    ((println (str "Monkey climbs onto the " ?obj "."))
      (retract ?m monkey-on-top-of ?on)
      (asser ?m monkey-on-top-of ?obj)
      (retract ?g goal-is-to-action on
               goal-is-to-argument1 ?obj)))
  
  (already-on-object
    0
    ((?g goal-is-to-action on
         goal-is-to-argument1 ?obj)
      (?m monkey-on-top-of ?obj))
    =>
    ((retract ?g goal-is-to-action on
              goal-is-to-argument1 ?obj)))

  (hold-to-eat
    0
    ((?g goal-is-to-action eat
         goal-is-to-argument1 ?obj)
      (?m monkey-holding ?obj2)
      (?obj2 != ?obj)
      (true not (exist ?g2 goal-is-to-action hold
                       goal-is-to-argument1 ?obj)))
    =>
    ((asser (gensym "Goal")
            goal-is-to-action hold
            goal-is-to-argument1 ?obj)))
  
  (satisfy-hunger
    0
    ((?g goal-is-to-action eat
         goal-is-to-argument1 ?name)
      (?m monkey-holding ?name)
      (?t thing-name ?name))
    =>
    ((println (str "Monkey eats the " ?name "."))
      (retract ?m monkey-holding ?name)
      (asser ?m monkey-holding blank)
      (retract ?g goal-is-to-action eat
               goal-is-to-argument1 ?name)
      (retract ?t thing-name ?name))))

     