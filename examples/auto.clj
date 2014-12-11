((templates
  (working-state
   engine)

  (spark-state
   engine)

  (charge-state
   battery)

  (rotation-state
   engine)

  (symptom
   engine)

  (repair
   advice)

  (stage
   value))

 (rules
  (normal-engine-state-conclusions
   8
   (working-state engine normal)
   =>
   (asser repair advice "No repair needed.")
   (asser spark-state engine normal)
   (asser charge-state battery charged)
   (asser rotation-state engine rotates))

  (unsatisfactory-engine-state-conclusions
   8
   (working-state engine unsatisfactory)
   =>
   (asser charge-state battery charged)
   (asser rotation-state engine rotates))

  (determine-engine-state
   8
   (stage value diagnose)
   (not working-state engine ?ws)
   (not repair advice ?a)
   =>
   (if (a/yes-or-no "Does the engine start?")
     (if (a/yes-or-no "Does the engine run normally?")
       (asser working-state engine normal)
       (asser working-state engine unsatisfactory))
     (asser working-state engine does-not-start)))

  (determine-rotation-state
   0
   (working-state engine does-not-start)
   (not rotation-state engine ?rs)
   (not repair advice ?a)
   =>
   (if (a/yes-or-no "Does the engine rotate?")
     (do
       (asser rotation-state engine rotates)
       (asser spark-state engine irregular-spark))
     (do
       (asser rotation-state engine does-not-rotate)
       (asser spark-state engine does-not-spark))))

  (determine-sluggishness
   3
   (working-state engine unsatisfactory)
   (not repair advice ?a)
   =>
   (if (a/yes-or-no "Is the engine sluggish?")
     (asser repair advice "Clean the fuel line.")))

  (determine-misfiring
   2
   (working-state engine unsatisfactory)
   (not repair advice ?a)
   =>
   (if (a/yes-or-no "Does the engine misfire?")
     (do
       (asser repair advice "Point gap adjustment.")
       (asser spark-state engine irregular-spark))))

  (determine-knocking
   1
   (working-state engine unsatisfactory)
   (not repair advice ?a)
   =>
   (if (a/yes-or-no "Does the engine knock?")
     (asser repair advice "Timing adjustment.")))

  (determine-low-output
   0
   (working-state engine unsatisfactory)
   (not symptom engine ?se)
   (not repair advice ?a)
   =>
   (if (a/yes-or-no "Is the output of the engine low?")
     (asser symptom engine low-output)
     (asser symptom engine not-low-output)))

  (determine-gas-level
   0
   (working-state engine does-not-start)
   (rotation-state engine rotates)
   (not repair advice ?a)
   =>
   (if (not (a/yes-or-no "Does the tank have any gas in it?"))
     (asser repair advice "Add gas.")))

  (determine-battery-state
   0
   (rotation-state engine does-not-rotate)
   (not charge-state battery ?cs)
   (not repair advice ?a)
   =>
   (if (a/yes-or-no "Is the battery charged?")
     (asser charge-state battery charged)
     (do
       (asser repair advice "Charge the battery.")
       (asser charge-state battery dead))))

  (determine-point-surface-state-1
   8
   (working-state engine does-not-start)
   (spark-state engine irregular-spark)
   (not repair advice ?a)
   =>
   (condp = (a/ask "What is the surface state of the points?" '[normal burned contaminated])
     'burned (asser repair advice "Replace the points.")
     'contaminated (asser repair advice "Clean the points.")
     'normal))

  (determine-point-surface-state-2
   0
   (symptom engine low-output)
   (not repair advice ?a)
   =>
   (condp = (a/ask "What is the surface state of the points?" '[normal burned contaminated])
     'burned (asser repair advice "Replace the points.")
     'contaminated (asser repair advice "Clean the points.")
     'normal))

  (determine-conductivity-test
   0
   (working-state engine does-not-start)
   (spark-state engine does-not-spark)
   (charge-state battery charged)
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
