((templates
  (engine
   working-state
   spark-state
   rotation-state
   symptom)

  (battery
   charge-state)

  (repair
   advice)

  (stage
   value))

 (rules
  (normal-engine-state-conclusions
   8
   (engine working-state normal)
   =>
   (asser repair advice "No repair needed.")
   (asser engine spark-state normal)
   (asser battery charge-state charged)
   (asser engine rotation-state rotates))

  (unsatisfactory-engine-state-conclusions
   8
   (engine working-state unsatisfactory)
   =>
   (asser battery charge-state charged)
   (asser engine rotation-state rotates))

  (determine-engine-state
   8
   (stage value diagnose)
   (not engine working-state ?ws)
   (not repair advice ?a)
   =>
   (if (a/yes-or-no "Does the engine start?")
     (if (a/yes-or-no "Does the engine run normally?")
       (asser engine working-state normal)
       (asser engine working-state unsatisfactory))
     (asser engine working-state does-not-start)))

  (determine-rotation-state
   0
   (engine working-state does-not-start)
   (not engine rotation-state ?rs)
   (not repair advice ?a)
   =>
   (if (a/yes-or-no "Does the engine rotate?")
     (do
       (asser engine rotation-state rotates)
       (asser engine spark-state irregular-spark))
     (do
       (asser engine rotation-state does-not-rotate)
       (asser engine spark-state does-not-spark))))

  (determine-sluggishness
   0
   (engine working-state unsatisfactory)
   (not repair advice ?a)
   =>
   (if (a/yes-or-no "Is the engine sluggish?")
     (asser repair advice "Clean the fuel line.")))

  (determine-misfiring
   0
   (engine working-state unsatisfactory)
   (not repair advice ?a)
   =>
   (if (a/yes-or-no "Does the engine misfire?")
     (do
       (asser repair advice "Point gap adjustment.")
       (asser engine spark-state irregular-spark))))

  (determine-knocking
   0
   (engine working-state unsatisfactory)
   (not repair advice ?a)
   =>
   (if (a/yes-or-no "Does the engine knock?")
     (asser repair advice "Timing adjustment.")))

  (determine-low-output-1
   0
   (engine working-state unsatisfactory)
   (not engine symptom low-output)
   (not repair advice ?a)
   =>
   (if (a/yes-or-no "Is the output of the engine low?")
     (asser engine symptom low-output)
     (asser engine symptom not-low-output)))

  (determine-low-output-2
   0
   (engine working-state unsatisfactory)
   (not engine symptom not-low-output)
   (not repair advice ?a)
   =>
   (if (a/yes-or-no "Is the output of the engine low?")
     (asser engine symptom low-output)
     (asser engine symptom not-low-output)))

  (determine-gas-level
   0
   (engine working-state does-not-start)
   (engine rotation-state rotates)
   (not repair advice ?a)
   =>
   (if (not (a/yes-or-no "Does the tank have any gas in it?"))
     (asser repair advice "Add gas.")))

  (determine-battery-state
   0
   (engine rotation-state does-not-rotate)
   (not battery charge-state ?cs)
   (not repair advice ?a)
   =>
   (if (a/yes-or-no "Is the battery charged?")
     (asser battery charge-state charged)
     (do
       (asser repair advice "Charge the battery.")
       (asser battery charge-state dead))))

  (determine-point-surface-state-1
   8
   (engine working-state does-not-start)
   (engine spark-state irregular-spark)
   (not repair advice ?a)
   =>
   (condp = (a/ask "What is the surface state of the points?" '[normal burned contaminated])
     'burned (asser repair advice "Replace the points.")
     'contaminated (asser repair advice "Clean the points.")
     'normal))

  (determine-point-surface-state-2
   0
   (engine symptom low-output)
   (not repair advice ?a)
   =>
   (condp = (a/ask "What is the surface state of the points?" '[normal burned contaminated])
     'burned (asser repair advice "Replace the points.")
     'contaminated (asser repair advice "Clean the points.")
     'normal (asser repair advice "Take your car to a electrician.")))

  (determine-conductivity-test
   0
   (engine working-state does-not-start)
   (engine spark-state does-not-spark)
   (battery charge-state charged)
   (not repair advice ?a)
   =>
   (if (a/yes-or-no "Is the conductivity test for the ignition coil positive?")
     (asser repair advice "Repair the distributor lead wire.")
     (asser repair advice "Replace the ignition coil.")))

  (no-repairs
   -8
   (stage value diagnose)
   (not repair advice ?a)
   =>
   (asser repair advice "Take your car to a mechanic."))

  (system-banner
   0
   ?s (stage value start)
   =>
   (println "The Engine Diagnosis Expert System")
   (println)
   (modify ?s value diagnose))

  (print-repair
   -8
   (repair advice ?a)
   =>
   (println)
   (println (str "Suggested Repair: " ?a))
   (println)))

 (functions
  (ns a)

  (defn ask [q ops]
    (println (str q " " ops))
    (if-let [a (read)]
      (if (some #{a} ops)
        a
        (ask q ops))))

  (defn yes-or-no [q]
    (if (= (ask q '[yes no]) 'yes)
      true
      false)))

 (facts
  (stage value start)))
