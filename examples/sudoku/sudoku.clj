((templates
  (possible row column value group id)
  (impossible id value rank reason)
  (technique-employed reason rank)
  (technique name rank)
  (size-value size value)
  (iterate-rc row column index)
  (rank value process)
  (unsolved row column)
  (phase value)
  (mode value)
  (size value)
  (print-position a1 a2))
(rules
 (initialize
  0
  (phase value start)
  =>
  (asser phase grid-values)
  (asser size-value size 1 value 1)
  (asser size-value size 2 value 2)
  (asser size-value size 2 value 3)
  (asser size-value size 2 value 4)
  (asser size-value size 3 value 5)
  (asser size-value size 3 value 6)
  (asser size-value size 3 value 7)
  (asser size-value size 3 value 8)
  (asser size-value size 3 value 9)
  (asser size-value size 4 value 10)
  (asser size-value size 4 value 11)
  (asser size-value size 4 value 12)
  (asser size-value size 4 value 13)
  (asser size-value size 4 value 14)
  (asser size-value size 4 value 15)
  (asser size-value size 4 value 16)
  (asser size-value size 5 value 17)
  (asser size-value size 5 value 18)
  (asser size-value size 5 value 19)
  (asser size-value size 5 value 20)
  (asser size-value size 5 value 21)
  (asser size-value size 5 value 22)
  (asser size-value size 5 value 23)
  (asser size-value size 5 value 24)
  (asser size-value size 5 value 25))

 (stress-test1
  10
  (phase value match)
  (mode value stress-test)
  (rank value ?last
              (not-exists rank value ?p))
  (technique rank ?next
             ((< ?last ?next)
              (not-exists technique rank ?q)))
  =>
  (asser rank value ?next process yes))

 (stress-test2
  10
  (phase value match)
  (mode value stress-test)
  (rank value ?last)
  (rank value ?p
              (<= ?p ?last))
  (technique rank ?next
             ((< ?last ?next)
              (not-exists technique rank ?q)))
  =>
  (asser rank value ?next process yes))

 (stress-test3
  10
  (phase value match)
  (mode value stress-test)
  (rank value ?last
              (not-exists rank value ?p))
  (technique rank ?q)
  (technique rank ?next
             ((< ?last ?next)
              [(<= ?q ?last) (<= ?next ?q)]))
  =>
  (asser rank value ?next process yes))

 (stress-test4
  10
  (phase value match)
  (mode value stress-test)
  (rank value ?last)
  (rank value ?p
              (<= ?p ?last))
  (technique rank ?q)
  (technique rank ?next
             ((< ?last ?next)
              [(<= ?q ?last) (<= ?next ?q)]))
  =>
  (asser rank value ?next process yes))

 (enable-techniques
  10
  (phase match)
  (size value ?s
        ((not-exists possible value any)
         (not-exists rank value ?r)))
  =>
  (asser rank value 1 process yes))

 (expand-any-start1
  10
  (phase value expand-any)
  (possible row ?r column ?c value any id ?id
            (not-exists possible value any id ?id2))
  =>
  (asser iterate-rc row ?r column ?c index 1))

 (expand-any-start2
  10
  (phase value expand-any)
  (possible row ?r column ?c value any id ?id)
  (possible value any id ?id2
            (>= ?id2 ?id))
  =>
  (asser iterate-rc row ?r column ?c index 1))

 (expand-any1
  10
  (phase value expand-any)
  (possible row ?r column ?c value any group ?g id ?id
            (not-exists possible value any id ?id2))
  (size value ?s)
  ?f (iterate-rc row ?r column ?c index ?v)
  (size-value size ?as value ?v
              ((<= ?as ?s)
               (not-exists possible row ?r column ?c value ?v)))
  =>
  (asser possible row ?r column ?c value ?v group ?g id ?id)
  (modify ?f index (+ ?v 1)))

 (expand-any2
  10
  (phase value expand-any)
  (possible row ?r column ?c value any group ?g id ?id)
  (possible value any id ?id2
            (<= ?id ?id2))
  (size value ?s)
  ?f (iterate-rc row ?r column ?c index ?v)
  (size-value size ?as value ?v
              ((<= ?as ?s)
               (not-exists possible row ?r column ?c value ?v)))
  =>
  (asser possible row ?r column ?c value ?v group ?g id ?id)
  (modify ?f index (+ ?v 1)))

 (position-expanded1
  10
  (phase value expand-any)
  ?f1 (possible row ?r column ?c value any)
  (size value ?s)
  ?f2 (iterate-rc row ?r column ?c index ?v
                  (not-exists size-value size ?as value ?v))
  =>
  (asser unsolved row ?r column ?c)
  (retract ?f1 ?f2))

 (position-expanded2
  10
  (phase value expand-any)
  ?f1 (possible row ?r column ?c value any)
  (size value ?s)
  ?f2 (iterate-rc row ?r column ?c index ?v)
  (size-value size ?as value ?v
                  (< ?s ?as))
  =>
  (asser unsolved row ?r column ?c)
  (retract ?f1 ?f2))

 (expand-any-done
  10
  ?f (phase value expand-any
            (not-exists possible value any))
  =>
  (retract ?f)
  (asser phase value initial-output)
  (asser print-position a1 1 a2 1))

 (begin-match
  10
  ?f (phase value initial-output)
  =>
  (retract ?f)
  (asser phase value match))

 (begin-elimination
  -20
  ?f (phase value match)
  (impossible id ?id)
  =>
  (retract ?f)
  (asser phase value elimination))

 (next-rank-unsolved
  -20
  (phase value match
         (not-exists impossible id ?id))
  (rank value ?last)


   (not (rank (value ?p&:(> ?p ?last))))

   (technique (rank ?next&:(> ?next ?last)))

   (not (technique (rank ?p&:(> ?p ?last)&:(< ?p ?next))))

   (exists (unsolved))

   =>

   (assert (rank (value ?next) (process yes))))

;;; **********************
;;; next-rank-not-unsolved
;;; **********************

(defrule next-rank-not-unsolved

   (declare (salience -20))

   (phase match)

   (not (impossible))

   (rank (value ?last))

   (not (rank (value ?p&:(> ?p ?last))))

   (technique (rank ?next&:(> ?next ?last)))

   (not (technique (rank ?p&:(> ?p ?last)&:(< ?p ?next))))

   (not (unsolved))

   =>

   (assert (rank (value ?next) (process no))))

;;; ************
;;; begin-output
;;; ************

(defrule begin-output

   (declare (salience -20))

   ?f <- (phase match)

   (not (impossible))

   (rank (value ?last))

   (not (rank (value ?p&:(> ?p ?last))))

   (not (technique (rank ?next&:(> ?next ?last))))

   =>

   (retract ?f)

   (assert (phase final-output))
   (assert (print-position 1 1)))














