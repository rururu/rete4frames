((Medication ttd TTD1-1x3 acei-arb ACEI1-2x2 date "16.4.2014" other ? person-id 1 ccb CCB1-1x1)
(Medication ttd TTD0-1x1 acei-arb ARB2-3x1 date "17.2.2015" other ? person-id 2 ccb CCB4-1x1)
(Medication ttd TTD7-3x4 acei-arb ACEI0-2x2 date "18.2.2015" other ? person-id 3 ccb CCB6-1x2)
(Medication ttd TTD1-1x3 acei-arb ACEI-2x2 date "17.1.2015" other OTH1-1x1 person-id 1 ccb CCB1-1x1)
(Medication ttd TTD2-2x2 acei-arb - date "18.2.2015" other OTH1-2x2 person-id 1 ccb CCB-1x2)
(Treatment date "17.2.2015" stage 1 person-id 2 goal SBP<140-DBP<90)
(Treatment date "18.2.2015" stage 1 person-id 3 goal SBP<150-DBP<90)
(Treatment date "18.2.2015" stage 3 person-id 1 goal SBP<150-DBP<90)
(PersonalData age 70 person-id 1 race nonblack name "Alice")
(PersonalData age 58 person-id 2 race black name "Sam")
(PersonalData age ? person-id 0 race ? name "")
(PersonalData age 63 person-id 3 race nonblack name "Tom")
(PersonalData age :? person-id 0 race :? name "")
(FactStore path "examples/hypertension-fact-base.clj")
(CoexistingDiseases person-id 2 chronic-kidney-disease true diabetes false)
(CoexistingDiseases person-id 3 chronic-kidney-disease false diabetes true)
(BloodPressure systolic 155 date "16.4.2014" diastolic 98 person-id 1)
(BloodPressure systolic 148 date "17.2.2015" diastolic 87 person-id 2)
(BloodPressure systolic 160 date "18.2.2015" diastolic 94 person-id 3)
(BloodPressure systolic 155 date "17.1.2015" diastolic 90 person-id 1)
(BloodPressure systolic 153 date "18.2.2015" diastolic 89 person-id 1)
(BloodPressure systolic 132 date "19.2.2015" diastolic 78 person-id 3)
(BloodPressure systolic 140 date "19.2.2015" diastolic 90 person-id 1)
(BloodPressure systolic 151 date "25.2.2015" diastolic 101 person-id 1)
(BloodPressure systolic 141 date "25.2.2015" diastolic 88 person-id 3))