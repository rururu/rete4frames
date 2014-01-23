((templates
(nationality value)
(color value)
(pet value)
(drink value)
(smokes value)
(avh a v h)
(solution
   nationality
   house
   color
   pet
   drink
   smokes)
)
(rules
(find-solution-1 0
  (avh a nationality v englishman h ?n1)
  (avh a color v red h ?c1 (= ?c1 ?n1))
  (avh a nationality v spaniard h ?n2 (not= ?n2 ?n1))
  (avh a pet v dog h ?p1 (= ?p1 ?n2))
  (avh a color v ivory h ?c2 (not= ?c2 ?c1))
  (avh a color v green h ?c3 ((not= ?c3 ?c1) (= ?c3 (+ ?c2 1))))
  (avh a drink v coffee h ?d1 (= ?d1 ?c3))
  (avh a drink v milk h ?d2 ((not= ?d2 ?d1) (= ?d2 3)))
  (avh a smokes v old-golds h ?s1)
  (avh a pet v snails h ?p2 ((not= ?p2 ?p1) (= ?p2 ?s1)))
  (avh a nationality v ukrainian h ?n3
       ((not= ?n3 ?n2) (not= ?n3 ?n1)))
  (avh a drink v tea h ?d3
       ((not= ?d3 ?d2) (not= ?d3 ?d1) (= ?d3 ?n3)))
  (avh a nationality v norwegian h ?n4
       ((not= ?n4 ?n3) (not= ?n4 ?n2) (not= ?n4 ?n1) (= ?n4 1)))
  (avh a smokes v chesterfields h ?s2 (not= ?s2 ?s1))
  (avh a pet v fox h ?p3
       ((not= ?p3 ?p2)
        (not= ?p3 ?p1)
        [(= ?s2 (- ?p3 1)) (= ?s2 (+ ?p3 1))]))
  (avh a smokes v lucky-strikes h ?s3
       ((not= ?s3 ?s2) (not= ?s3 ?s1)))
  (avh a drink v orange-juice h ?d4
       ((not= ?d4 ?d3) (not= ?d4 ?d2) (not= ?d4 ?d1) (= ?d4 ?s3)))
  (avh a nationality v japanese h ?n5
       ((not= ?n5 ?n4) (not= ?n5 ?n3) (not= ?n5 ?n2) (not= ?n5 ?n1)))
  (avh a smokes v parliaments h ?s4
       ((not= ?s4 ?s3) (not= ?s4 ?s2) (not= ?s4 ?s1) (= ?s4 ?n5)))
  (avh a pet v horse h ?p4
       ((not= ?p4 ?p3) (not= ?p4 ?p2) (not= ?p4 ?p1)))
  (avh a smokes v kools h ?s5
       ((not= ?s5 ?s4)
        (not= ?s5 ?s3)
        (not= ?s5 ?s2)
        (not= ?s5 ?s1)
        [(= ?p4 (- ?s5 1)) (= ?p4 (+ ?s5 1))]))
  (avh a color v yellow h ?c4
       ((not= ?c4 ?c3) (not= ?c4 ?c2) (not= ?c4 ?c1) (= ?c4 ?s5)))
  (avh a color v blue h ?c5
       ((not= ?c5 ?c4)
        (not= ?c5 ?c3)
        (not= ?c5 ?c2)
        (not= ?c5 ?c1)
        [(= ?c5 (- ?n4 1)) (= ?c5 (+ ?n4 1))]))
  (avh a drink v water h ?d5
       ((not= ?d5 ?d4) (not= ?d5 ?d3) (not= ?d5 ?d2) (not= ?d5 ?d1)))
  (avh a pet v zebra h ?p5
       ((not= ?p5 ?p4) (not= ?p5 ?p3) (not= ?p5 ?p2) (not= ?p5 ?p1)))
  =>
  (asser solution nationality englishman house ?n1)
  (asser solution color red house ?c1)
  (asser solution nationality spaniard house ?n2)
  (asser solution pet dog house ?p1)
  (asser solution color ivory house ?c2)
  (asser solution color green house ?c3)
  (asser solution drink coffee house ?d1)
  (asser solution drink milk house ?d2)
  (asser solution smokes old-golds house ?s1)
  (asser solution pet snails house ?p2)
  (asser solution nationality ukrainian house ?n3)
  (asser solution drink tea house ?d3)
  (asser solution nationality norwegian house ?n4))

(find-solution-2 0
  (avh a nationality v englishman h ?n1)
  (avh a color v red h ?c1 (= ?c1 ?n1))
  (avh a nationality v spaniard h ?n2 (not= ?n2 ?n1))
  (avh a pet v dog h ?p1 (= ?p1 ?n2))
  (avh a color v ivory h ?c2 (not= ?c2 ?c1))
  (avh a color v green h ?c3 ((not= ?c3 ?c1) (= ?c3 (+ ?c2 1))))
  (avh a drink v coffee h ?d1 (= ?d1 ?c3))
  (avh a drink v milk h ?d2 ((not= ?d2 ?d1) (= ?d2 3)))
  (avh a smokes v old-golds h ?s1)
  (avh a pet v snails h ?p2 ((not= ?p2 ?p1) (= ?p2 ?s1)))
  (avh a nationality v ukrainian h ?n3
       ((not= ?n3 ?n2) (not= ?n3 ?n1)))
  (avh a drink v tea h ?d3
       ((not= ?d3 ?d2) (not= ?d3 ?d1) (= ?d3 ?n3)))
  (avh a nationality v norwegian h ?n4
       ((not= ?n4 ?n3) (not= ?n4 ?n2) (not= ?n4 ?n1) (= ?n4 1)))
  (avh a smokes v chesterfields h ?s2 (not= ?s2 ?s1))
  (avh a pet v fox h ?p3
       ((not= ?p3 ?p2)
        (not= ?p3 ?p1)
        [(= ?s2 (- ?p3 1)) (= ?s2 (+ ?p3 1))]))
  (avh a smokes v lucky-strikes h ?s3
       ((not= ?s3 ?s2) (not= ?s3 ?s1)))
  (avh a drink v orange-juice h ?d4
       ((not= ?d4 ?d3) (not= ?d4 ?d2) (not= ?d4 ?d1) (= ?d4 ?s3)))
  (avh a nationality v japanese h ?n5
       ((not= ?n5 ?n4) (not= ?n5 ?n3) (not= ?n5 ?n2) (not= ?n5 ?n1)))
  (avh a smokes v parliaments h ?s4
       ((not= ?s4 ?s3) (not= ?s4 ?s2) (not= ?s4 ?s1) (= ?s4 ?n5)))
  (avh a pet v horse h ?p4
       ((not= ?p4 ?p3) (not= ?p4 ?p2) (not= ?p4 ?p1)))
  (avh a smokes v kools h ?s5
       ((not= ?s5 ?s4)
        (not= ?s5 ?s3)
        (not= ?s5 ?s2)
        (not= ?s5 ?s1)
        [(= ?p4 (- ?s5 1)) (= ?p4 (+ ?s5 1))]))
  (avh a color v yellow h ?c4
       ((not= ?c4 ?c3) (not= ?c4 ?c2) (not= ?c4 ?c1) (= ?c4 ?s5)))
  (avh a color v blue h ?c5
       ((not= ?c5 ?c4)
        (not= ?c5 ?c3)
        (not= ?c5 ?c2)
        (not= ?c5 ?c1)
        [(= ?c5 (- ?n4 1)) (= ?c5 (+ ?n4 1))]))
  (avh a drink v water h ?d5
       ((not= ?d5 ?d4) (not= ?d5 ?d3) (not= ?d5 ?d2) (not= ?d5 ?d1)))
  (avh a pet v zebra h ?p5
       ((not= ?p5 ?p4) (not= ?p5 ?p3) (not= ?p5 ?p2) (not= ?p5 ?p1)))
  =>
  (asser solution smokes chesterfields house ?s2)
  (asser solution pet fox house ?p3)
  (asser solution smokes lucky-strikes house ?s3)
  (asser solution drink orange-juice house ?d4)
  (asser solution nationality japanese house ?n5)
  (asser solution smokes parliaments house ?s4)
  (asser solution pet horse house ?p4)
  (asser solution smokes kools house ?s5)
  (asser solution color yellow house ?c4)
  (asser solution color blue house ?c5)
  (asser solution drink water house ?d5)
  (asser solution pet zebra house ?p5))

(who-owns-zebra 0
  (solution nationality ?n house ?h (not (nil? ?n)))
  (solution pet zebra house ?h)
  =>
  (println (str ?n " owns the zebra")))

(who-dtinks-water 0
  (solution nationality ?n house ?h (not (nil? ?n)))
  (solution drink water house ?h)
  =>
  (println (str ?n " drinks water")))

(generate-pet-combinations 1
   ?f (pet value ?e)
   =>
   (asser avh a pet v ?e h 1)
   (asser avh a pet v ?e h 2)
   (asser avh a pet v ?e h 3)
   (asser avh a pet v ?e h 4)
   (asser avh a pet v ?e h 5)
   (retract ?f))

(generate-nationality-combinations 2
   ?f (nationality value ?e)
   =>
   (asser avh a nationality v ?e h 1)
   (asser avh a nationality v ?e h 2)
   (asser avh a nationality v ?e h 3)
   (asser avh a nationality v ?e h 4)
   (asser avh a nationality v ?e h 5)
   (retract ?f))

(generate-color-combinations 3
   ?f (color value ?e)
   =>
   (asser avh a color v ?e h 1)
   (asser avh a color v ?e h 2)
   (asser avh a color v ?e h 3)
   (asser avh a color v ?e h 4)
   (asser avh a color v ?e h 5)
   (retract ?f))

(generate-drink-combinations 4
   ?f (drink value ?e)
   =>
   (asser avh a drink v ?e h 1)
   (asser avh a drink v ?e h 2)
   (asser avh a drink v ?e h 3)
   (asser avh a drink v ?e h 4)
   (asser avh a drink v ?e h 5)
   (retract ?f))

(generate-smokes-combinations 5
   ?f (smokes value ?e)
   =>
   (asser avh a smokes v ?e h 1)
   (asser avh a smokes v ?e h 2)
   (asser avh a smokes v ?e h 3)
   (asser avh a smokes v ?e h 4)
   (asser avh a smokes v ?e h 5)
   (retract ?f))
)
(functions
)
(facts
 (color value red)
 (color value green)
 (color value ivory)
 (color value yellow)
 (color value blue)
 (nationality value englishman)
 (nationality value spaniard)
 (nationality value ukrainian)
 (nationality value norwegian)
 (nationality value japanese)
 (pet value dog)
 (pet value snails)
 (pet value fox)
 (pet value horse)
 (pet value zebra)
 (drink value water)
 (drink value coffee)
 (drink value milk)
 (drink value orange-juice)
 (drink value tea)
 (smokes value old-golds)
 (smokes value kools)
 (smokes value chesterfields)
 (smokes value lucky-strikes)
 (smokes value parliaments))
)
