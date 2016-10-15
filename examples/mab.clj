((templates
  (monkey
    location
    on-top-of
    holding)

  (thing
    name
    location
    on-top-of
    weight)

  (chest
    name
    contents
    unlocked-by)

  (goal-is-to
    action
    argument1
    argument2))
(rules
  (hold-chest-to-put-on-floor
    0
    (goal-is-to action unlock argument1 ?chest)
    (thing name ?chest on-top-of ?surf weight light
      (not= ?surf floor))
    (monkey holding ?thing
      (not= ?thing ?chest))
    (not goal-is-to action hold argument1 ?chest)
    =>
    (asser goal-is-to action hold argument1 ?chest))

  (put-chest-on-floor
    0
    (goal-is-to action unlock argument1 ?chest)
    ?monkey (monkey location ?place on-top-of ?on holding ?chest)
    ?thing (thing name ?chest)
    =>
    (println (str "Monkey throws the " ?chest " off the " ?on " onto the floor." ))
    (modify ?monkey holding blank)
    (modify ?thing location ?place on-top-of floor))

  (get-key-to-unlock
    0
    (goal-is-to action unlock argument1 ?obj)
    (thing name ?obj on-top-of floor)
    (chest name ?obj unlocked-by ?key)
    (monkey holding ?thing
      (not= ?thing ?key))
    (not goal-is-to action hold argument1 ?key)
    =>
    (asser goal-is-to action hold argument1 ?key))

  (move-to-chest-with-key
    0
    (goal-is-to action unlock argument1 ?chest)
    (monkey location ?mplace holding ?key)
    (thing name ?chest location ?cplace on-top-of floor
      (not= ?cplace ?mplace))
    (chest name ?chest unlocked-by ?key)
    (not goal-is-to action walk-to argument1 ?cplace)
    =>
    (asser goal-is-to action walk-to argument1 ?cplace))

  (unlock-chest-with-key
    0
    ?goal (goal-is-to action unlock argument1 ?name)
    ?chest (chest name ?name contents ?contents unlocked-by ?key)
    (thing name ?name location ?place on-top-of ?on)
    (monkey location ?place on-top-of ?on holding ?key)
    =>
    (println (str "Monkey opens the " ?name " with the " ?key " revealing the " ?contents "." ))
    (modify ?chest contents nothing)
    (asser thing name ?contents location ?place on-top-of ?name weight light)
    (retract ?goal))

  (unlock-chest-to-hold-object
    0
    (goal-is-to action hold argument1 ?obj)
    (chest name ?chest contents ?obj)
    (not goal-is-to action unlock argument1 ?chest)
    =>
    (asser goal-is-to action unlock argument1 ?chest))

  (use-ladder-to-hold
    0
    (goal-is-to action hold argument1 ?obj)
    (thing name ?obj location ?place on-top-of ceiling weight light)
    (not thing name ladder location ?place)
    (not goal-is-to action move argument1 ladder argument2 ?place)
    =>
    (asser goal-is-to action move argument1 ladder argument2 ?place))

  (climb-ladder-to-hold
    0
    (goal-is-to action hold argument1 ?obj)
    (thing name ?obj location ?place on-top-of ceiling weight light)
    (thing name ladder location ?place on-top-of floor)
    (monkey on-top-of ?s
      (not= ?s ladder))
    (not goal-is-to action on argument1 ladder)
    =>
    (asser goal-is-to action on argument1 ladder))

  (grab-object-from-ladder
   0
   ?goal (goal-is-to action hold argument1 ?name)
   ?thing (thing name ?name location ?place on-top-of ceiling weight light)
   (thing name ladder location ?place)
   ?monkey (monkey location ?place on-top-of ladder holding blank)
   =>
   (println (str "Monkey grabs the " ?name "." ))
   (modify ?thing location held on-top-of held)
   (modify ?monkey holding ?name)
   (retract ?goal))

  (climb-to-hold
    0
    (goal-is-to action hold argument1 ?obj)
    (thing name ?obj location ?place on-top-of ?on weight light
      (not= ?on ceiling))
    (monkey location ?place on-top-of ?surf
      (not= ?surf ?on))
    (not goal-is-to action on argument1 ?on)
    =>
    (asser goal-is-to action on argument1 ?on))

  (walk-to-hold
    0
    (goal-is-to action hold argument1 ?obj)
    (thing name ?obj location ?place on-top-of ?surf weight light
      (not= ?surf ceiling))
    (monkey location ?loc
      (not= ?loc ?place))
    (not goal-is-to action walk-to argument1 ?place)
    =>
    (asser goal-is-to action walk-to argument1 ?place))

  (drop-to-hold
    0
    (goal-is-to action hold argument1 ?obj)
    (thing name ?obj location ?place on-top-of ?on weight light)
    (monkey location ?place on-top-of ?on holding ?hold
      (not= ?hold blank))
    (not goal-is-to action hold argument1 blank)
    =>
    (asser goal-is-to action hold argument1 blank))

  (grab-object
    0
    ?goal (goal-is-to action hold argument1 ?name)
    ?thing (thing name ?name location ?place on-top-of ?on weight light)
    ?monkey (monkey location ?place on-top-of ?on holding blank)
    =>
    (println (str "Monkey grabs the " ?name "." ))
    (modify ?thing location held on-top-of held)
    (modify ?monkey holding ?name)
    (retract ?goal))

  (drop-object
    0
    ?goal (goal-is-to action hold argument1 blank)
    ?monkey (monkey location ?place on-top-of ?on holding ?name
      (not= ?name blank))
    ?thing (thing name ?name)
    =>
    (println (str "Monkey drops the " ?name "." ))
    (modify ?monkey holding blank)
    (modify ?thing location ?place on-top-of ?on)
    (retract ?goal))

  (unlock-chest-to-move-object
    0
    (goal-is-to action move argument1 ?obj argument2 ?place)
    (chest name ?chest contents ?obj)
    (not goal-is-to action unlock argument1 ?chest)
    =>
    (asser goal-is-to action unlock argument1 ?chest))

  (hold-object-to-move
    0
    (goal-is-to action move argument1 ?obj argument2 ?place)
    (thing name ?obj location ?loc weight light
      (not= ?loc ?place))
    (monkey holding ?hold
      (not= ?hold ?obj))
    (not goal-is-to action hold argument1 ?obj)
    =>
    (asser goal-is-to action hold argument1 ?obj))

  (move-object-to-place
    0
    (goal-is-to action move argument1 ?obj argument2 ?place)
    (monkey location ?loc holding ?obj
      (not= ?loc ?place))
    (not goal-is-to action walk-to argument1 ?place)
    =>
    (asser goal-is-to action walk-to argument1 ?place))

  (drop-object-once-moved
    0
    ?goal (goal-is-to action move argument1 ?name argument2 ?place)
    ?monkey (monkey location ?place holding ?obj)
    ?thing (thing name ?name weight light)
    =>
    (println (str "Monkey drops the " ?name "." ))
    (modify ?monkey holding blank)
    (modify ?thing location ?place on-top-of floor)
    (retract ?goal))

  (already-moved-object
    0
    ?goal (goal-is-to action move argument1 ?obj argument2 ?place)
    (thing name ?obj location ?place)
    =>
    (retract ?goal))

  (already-at-place
    0
    ?goal (goal-is-to action walk-to argument1 ?place)
    (monkey location ?place)
    =>
    (retract ?goal))

  (get-on-floor-to-walk
    0
    (goal-is-to action walk-to argument1 ?place)
    (monkey location ?loc on-top-of ?surf
      ((not= ?loc ?place)
       (not= ?surf floor)))
    (not goal-is-to action on argument1 floor)
    =>
    (asser goal-is-to action on argument1 floor))

  (walk-holding-nothing
    0
    ?goal (goal-is-to action walk-to argument1 ?place)
    ?monkey (monkey location ?loc on-top-of floor holding blank
      (not= ?loc ?place))
    =>
    (println (str "Monkey walks to " ?place "." ))
    (modify ?monkey location ?place)
    (retract ?goal))

  (walk-holding-object
    0
    ?goal (goal-is-to action walk-to argument1 ?place)
    ?monkey (monkey location ?loc on-top-of floor holding ?obj
      ((not= ?loc ?place)
       (not= ?obj blank)))
    =>
    (println (str "Monkey walks to " ?place " holding the " ?obj "." ))
    (modify ?monkey location ?place)
    (retract ?goal))

  (jump-onto-floor
    0
    ?goal (goal-is-to action on argument1 floor)
    ?monkey (monkey on-top-of ?on
      (not= ?on floor))
    =>
    (println (str "Monkey jumps off the " ?on " onto the floor." ))
    (modify ?monkey on-top-of floor)
    (retract ?goal))

  (walk-to-place-to-climb
    0
    (goal-is-to action on argument1 ?obj)
    (thing name ?obj location ?place)
    (monkey location ?loc
      (not= ?loc ?place))
    (not goal-is-to action walk-to argument1 ?place)
    =>
    (asser goal-is-to action walk-to argument1 ?place))

  (drop-to-climb
    0
    (goal-is-to action on argument1 ?obj)
    (thing name ?obj location ?place)
    (monkey location ?place holding ?hold
      (not= ?hold blank))
    (not goal-is-to action hold argument1 blank)
    =>
    (asser goal-is-to action hold argument1 blank))

  (climb-indirectly
    0
    (goal-is-to action on argument1 ?obj)
    (thing name ?obj location ?place on-top-of ?on)
    (monkey location ?place on-top-of ?on2 holding blank
      ((not= ?on2 ?on)
       (not= ?on2 ?obj)))
    (not goal-is-to action on argument1 ?on)
    =>
    (asser goal-is-to action on argument1 ?on))

  (climb-directly
    0
    ?goal (goal-is-to action on argument1 ?obj)
    (thing name ?obj location ?place on-top-of ?on)
    ?monkey (monkey location ?place on-top-of ?on holding blank)
    =>
    (println (str "Monkey climbs onto the " ?obj "." ))
    (modify ?monkey on-top-of ?obj)
    (retract ?goal))

  (already-on-object
    0
    ?goal (goal-is-to action on argument1 ?obj)
    (monkey on-top-of ?obj)
    =>
    (retract ?goal))

  (hold-to-eat
    0
    (goal-is-to action eat argument1 ?obj)
    (monkey holding ?h
      (not= ?h ?obj))
    (not goal-is-to action hold argument1 ?obj)
    =>
    (asser goal-is-to action hold argument1 ?obj))

  (satisfy-hunger
    0
    ?goal (goal-is-to action eat argument1 ?name)
    ?monkey (monkey holding ?name)
    ?thing (thing name ?name)
    =>
    (println (str "Monkey eats the " ?name "." ))
    (modify ?monkey holding blank)
    (retract ?goal ?thing)
    (problem-solved)))

(functions)

(facts
  (monkey location t5-7 on-top-of green-couch holding blank)
  (thing name green-couch location t5-7 weight heavy on-top-of floor)
  (thing name red-couch location t2-2 weight heavy on-top-of floor)
  (thing name big-pillow location t2-2 on-top-of red-couch weight light)
  (thing name red-chest location t2-2 on-top-of big-pillow weight light)
  (chest name red-chest contents ladder unlocked-by red-key)
  (thing name blue-chest location t7-7 on-top-of ceiling weight light)
  (chest name blue-chest contents bananas unlocked-by blue-key)
  (thing name blue-couch location t8-8 weight heavy on-top-of floor)
  (thing name green-chest location t8-8 on-top-of ceiling weight light)
  (chest name green-chest contents blue-key unlocked-by red-key)
  (thing name red-key location t1-3 on-top-of floor weight light)
  (goal-is-to action eat argument1 bananas)))
