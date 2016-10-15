;; Therapist's expert system example for healing hypertension
;;
;; In this example used knowledge from a document:
;; 2014 Evidence-Based Guideline for the Management of High Blood Pressure in Adults
;; (http://jama.jamanetwork.com/article.aspx?articleid=1791497)
;;
;; The idea of this example was inspired by screencast of Ryan Brush on StrangeLoop 2014:
;; (https://www.youtube.com/watch?v=Z6oVuYmRgkk)
;;
;; Copyright © 2015 Ruslan Sorokin.
((templates
  (BloodPressure
   person-id systolic diastolic date)

  (PersonalData
   person-id name age race)

  (CoexistingDiseases
   person-id diabetes chronic-kidney-disease)

  (CurrentDate
   date)

  (FactStore
   path)

  (WorkingDay
   work)

  (Reception
   patient-name person-id action)

  (Treatment
   person-id date goal stage)

  (Medication
   person-id date ttd ccb acei-arb other)

  (Analyse
   task query params result))

 (rules
  (load-facts
   0
   (FactStore path ?p)
   =>
   (run-loaded-facts ?p)
   (asser CurrentDate date (h/today))
   (asser PersonalData person-id 0 name ""))

  (work-begin-or-end
   0
   (CurrentDate date ?d)
   (not WorkingDay work ?w)
   =>
   (println (str "\nToday: " ?d ".."))
   (asser WorkingDay work (h/ask2 "What will do?" '[reception analyse end])))

  ;; RECEPTION RULES

  (reception-begin-or-end
   0
   (FactStore path ?p)
   ?wd (WorkingDay work reception)
   (not Reception person-id ?pid)
   =>
   (let [answer (h/ask1 "\nWhat is a name of a next patient? (no-patients)")]
     (if (= answer 'no-patients)
       (do (retract ?wd)
         (println "\nReception is over."))
       (asser Reception patient-name answer person-id -1))))

  (existing-patient
   ;; Examination of the existing patient
   0
   (CurrentDate date ?cdt)
   ?rn (Reception patient-name ?pnm
                  person-id -1)
   (PersonalData name ?pnm
                 age ?age
                 person-id ?pid
                 race ?race)
   (BloodPressure person-id ?pid
                  systolic ?p-sys
                  diastolic ?p-dis
                  date ?pdt
                  (h/before ?pdt ?cdt))
   (not BloodPressure
        person-id ?pid
        date ?adt
        ((not= ?adt ?cdt)
         (h/after ?adt ?pdt)))
   =>
   (println (str "\nPatient: " ?pnm ", age: " ?age ", race: " ?race))
   (println (str "Last blood pressure values: systolic: " ?p-sys ", diastolic: " ?p-dis ", date: " ?pdt))
   (let [[?c-sys ?c-dis] (h/ask-bp-values ?pnm)]
     (asser BloodPressure
            person-id ?pid
            systolic ?c-sys
            diastolic ?c-dis
            date ?cdt))
   (modify ?rn
           person-id ?pid
           action BP-COMPARE))

  (new-patient
   ;; Registration of a new patient
   0
   (CurrentDate date ?cdt)
   ?rn (Reception patient-name ?pnm
                  person-id -1)
   (not PersonalData name ?pnm)
   (PersonalData person-id ?max)
   (not PersonalData person-id ?id
        (> ?id ?max))
   =>
   (println "\nNew patient: " ?pnm)
   (let [qmap (h/ask-map {:age (h/ask1 (str "Please, " ?pnm ", enter enter your age in years."))
                          :rce (h/ask2 "To what race are you belong?" '[black nonblack])
                          :ckd (h/yes-or-no "Have you chronic kidney disease?")
                          :dbt (h/yes-or-no "Have you diabetes?")
                          :bpv (h/ask-bp-values)}
                         "Is this correct data: ")
         ?pid (inc ?max)]
     (asser PersonalData
            person-id ?pid
            name ?pnm
            age (qmap :age)
            race (qmap :rce))
     (asser CoexistingDiseases
            person-id ?pid
            diabetes (qmap :dbt)
            chronic-kidney-disease (qmap :ckd))
     (asser BloodPressure
            person-id ?pid
            systolic (first (qmap :bpv))
            diastolic (second (qmap :bpv))
            date ?cdt)
     (modify ?rn
             person-id ?pid
             action TREATMENT)))

  (current-bp-compare
   ;; Compare current blood pressure with previous one
   0
   (CurrentDate date ?cdt)
   ?rn (Reception person-id ?pid
                  patient-name ?name
                  action BP-COMPARE)
   (BloodPressure person-id ?pid
                  systolic ?c-sys
                  diastolic ?c-dis
                  date ?cdt)
   (BloodPressure person-id ?pid
                  date ?pdt
                  systolic ?p-sys
                  diastolic ?p-dis
        (h/before ?pdt ?cdt))
   (not BloodPressure
        person-id ?pid
        date ?adt
        ((h/after ?adt ?pdt)
         (not= ?adt ?cdt)))
   =>
   (cond
    (and (< ?c-sys ?p-sys) (< ?c-dis ?p-dis))
      (println (str "Perfect, " ?name  "!"))
    (or (< ?c-sys ?p-sys) (< ?c-dis ?p-dis))
      (println (str "Good, " ?name "."))
    (and (> ?c-sys ?p-sys) (> ?c-dis ?p-dis))
      (println (str "Hm.., " ?name ".."))
    (or (> ?c-sys ?p-sys) (> ?c-dis ?p-dis))
      (println (str "So-so, " ?name "."))
    :else
      (println (str "Not so bad, " ?name ".")))
   (modify ?rn action RETROSPECT))

  (continuation-or-decision
   0
   ;; If month passed then go to decision
   (CurrentDate date ?cdt)
   ?rn (Reception action RETROSPECT
                  person-id ?pid)
   (Treatment person-id ?pid
              date ?tdt
              stage ?stg)
   =>
   (if (h/month-passed? ?tdt ?cdt)
     (do
       (println "\n" ?stg " month of the treatment is over! ")
       (modify ?rn action DECISION))
     (do
       (println (str "\nContinue prescribed medication.
                     Waiting for you after " (h/month-after ?tdt) ", if all is well.
                     Good bye."))
       (retract ?rn))))

  (Recommendation-1
   ;; Initial treatment for old patients with high blood pressure
   0
   (CurrentDate date ?cdt)
   (Reception action TREATMENT
              person-id ?pid)
   (PersonalData person-id ?pid
                 age ?age
                 (>= ?age 60))
   (BloodPressure person-id ?pid
                  date ?cdt
                  systolic ?sys
                  diastolic ?dis
                  [(>= ?sys 150) (>= ?dis 90)])
   ;; initial pharmacologic treatment
   (not Treatment person-id ?pid)
   =>
   (println "\n         R e c o m m e n d a t i o n - 1")
   (println "\nStrong Recommendation – Grade A:
            initiate pharmacologic treatment to lower blood pressure
            at systolic blood pressure (SBP) ≥150 mm Hg
            or diastolic blood pressure (DBP) ≥90 mm Hg
            and treat to a goal SBP <150 mm Hg and goal DBP <90 mm Hg.")
   (asser Treatment
          person-id ?pid
          date ?cdt
          goal SBP<150-DBP<90
          stage 1))

  (Corollary-Recommendation
   0
   (CurrentDate date ?cdt)
   (Reception action DECISION
              person-id ?pid)
   (PersonalData person-id ?pid
                 age ?age
                 (>= ?age 60))
   (BloodPressure person-id ?pid
                  date ?cdt
                  systolic ?sys
                  ((< ?sys 140)
                   (h/yes-or-no "Is your treatment well tolerated?")))
   ?tt (Treatment person-id ?pid)
   =>
   (println "\n         C o r o l l a r y  R e c o m m e n d a t i o n")
   (println (str "\nContinue prescribed medication.
                 Waiting for you after a month, if all is well.
                 Good bye."))
   (retract ?rn))

  (Recommendation-2-3
   ;; Initial treatment for non old patients with high blood pressure
   0
   (CurrentDate date ?cdt)
   (Reception action TREATMENT
              person-id ?pid)
   (PersonalData person-id ?pid
                 age ?age
                 (< ?age 60))
   (BloodPressure person-id ?pid
                  date ?cdt
                  systolic ?sys
                  diastolic ?dis
                  [(>= ?sys 140) (>= ?dis 90)])
   ;; initial pharmacologic treatment
   (not Treatment person-id ?pid)
   =>
   (println "\n         R e c o m m e n d a t i o n - 2,3")
   (println "\nStrong Recommendation – Grade A:
            initiate pharmacologic treatment to lower blood pressure
            at systolic blood pressure (SBP) ≥140 mm Hg
            or diastolic blood pressure (DBP) ≥90 mm Hg
            and treat to a goal SBP <140 mm Hg and goal DBP <90 mm Hg.")
   (asser Treatment
          person-id ?pid
          date ?cdt
          goal SBP<140-DBP<90
          stage 1))

  (Recommendation-4
   ;; Initial treatment for chronic kidney disease patients
   0
   (CurrentDate date ?cdt)
   (Reception action TREATMENT
              person-id ?pid)
   (PersonalData person-id ?pid
                 age ?age
                 (>= ?age 18))
   (CoexistingDiseases person-id ?pid
                       chronic-kidney-disease yes)
   (BloodPressure person-id ?pid
                  date ?cdt
                  systolic ?sys
                  diastolic ?dis
                  [(>= ?sys 140) (>= ?dis 90)])
   ;; initial pharmacologic treatment
   (not Treatment person-id ?pid)
   =>
   (println "\n         R e c o m m e n d a t i o n - 4")
   (println "\nExpert Opinion – Grade E:
            initiate pharmacologic treatment to lower blood pressure
            at systolic blood pressure (SBP) ≥140 mm Hg
            or diastolic blood pressure (DBP) ≥90 mm Hg
            and treat to a goal SBP <140 mm Hg and goal DBP <90 mm Hg.")
   (asser Treatment
          person-id ?pid
          date ?cdt
          goal SBP<140-DBP<90
          stage 1))

  (Recommendation-5
   ;; Initial treatment for diabetes patients
   0
   (CurrentDate date ?cdt)
   (Reception action TREATMENT
              person-id ?pid)
   (PersonalData person-id ?pid
                 age ?age
                 (>= ?age 18))
   (CoexistingDiseases person-id ?pid
                       diabetes yes)
   (BloodPressure person-id ?pid
                  date ?cdt
                  systolic ?sys
                  diastolic ?dis
                  [(>= ?sys 140) (>= ?dis 90)])
   ;; initial pharmacologic treatment
   (not Treatment person-id ?pid)
   =>
   (println "\n         R e c o m m e n d a t i o n - 5")
   (println "\nExpert Opinion – Grade E:
            initiate pharmacologic treatment to lower blood pressure
            at systolic blood pressure (SBP) ≥140 mm Hg
            or diastolic blood pressure (DBP) ≥90 mm Hg
            and treat to a goal SBP <140 mm Hg and goal DBP <90 mm Hg.")
   (asser Treatment
          person-id ?pid
          date ?cdt
          goal SBP<140-DBP<90
          stage 1))

  (Recommendation-6
   ;; Initial medication for non black patients
   0
   (FactStore path ?p)
   (CurrentDate date ?cdt)
   ?rn (Reception action TREATMENT
                  person-id ?pid
                  patient-name ?name)
   (PersonalData person-id ?pid
                 race nonblack)
   (Treatment person-id ?pid
                  date ?cdt
                  stage 1)
   ;; initial medication
   (not Medication person-id ?pid)
   =>
   (println "\n         R e c o m m e n d a t i o n - 6")
   (println "\nModerate Recommendation – Grade B:
            initial antihypertensive treatment should include
            a thiazide-type diuretic (TTD),
            calcium channel blocker (CCB),
            angiotensin-converting enzyme inhibitor (ACEI), or angiotensin receptor blocker (ARB).")
   (let [?ttd (h/ask1 (str "Doctor, please, enter initial prescription to patient " ?name ":
                      - thiazide-type diuretic (TTD):"))
         ?ccb (h/ask1 "
                      - calcium channel blocker (CCB):")
         ?aab (h/ask1 "
                      - angiotensin-converting enzyme inhibitor (ACEI), or angiotensin receptor blocker (ARB):")]
     (asser Medication
            person-id ?pid
            date ?cdt
            ttd ?ttd
            ccb ?ccb
            acei-arb ?aab)))

  (Recommendation-7
   0
   ;; Initial medication for black patients
   (CurrentDate date ?cdt)
   ?rn (Reception action TREATMENT
                  person-id ?pid
                  patient-name ?name)
   (PersonalData person-id ?pid
                 race black)
   (Treatment person-id ?pid
                  date ?cdt
                  stage 1)
   (CoexistingDiseases person-id ?pid
                       diabetes ?dbt)
   ;; initial medication
   (not Medication person-id ?pid)
   =>
   (println "\n         R e c o m m e n d a t i o n - 7")
   (if ?dbt
     (println "\nWeak Recommendation – Grade C:")
     (println "\nModerate Recommendation – Grade B:"))

   (println "initial antihypertensive treatment should include
            a calcium channel blocker (CCB).")
   (let [?ccb (h/ask1 (str "Doctor, please, enter initial prescription to patient " ?name ":
                      - calcium channel blocker (CCB):"))]
     (asser Medication
            person-id ?pid
            date ?cdt
            ttd -1
            ccb ?ccb
            acei-arb -1)))

  (Recommendation-8
   ;; Additional medication for all chronic kidney disease patients
   0
   (CurrentDate date ?cdt)
   ?rn (Reception action TREATMENT
                  person-id ?pid
                  patient-name ?name)
   (PersonalData person-id ?pid
                 age ?age
                 (>= ?age 18))
   (CoexistingDiseases person-id ?pid
                       chronic-kidney-disease true)
   ?mn (Medication person-id ?pid
                   date ?cdt
                   acei-arb -1)
   =>
   (println "\n         R e c o m m e n d a t i o n - 8")
   (println "\nModerate Recommendation – Grade B:
            antihypertensive treatment should include an ACEI or ARB
            to improve kidney outcomes.
            This applies to all chronic kidney disease patients with hypertension
            regardless of race or diabetes status.")
   (let [?aab (h/ask1 (str "Doctor, please, enter prescription to patient " ?name ":
                           - angiotensin-converting enzyme inhibitor (ACEI), or angiotensin receptor blocker (ARB):"))]
     (modify ?mn acei-arb ?aab)))

  (goal-not-reached
   0
   (CurrentDate date ?cdt)
   ?rn (Reception action DECISION
                  person-id ?pid)
   ?tt (Treatment person-id ?pid
                  goal ?goal
                  stage ?stg)
   (BloodPressure person-id ?pid
                  date ?cdt
                  diastolic ?dis
                  systolic ?sys
                  [((= ?goal SBP<150-DBP<90) [(>= ?sys 150) (>= ?dis 90)])
                   ((= ?goal SBP<140-DBP<90) [(>= ?sys 140) (>= ?dis 90)])])
  =>
  (modify ?rn action GOAL-NOT-REACHED)
  (modify ?tt date ?cdt stage (inc ?stg)))

  (goal-reached
   -1
   ?rn (Reception action DECISION
                  person-id ?pid)
   (PersonalData person-id ?pid
                 name ?name)
   ?tt (Treatment person-id ?pid)
   =>
   (println (str "\nCongratulations," ?name ", goal of the treatment is reached!
                 Watch your health.
                 Good bye."))
   (retract ?rn))

  (Recommendation-9
   ;; Goal BP is not reached within a month of treatment
   0
   (CurrentDate date ?cdt)
   ?rn (Reception action GOAL-NOT-REACHED
                  person-id ?pid
                  patient-name ?name)
   ;; not yet today medication
   (not Medication
        person-id ?pid
        date ?cdt)
   ;; Last medication
   (Medication person-id ?pid
               date ?ldt
               ttd ?ttd
               ccb ?ccb
               acei-arb ?aab
               other ?oth)
   (not Medication person-id ?pid
               date ?adt
        (h/after ?adt ?ldt))
   =>
   (println "\n         R e c o m m e n d a t i o n - 9")
   (println (str "\nCurrent medications for " ?name " are
                 - thiazide-type diuretic - " ?ttd "
                 - calcium channel blocker - " ?ccb "
                 - angiotensin-converting enzyme inhibitor (ACEI), or angiotensin receptor blocker (ARB) - " ?aab "
                 - other - " ?oth))
   (println "\nIncrease the dose of the initial drug or add a second drug from one of these classes.
            Do not use an ACEI and an ARB together.
            If goal BP cannot be reached using only the drugs in these classes because of a contraindication
            or the need to use more than 3 drugs to reach goal BP, antihypertensive drugs from other classes can be used.")
   (let [?ttd (h/ask1 (str "Doctor, please, enter new prescription to patient " ?name ":
                      - thiazide-type diuretic (TTD):"))
         ?ccb (h/ask1 "
                      - calcium channel blocker (CCB):")
         ?aab (h/ask1 "
                      - angiotensin-converting enzyme inhibitor (ACEI), or angiotensin receptor blocker (ARB):")
         ?oth (h/ask1 "
                      - other if needed:")]
     (asser Medication
            person-id ?pid
            date ?cdt
            ttd ?ttd
            ccb ?ccb
            acei-arb ?aab
            other ?oth)))

  (reception-done
   ;; Reception for this patient is ended when medication is defined
   -1
   (FactStore path ?p)
   (CurrentDate date ?cdt)
   ?rn (Reception action ?a
                  person-id ?pid
                  patient-name ?name
                  [(= ?a TREATMENT) (= ?a GOAL-NOT-REACHED)])
   (Medication person-id ?pid
                   date ?cdt)
   =>
   (println (str "\nPlease, " ?name ", take these medications as instructed until the next reception.
                   Waiting for you after a month, if all is well.
                   Good bye." ))
   (retract ?rn))

  ;; ANALYSE RULES

  (analyse-begin-or-end
   0
   (FactStore path ?p)
   ?wd (WorkingDay work analyse)
   (not Analyse task ?tsk)
   =>
   (println "\nAnalitic task list:
            P - Patients,
            D - Diseases,
            B - Bloood Pressure,
            T - Treatment,
            M - Medication.")
   (let [?tsk (h/ask2 "Select task?" '[P D B T M no-tasks])]
     (if (= ?tsk 'no-tasks)
       (do (retract ?wd)
         (println "Analyse is over."))
       (asser Analyse task ?tsk query -1 result -1))))

  (treatment-or-medication-task
   0
   ?tsk (Analyse task ?t result -1
                 [(= ?t T) (= ?t M)])
   =>
   (let [wht (?t '{T Treatment
                   M Medication})
         all (fact-list wht)
         srt (sort-by #(slot-value 'date %) h/after all)
         ids (map #(slot-value 'person-id %) srt)
         pds (mapcat #(facts-with-slot-value 'PersonalData 'person-id = %) ids)
         nms (map #(slot-value 'name %) pds)]
     (modify ?tsk result ?srt)
     (println "\nBloodPressure values:\n")
     (doall (map #(println [(rest %1) %2]) srt nms))))

  (BP-task
   0
   ?tsk (Analyse task B result -1)
   =>
   (let [all (fact-list 'BloodPressure)
         srt (reverse (sort-by #(slot-value 'systolic %)
                      (sort-by #(slot-value 'diastolic %) all)))
         ids (map #(slot-value 'person-id %) srt)
         pds (mapcat #(facts-with-slot-value 'PersonalData 'person-id = %) ids)
         nms (map #(slot-value 'name %) pds)]
     (modify ?tsk result ?srt)
     (println "\nBloodPressure values:\n")
     (doall (map #(println [(rest %1) %2]) srt nms))))

  (patients-task
   0
   ?tsk (Analyse task P query -1)
   =>
   (println "\nPatients task queries:
            - List of all patients - query: [all]
            - Patients of age      - query: [age min max]
            - Patients of race     - query: [race black/nonblack]
            - History of patient   - query: [his name]")
   (let [[?q & ?pp] (h/ask1 "Please, enter patients task query:")]
     (modify ?tsk
             query ?q
             params ?pp)))

  (diseases-task
   0
   ?tsk (Analyse task D query -1)
   =>
   (println "\nDiseases task queries:
            - List of chronic kidney disease patients - query: [ckd]
            - List of diabetes patients               - query: [dbt]")
   (let [[?q & ?pp] (h/ask1 "Please, enter diseases task query:")]
     (modify ?tsk
             query ?q
             params ?pp)))

  (queries-diseases
   0
   ?tsk (Analyse task D query ?q result -1
                 (not= ?q -1))
   =>
   (if-let [dis (?q '{ckd chronic-kidney-disease
                      dbt diabetes})]
     (let [cds (facts-with-slot-value 'CoexistingDiseases dis = true)
           ids (map #(slot-value 'person-id %) cds)
           pds (mapcat #(facts-with-slot-value 'PersonalData 'person-id = %) ids)
           ?ps (sort-by #(slot-value 'name %) pds)]
       (modify ?tsk result ?ps)
       (println "\nPatients with " dis ":\n")
       (doseq [p ?ps]
         (println (rest p))))
     (do
       (println (str "Unknown query: " ?q))
       (modify ?tsk result 0))))

  (query-patients-all
   0
   ?tsk (Analyse task P query all params ?pp result -1)
   =>
   (let [all (fact-list 'PersonalData)
         ?ap (sort-by #(slot-value 'name %) all)]
     (modify ?tsk result ?ap)
     (println "\nAll patients:\n")
     (doseq [p ?ap]
       (println (rest p)))))

  (query-patients-of-age
   0
   ?tsk (Analyse task P query age params ?pp result -1)
   =>
   (let [[min max] ?pp
         pts (facts-with-slot-value
               'PersonalData
               'age
               #(and (number? %1) (<= (first %2) %1 (second %2)))
               ?pp)
         ?srt (sort-by #(slot-value 'name %) pts)]
     (modify ?tsk result ?srt)
     (println (str "\nPatients from " min " to " max " years old:\n"))
     (doseq [p ?srt]
       (println (rest p)))))

  (query-patients-of-race
   0
   ?tsk (Analyse task P query race params ?pp result -1)
   =>
   (let [[r] ?pp
         pts (facts-with-slot-value 'PersonalData 'race = r)
         ?srt (sort-by #(slot-value 'name %) pts)]
     (modify ?tsk result ?srt)
     (println (str "\nPatients of race" r ":\n"))
     (doseq [p ?srt]
       (println (rest p)))))

  (query-history-of-patient
   0
   ?tsk (Analyse task P query his params ?pp result -1)
   =>
   (let [[name] ?pp
         pdt (facts-with-slot-value 'PersonalData 'name = name)]
     (if (seq pdt)
       (let [pid (slot-value 'person-id (first pdt))
             ?rt (facts-with-slot-value 'person-id = pid)]
         (modify ?tsk result ?rt)
         (println (str "\nPatient " name " data:\n"))
         (doseq [d ?rt]
           (println d)))
       (do
         (println (str "\nPatient " name " not found!"))
         (modify ?tsk result 0)))))

  (task-accomplished
   -1
   ?tsk (Analyse result ?r
                 (not= ?r -1))
   =>
   (retract ?tsk))

  ;; THE END

  (the-end
   0
   ?fs (FactStore path ?p)
   ?cd (CurrentDate date ?d)
   ?wd (WorkingDay work end)
   =>
   (retract ?wd ?cd)
   (save-facts ?p)
   (retract ?fs)
   (println "The end.")))

 (functions
  (ns h)

  (defn cal-to-str [c]
    ;; calendar-to-string
    (str (.get c (java.util.GregorianCalendar/DATE)) "."
         (inc (.get c (java.util.GregorianCalendar/MONTH))) "."
         (.get c (java.util.GregorianCalendar/YEAR))))

  (defn today []
    (cal-to-str (java.util.GregorianCalendar/getInstance)))

  (defn calendar [s]
    (let [l (seq (.split s "\\."))
          [d m y] (map read-string l)
          c (java.util.GregorianCalendar/getInstance)]
      (.set c y (dec m) d)
      c))

  (defn after [s1 s2]
    (.after (calendar s1) (calendar s2)))

  (defn before [s1 s2]
    (.before (calendar s1) (calendar s2)))

  (defn month-passed? [s1 s2]
    (let [c1 (calendar s1)]
      (.roll c1 (java.util.GregorianCalendar/MONTH) true)
      (.before c1 (calendar s2))))

  (defn month-after [s1]
    (let [c1 (calendar s1)]
      (.roll c1 (java.util.GregorianCalendar/MONTH) true)
      (cal-to-str c1)))

  (defn ask-bp-values
    ([]
     (println (str "What are blood pressure values now? [systolic diastolic]"))
     (if-let [[s d :as bp] (read)]
       (if (and (vector? bp)
                (= (count bp) 2)
                (number? s)
                (number? d)
                (< 0 s 300)
                (< 0 d 300))
         bp
         (do (println (str "Wrong values: " bp))
           (ask-bp-values)))))
    ([?name]
     (println (str "\nWell, " ?name ", let's measure your current blood pressure.."))
     (ask-bp-values)))

  (defn ask1 [q]
    (println q)
    (read))

  (defn ask2 [q ops]
    (println (str q " " ops))
    (if-let [a (read)]
      (if (or (empty? ops) (some #{a} ops))
        a
        (ask2 q ops))))

  (defn yes-or-no [q]
    (if (= (ask2 q '[yes no]) 'yes)
      true
      false))

  (defmacro ask-map [qmap text]
    ;; example call: (h/ask-map {'x (h/ask1 "x value?") 'y (h/ask1 "y value?")} "Correct ")
    `(loop [qum# nil ans# nil]
      (if (and (some? qum#) ans#)
        qum#
        (let [qum2# ~qmap
              ans2# (h/yes-or-no (str ~text qum2# "?"))]
          (recur qum2# ans2#))))))

 (facts
  (FactStore path "examples/hypertension-fact-base.clj")))







