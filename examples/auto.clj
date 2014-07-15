((templates
  (engine
   working-state
   spark-state
   rotation-state
   symptom)

  (battery
   charge-state)

  (repair
   advice))
 (rules
  (normal-engine-state-conclusions
   10
   (engine working-state normal)
   =>
   (asser repair advice "No repair needed.")
   (asser engine spark-state normal)
   (asser battery charge-state charged)
   (asser engine rotation-state rotates))

  (unsatisfactory-engine-state-conclusions
   10
   (engine working-state unsatisfactory)
   =>
   (asser battery charge-state charged)
   (asser engine rotation-state rotates))

  (determine-engine-state
   0
   (??
    ((not-exists engine working-state ?ws)
     (not-exists repair advice ?a)))
   =>
   (if (a/yes-or-no "Does the engine start (yes/no)?")
     (if (a/yes-or-no "Does the engine run normally (yes/no)?")
       (asser engine working-state normal)
       (asser engine working-state unsatisfactory))
     (asser engine working-state does-not-start)))

  (determine-rotation-state
   0
   (engine working-state does-not-start
           ((not-exists engine rotation-state ?rs)
            (not-exists repair advice ?a)))
   =>
   (if (a/yes-or-no "Does the engine rotate (yes/no)?")
     (do
       (asser engine rotation-state rotates)
       (asser engine spark-state irregular-spark))
     (do
       (asser engine rotation-state does-not-rotate)
       (asser engine spark-state does-not-spark))))))
