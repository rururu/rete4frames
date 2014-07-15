
;;;======================================================
;;;   Automotive Expert System
;;;
;;;     This expert system diagnoses some simple
;;;     problems with a car.
;;;
;;;     CLIPS Version 6.0 Example
;;;
;;;     To execute, merely load, reset and run.
;;;======================================================

;;****************
;;* DEFFUNCTIONS *
;;****************

(deffunction ask-question (?question $?allowed-values)
   (printout t ?question)
   (bind ?answer (read))
   (if (lexemep ?answer) 
       then (bind ?answer (lowcase ?answer)))
   (while (not (member ?answer ?allowed-values)) do
      (printout t ?question)
      (bind ?answer (read))
      (if (lexemep ?answer) 
          then (bind ?answer (lowcase ?answer))))
   ?answer)

(deffunction yes-or-no-p (?question)
   (bind ?response (ask-question ?question yes no y n))
   (if (or (eq ?response yes) (eq ?response y))
       then TRUE 
       else FALSE))

;;;**********************
;;;* ENGINE STATE RULES *
;;;**********************

(defrule normal-engine-state-conclusions ""
   (declare (salience 10))
   (working-state engine normal)
   =>
   (assert (repair "No repair needed."))
   (assert (spark-state engine normal))
   (assert (charge-state battery charged))
   (assert (rotation-state engine rotates)))

(defrule unsatisfactory-engine-state-conclusions ""
   (declare (salience 10))
   (working-state engine unsatisfactory)
   =>
   (assert (charge-state battery charged))
   (assert (rotation-state engine rotates)))

;;;***************
;;;* QUERY RULES *
;;;***************

(defrule determine-engine-state ""
   (not (working-state engine ?))
   (not (repair ?))
   =>
   (if (yes-or-no-p "Does the engine start (yes/no)? ") 
       then 
       (if (yes-or-no-p "Does the engine run normally (yes/no)? ")
           then (assert (working-state engine normal))
           else (assert (working-state engine unsatisfactory)))
       else 
       (assert (working-state engine does-not-start))))

(defrule determine-rotation-state ""
   (working-state engine does-not-start)
   (not (rotation-state engine ?))
   (not (repair ?))   
   =>
   (if (yes-or-no-p "Does the engine rotate (yes/no)? ")
       then
       (assert (rotation-state engine rotates))
       (assert (spark-state engine irregular-spark))
       else
       (assert (rotation-state engine does-not-rotate))       
       (assert (spark-state engine does-not-spark))))

(defrule determine-sluggishness ""
   (working-state engine unsatisfactory)
   (not (repair ?))
   =>
   (if (yes-or-no-p "Is the engine sluggish (yes/no)? ")
       then (assert (repair "Clean the fuel line."))))

(defrule determine-misfiring ""
   (working-state engine unsatisfactory)
   (not (repair ?))
   =>
   (if (yes-or-no-p "Does the engine misfire (yes/no)? ")
       then
       (assert (repair "Point gap adjustment."))       
       (assert (spark-state engine irregular-spark)))) 

(defrule determine-knocking ""
   (working-state engine unsatisfactory)
   (not (repair ?))
   =>
   (if (yes-or-no-p "Does the engine knock (yes/no)? ")
       then
       (assert (repair "Timing adjustment."))))

(defrule determine-low-output ""
   (working-state engine unsatisfactory)
   (not (symptom engine low-output | not-low-output))
   (not (repair ?))
   =>
   (if (yes-or-no-p "Is the output of the engine low (yes/no)? ")
       then
       (assert (symptom engine low-output))
       else
       (assert (symptom engine not-low-output))))

(defrule determine-gas-level ""
   (working-state engine does-not-start)
   (rotation-state engine rotates)
   (not (repair ?))
   =>
   (if (not (yes-or-no-p "Does the tank have any gas in it (yes/no)? "))
       then
       (assert (repair "Add gas."))))

(defrule determine-battery-state ""
   (rotation-state engine does-not-rotate)
   (not (charge-state battery ?))
   (not (repair ?))
   =>
   (if (yes-or-no-p "Is the battery charged (yes/no)? ")
       then
       (assert (charge-state battery charged))
       else
       (assert (repair "Charge the battery."))
       (assert (charge-state battery dead))))  

(defrule determine-point-surface-state ""
   (or (and (working-state engine does-not-start)      
            (spark-state engine irregular-spark))
       (symptom engine low-output))
   (not (repair ?))
   =>
   (bind ?response 
      (ask-question "What is the surface state of the points (normal/burned/contaminated)? "
                    normal burned contaminated))
   (if (eq ?response burned) 
       then 
      (assert (repair "Replace the points."))
       else (if (eq ?response contaminated)
                then (assert (repair "Clean the points.")))))

(defrule determine-conductivity-test ""
   (working-state engine does-not-start)      
   (spark-state engine does-not-spark)
   (charge-state battery charged)
   (not (repair ?))
   =>
   (if (yes-or-no-p "Is the conductivity test for the ignition coil positive (yes/no)? ")
       then
       (assert (repair "Repair the distributor lead wire."))
       else
       (assert (repair "Replace the ignition coil."))))

(defrule no-repairs ""
  (declare (salience -10))
  (not (repair ?))
  =>
  (assert (repair "Take your car to a mechanic.")))

;;;****************************
;;;* STARTUP AND REPAIR RULES *
;;;****************************

(defrule system-banner ""
  (declare (salience 10))
  =>
  (printout t crlf crlf)
  (printout t "The Engine Diagnosis Expert System")
  (printout t crlf crlf))

(defrule print-repair ""
  (declare (salience 10))
  (repair ?item)
  =>
  (printout t crlf crlf)
  (printout t "Suggested Repair:")
  (printout t crlf crlf)
  (format t " %s%n%n%n" ?item))

