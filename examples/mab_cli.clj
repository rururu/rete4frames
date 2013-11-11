((templates
(monkey 
  location :default green-couch
  on-top-of :default floor 
  holding :default nothing)

(thing 
  name
  location
  on-top-of :default floor
  weight :default light)

(chest
  name
  contents
  unlocked-by)

(goal-is-to
  action
  argument1
  argument2)
)

(rules
(hold-chest-to-put-on-floor 0 
  (goal-is-to ?g action unlock argument1 ?chest)
  (thing ?t name ?chest on-top-of (not floor) weight light)
  (monkey ?m holding (not ?chest))
  (not (goal-is-to ?x action hold argument1 ?chest))
  =>
  (assert (goal-is-to ?x action hold argument1 ?chest)))

(put-chest-on-floor 0 
  (goal-is-to ?g action unlock argument1 ?chest)
  (monkey ?monkey location ?place on-top-of ?on holding ?chest)
  (thing ?thing name ?chest)
  =>
  (println (str "Monkey throws the " ?chest " off the " 
              ?on " onto the floor." ))
  (modify (monkey ?monkey holding blank))
  (modify (thing ?thing location ?place on-top-of floor)))

(get-key-to-unlock 0 
  (goal-is-to ?g action unlock argument1 ?obj)
  (thing ?t name ?obj on-top-of floor)
  (chest ?c name ?obj unlocked-by ?key)
  (monkey ?m holding (not ?key))
  (not (goal-is-to ?x action hold argument1 ?key))
  =>
  (assert (goal-is-to ?x action hold argument1 ?key)))

(move-to-chest-with-key 0 
  (goal-is-to ?g action unlock argument1 ?chest)
  (monkey ?m location ?mplace holding ?key)
  (thing ?t name ?chest location ?cplace on-top-of floor)
  (?cplace != ?mplace)
  (chest ?c name ?chest unlocked-by ?key)
  (not (goal-is-to ?x action walk-to argument1 ?cplace))
  =>
  (assert (goal-is-to ?x action walk-to argument1 ?cplace)))

(unlock-chest-with-key 0 
  (goal-is-to ?goal action unlock argument1 ?name)
  (chest ?chest name ?name contents ?contents unlocked-by ?key)
  (thing ?t name ?name location ?place on-top-of ?on)
  (monkey ?m location ?place on-top-of ?on holding ?key)
  =>
  (println (str "Monkey opens the " ?name " with the " ?key 
              " revealing the " ?contents "." ))
  (modify (chest ?chest contents nothing))
  (assert (thing ?t name ?contents location ?place on-top-of ?name))
  (retract ?goal))

(unlock-chest-to-hold-object 0
  (goal-is-to ?g action hold argument1 ?obj)
  (chest ?c name ?chest contents ?obj)
  (not (goal-is-to ?x action unlock argument1 ?chest))
  =>
  (assert (goal-is-to ?x action unlock argument1 ?chest)))

(use-ladder-to-hold 0
  (goal-is-to ?g action hold argument1 ?obj)
  (thing ?t name ?obj location ?place on-top-of ceiling weight light)
  (not (thing ?x name ladder location ?place))
  (not (goal-is-to ?y action move argument1 ladder argument2 ?place))
  =>
  (assert (goal-is-to ?y action move argument1 ladder argument2 ?place)))

(climb-ladder-to-hold 0
  (goal-is-to ?g action hold argument1 ?obj)
  (thing ?t name ?obj location ?place on-top-of ceiling weight light)
  (thing ?t2 name ladder location ?place on-top-of floor)
  (monkey ?m on-top-of (not ladder))
  (not (goal-is-to ?x action on argument1 ladder))
  =>
  (assert (goal-is-to ?x action on argument1 ladder)))

(grab-object-from-ladder 0 
  (goal-is-to ?goal action hold argument1 ?name)
  (thing ?thing name ?name location ?place on-top-of ceiling weight light)
  (thing ?t2 name ladder location ?place)
  (monkey ?monkey location ?place on-top-of ladder holding blank)
  =>
  (println (str "Monkey grabs the " ?name "." ))
  (modify (thing ?thing location held on-top-of held))
  (modify (monkey ?monkey holding ?name))
  (retract ?goal))

(climb-to-hold 0 
  (goal-is-to ?g action hold argument1 ?obj)
  (thing ?t name ?obj location ?place on-top-of ?on weight light)
  (?on != ceiling)
  (monkey ?m location ?place on-top-of (not ?on))
  (not (goal-is-to ?x action on argument1 ?on))
  =>
  (assert (goal-is-to ?x action on argument1 ?on)))

(walk-to-hold 0
  (goal-is-to ?g action hold argument1 ?obj)
  (thing ?t name ?obj location ?place on-top-of (not ceiling) weight light)
  (monkey ?m location (not ?place))
  (not (goal-is-to ?x action walk-to argument1 ?place))
  =>
  (assert (goal-is-to ?x action walk-to argument1 ?place)))

(drop-to-hold 0
  (goal-is-to ?g action hold argument1 ?obj)
  (thing ?t name ?obj location ?place on-top-of ?on weight light)
  (monkey ?m location ?place on-top-of ?on holding (not blank))
  (not (goal-is-to ?x action hold argument1 blank))
  =>
  (assert (goal-is-to ?x action hold argument1 blank)))

(grab-object 0 
  (goal-is-to ?goal action hold argument1 ?name)
  (thing ?thing name ?name location ?place on-top-of ?on weight light)
  (monkey ?monkey location ?place on-top-of ?on holding blank)
  =>
  (println (str "Monkey grabs the " ?name "." ))
  (modify (thing ?thing location held on-top-of held))
  (modify (monkey ?monkey holding ?name))
  (retract ?goal))

(drop-object 0  
  (goal-is-to ?goal action hold argument1 blank)
  (monkey ?monkey location ?place on-top-of ?on holding ?name)
  (?name != blank)
  (thing ?thing name ?name)
  =>
  (println (str "Monkey drops the " ?name "." ))
  (modify (monkey ?monkey holding blank))
  (modify (thing ?thing location ?place on-top-of ?on))
  (retract ?goal))

(unlock-chest-to-move-object 0 
  (goal-is-to ?g action move argument1 ?obj argument2 ?place)
  (chest ?c name ?chest contents ?obj)
  (not (goal-is-to ?x action unlock argument1 ?chest))
  =>
  (assert (goal-is-to ?x action unlock argument1 ?chest)))

(hold-object-to-move 0  
  (goal-is-to ?g action move argument1 ?obj argument2 ?place)
  (thing ?t name ?obj location (not ?place) weight light)
  (monkey ?m holding (not ?obj))
  (not (goal-is-to ?x action hold argument1 ?obj))
  =>
  (assert (goal-is-to ?x action hold argument1 ?obj)))

(move-object-to-place 0 
  (goal-is-to ?g action move argument1 ?obj argument2 ?place)
  (monkey ?m location (not ?place) holding ?obj)
  (not (goal-is-to ?x action walk-to argument1 ?place))
  =>
  (assert (goal-is-to ?x action walk-to argument1 ?place)))

(drop-object-once-moved 0 
  (goal-is-to ?goal action move argument1 ?name argument2 ?place)
  (monkey ?monkey location ?place holding ?obj)
  (thing ?thing name ?name weight light)
  =>
  (println (str "Monkey drops the " ?name "." ))
  (modify (monkey ?monkey holding blank))
  (modify (thing ?thing location ?place on-top-of floor))
  (retract ?goal))

(already-moved-object 0
  (goal-is-to ?goal action move argument1 ?obj argument2 ?place)
  (thing ?t name ?obj location ?place)
  =>
  (retract ?goal))

(already-at-place 0 
  (goal-is-to ?goal action walk-to argument1 ?place)
  (monkey ?m location ?place)
  =>
  (retract ?goal))

(get-on-floor-to-walk 0
  (goal-is-to ?g action walk-to argument1 ?place)
  (monkey ?m location (not ?place) on-top-of (not floor))
  (not (goal-is-to ?x action on argument1 floor))
  =>
  (assert (goal-is-to ?x action on argument1 floor)))

(walk-holding-nothing 0
  (goal-is-to ?goal action walk-to argument1 ?place)
  (monkey ?monkey location (not ?place) on-top-of floor holding blank)
  =>
  (println (str "Monkey walks to " ?place "." ))
  (modify (monkey ?monkey location ?place))
  (retract ?goal))

(walk-holding-object 0
  (goal-is-to ?goal action walk-to argument1 ?place)
  (monkey ?monkey location (not ?place) on-top-of floor holding ?obj)
  (?obj != blank)
  =>
  (println (str "Monkey walks to " ?place " holding the " ?obj "." ))
  (modify (monkey ?monkey location ?place))
  (retract ?goal))

(jump-onto-floor 0 
  (goal-is-to ?goal action on argument1 floor)
  (monkey ?monkey on-top-of ?on)
  (?on != floor)
  =>
  (println (str "Monkey jumps off the " ?on " onto the floor." ))
  (modify (monkey ?monkey on-top-of floor))
  (retract ?goal))

(walk-to-place-to-climb 0 
  (goal-is-to ?g action on argument1 ?obj)
  (thing ?t name ?obj location ?place)
  (monkey ?m location (not ?place))
  (not (goal-is-to ?x action walk-to argument1 ?place))
  =>
  (assert (goal-is-to ?x action walk-to argument1 ?place)))

(drop-to-climb 0 
  (goal-is-to ?g action on argument1 ?obj)
  (thing ?t name ?obj location ?place)
  (monkey ?m location ?place holding (not blank))
  (not (goal-is-to ?x action hold argument1 blank))
  =>
  (assert (goal-is-to ?x action hold argument1 blank)))

(climb-indirectly 0 
  (goal-is-to ?g action on argument1 ?obj)
  (thing ?t name ?obj location ?place on-top-of ?on)
  (monkey ?m location ?place on-top-of ?on2 holding blank)
  (?on2 != ?on)
  (?on2 != ?obj)
  (not (goal-is-to ?x action on argument1 ?on))
  =>
  (assert (goal-is-to ?x action on argument1 ?on)))

(climb-directly 0  
  (goal-is-to ?goal action on argument1 ?obj)
  (thing ?t name ?obj location ?place on-top-of ?on)
  (monkey ?monkey location ?place on-top-of ?on holding blank)
  =>
  (println (str "Monkey climbs onto the " ?obj "." ))
  (modify (monkey ?monkey on-top-of ?obj))
  (retract ?goal))

(already-on-object 0
  (goal-is-to ?goal action on argument1 ?obj)
  (monkey ?m on-top-of ?obj)
  =>
  (retract ?goal))

(hold-to-eat 0
  (goal-is-to ?g action eat argument1 ?obj)
  (monkey ?m holding (not ?obj))
  (not (goal-is-to ?x action hold argument1 ?obj))
  =>
  (assert (goal-is-to ?x action hold argument1 ?obj)))

(satisfy-hunger 0
  (goal-is-to ?goal action eat argument1 ?name)
  (monkey ?monkey holding ?name)
  (thing ?thing name ?name)
  =>
  (println (str "Monkey eats the " ?name "." ))
  (modify (monkey ?monkey holding blank))
  (retract ?goal ?thing))
)
 
(facts
(monkey ?m location t5-7 on-top-of green-couch holding blank)
(thing ?t name green-couch location t5-7 weight heavy)
(thing ?t name red-couch location t2-2 weight heavy)
(thing ?t name big-pillow location t2-2 on-top-of red-couch)
(thing ?t name red-chest location t2-2 on-top-of big-pillow)
(chest ?c name red-chest contents ladder unlocked-by red-key)
(thing ?t name blue-chest location t7-7 on-top-of ceiling)
(chest ?c name blue-chest contents bananas unlocked-by blue-key)
(thing ?t name blue-couch location t8-8 weight heavy)
(thing ?t name green-chest location t8-8 on-top-of ceiling)
(chest ?c name green-chest contents blue-key unlocked-by red-key)
(thing ?t name red-key location t1-3)
(goal-is-to ?g action eat argument1 bananas)
))
