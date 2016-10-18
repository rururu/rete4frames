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
   (print-position a1 a2)
   (position-printed a1 a2)
   (position-value-color row column group id value color)
   (chain start-row start-column start-value row column group id value)
   (triple v1 v2 v3)
   (color-pair c1 c2))

 (rules
   (initialize
     0
     ?f (phase value start)
     =>
     (retract ?f)
     (asser phase value expand-any)
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

   (stress-test
     10
     (phase value match)
     (mode value stress-test)
     (rank value ?last)
     (technique rank ?next
                (> ?next ?last))
     (not rank value ?p
          (> ?p ?last))
     (not technique rank ?q
          ((> ?q ?last) (< ?q ?next)))
     =>
     (asser rank value ?next process yes))

   (enable-techniques
     10
     (phase value match)
     (size value ?s)
     (not possible value any)
     (not rank value ?r)
     =>
     (asser rank value 1 process yes))

   (expand-any-start
     10
     (phase value expand-any)
     (possible row ?r column ?c value any id ?id)
     (not possible value any id ?id2
          (< ?id2 ?id))
     =>
     (asser iterate-rc row ?r column ?c index 1))

   (expand-any
     10
     (phase value expand-any)
     (possible row ?r column ?c value any group ?g id ?id)
     (size value ?s)
     ?f (iterate-rc row ?r column ?c index ?v)
     (size-value size ?as value ?v
                 (<= ?as ?s))
     (not possible value any id ?id2
          (< ?id2 ?id))
     (not possible row ?r column ?c value ?v)
     =>
     (asser possible row ?r column ?c value ?v group ?g id ?id)
     (modify ?f index (+ ?v 1)))

   (position-expanded
     10
     (phase value expand-any)
     ?f1 (possible row ?r column ?c value any)
     (size value ?s)
     ?f2 (iterate-rc row ?r column ?c index ?v)
     (not size-value size ?as value ?v
          (<= ?as ?s))
     =>
     (asser unsolved row ?r column ?c)
     (retract ?f1 ?f2))

   (expand-any-done
     10
     ?f (phase value expand-any)
     (not possible value any)
     =>
     (retract ?f)
     (asser phase value initial-output)
     (asser print-position a1 1 a2 1))

   (begin-match
     -20
     ?f (phase value initial-output)
     =>
     (retract ?f)
     (asser phase value match))

   (begin-elimination
     -20
     ?f (phase value match)
     (impossible)
     =>
     (retract ?f)
     (asser phase value elimination))

   (next-rank-unsolved
     -20
     (phase value match)
     (rank value ?last)
     (technique rank ?next
                (> ?next ?last))
     (unsolved row ?r)
     (not impossible id ?id)
     (not rank value ?p
          (> ?p ?last))
     (not technique rank ?p
          ((> ?p ?last) (< ?p ?next)))
     =>
     (println [:NEXT-RANK ?next :PROCESS-YES])
     (asser rank value ?next process yes))

   (next-rank-not-unsolved
     -20
     (phase value match)
     (rank value ?last)
     (technique rank ?next
                (> ?next ?last))
     (not impossible id ?id)
     (not rank value ?p
          (> ?p ?last))
     (not technique rank ?p
          ((> ?p ?last) (< ?p ?next)))
     (not unsolved row ?r)
     =>
     (println [:NEXT-RANK ?next :PROCESS-NO])
     (asser rank value ?next process no))

   (begin-output
     -20
     ?f (phase value match)
     (rank value ?last)
     (not impossible id ?id)
     (not rank value ?p
          (> ?p ?last))
     (not technique rank ?next
          (> ?next ?last))
     =>
     (retract ?f)
     (asser phase value final-output)
     (asser print-position a1 1 a2 1))

   (initialize-techniques
     10
     (phase value start)
     =>
     (asser technique name Naked-Single rank 1)
     (asser technique name Hidden-Single rank 2)
     (asser technique name Locked-Candidate-Single-Line rank 3)
     (asser technique name Locked-Candidate-Multiple-Lines rank 4)
     (asser technique name Naked-Pairs rank 5)
     (asser technique name Hidden-Pairs rank 6)
     (asser technique name X-Wing rank 7)
     (asser technique name Naked-Triples rank 8)
     (asser technique name Hidden-Triples rank 9)
     (asser technique name XY-Wing rank 10)
     (asser technique name Swordfish rank 11)
     (asser technique name Duplicate-Color rank 12)
     (asser technique name Color-Conjugate-Pair rank 13)
     (asser technique name Multi-Color-Type-1 rank 14)
     (asser technique name Multi-Color-Type-2 rank 15)
     (asser technique name Forced-Chain-Convergence rank 16)
     (asser technique name Forced-Chain-XY rank 17)
     (asser technique name Unique-Rectangle rank 18))

   (remove-colors
     20
     (phase value elimination)
     ?f (position-value-color id ?id)
     =>
     (retract ?f))

   (remove-chains
     20
     (phase value elimination)
     ?f (chain)
     =>
     (retract ?f))

   (remove-unsolved
     20
     (phase value elimination)
     ?f (unsolved row ?r column ?c)
     (possible row ?r column ?c value ?v)
     (not possible row ?r column ?c value ?v2
          (not= ?v2 ?v))
     =>
     (retract ?f))

   (eliminate-not-employed
     10
     (phase value elimination)
     ?f1 (impossible id ?id value ?v rank ?p reason ?r)
     ?f2 (possible id ?id value ?v row ?rr column ?cr group ?gr)
     (not technique-employed rank ?p)
     (not impossible id ?id2
          (< ?id2 ?id))
     (not impossible id ?id value ?v2
          (< ?v2 ?v))
     (not impossible id ?id value ?v rank ?p2
          (< ?p2 ?p))
     =>
     (retract ?f1 ?f2)
     (asser technique-employed rank ?p reason ?r))

   (eliminate-employed
     10
     (phase value elimination)
     ?f1 (impossible id ?id value ?v rank ?p reason ?r)
     ?f2 (possible id ?id value ?v row ?rr column ?cr group ?gr)
     (technique-employed rank ?p)
     (not impossible id ?id2
          (< ?id2 ?id))
     (not impossible id ?id value ?v2
          (< ?v2 ?v))
     (not impossible id ?id value ?v rank ?p2
          (< ?p2 ?p))
     =>
     (retract ?f1 ?f2))

   (remove-extra
     10
     (phase value elimination)
     ?f (impossible id ?id value ?v)
     (not possible id ?id value ?v)
     =>
     (retract ?f))

   (elimination-done
     10
     ?f (phase value elimination)
     (not impossible id ?id)
     =>
     (retract ?f)
     (asser phase value match))

   (naked-single-group
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name Naked-Single rank ?p)
     (possible value ?v group ?g id ?id)
     (possible value ?v group ?g id ?id2
               (not= ?id2 ?id))
     (not possible value ?v2 group ?g id ?id
          (not= ?v2 ?v))
     (not impossible id ?id2 value ?v rank ?p)
     =>
     (asser impossible id ?id2 value ?v rank ?p reason "Naked Single"))

   (naked-single-row
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name Naked-Single rank ?p)
     (possible value ?v row ?r id ?id)
     (possible value ?v row ?r id ?id2
               (not= ?id2 ?id))
     (not possible value ?v2 row ?r id ?id
          (not= ?v2 ?v))
     (not impossible id ?id2 value ?v rank ?p)
     =>
     (asser impossible id ?id2 value ?v rank ?p reason "Naked Single"))

   (naked-single-column
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name Naked-Single rank ?p)
     (possible value ?v column ?c id ?id)
     (possible value ?v column ?c id ?id2
               (not= ?id2 ?id))
     (not possible value ?v2 column ?c id ?id
          (not= ?v2 ?v))
     (not impossible id ?id2 value ?v rank ?p)
     =>
     (asser impossible id ?id2 value ?v rank ?p reason "Naked Single"))

   (hidden-single-group
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name Hidden-Single rank ?p)
     (possible value ?v group ?g id ?id)
     (possible value ?v2 group ?g id ?id
               (not= ?v2 ?v))
     (not possible value ?v group ?g id ?id2
          (not= ?id2 ?id))
     (not impossible id ?id value ?v2 rank ?p)
     =>
     (asser impossible id ?id value ?v2 rank ?p reason "Hidden Single"))

   (hidden-single-row
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name Hidden-Single rank ?p)
     (possible value ?v row ?r id ?id)
     (possible value ?v2 row ?r id ?id
               (not= ?v2 ?v))
     (not possible value ?v row ?r id ?id2
          (not= ?id2 ?id))
     (not impossible id ?id value ?v2 rank ?p)
     =>
     (asser impossible id ?id value ?v2 rank ?p reason "Hidden Single"))

   (hidden-single-column
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name Hidden-Single rank ?p)
     (possible value ?v column ?c id ?id)
     (possible value ?v2 column ?c id ?id
               (not= ?v2 ?v))
     (not possible value ?v column ?c id ?id2
          (not= ?id2 ?id))
     (not impossible id ?id value ?v2 rank ?p)
     =>
     (asser impossible id ?id value ?v2 rank ?p reason "Hidden Single"))

   (locked-candidate-single-line-row
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name Locked-Candidate-Single-Line rank ?p)
     (possible value ?v row ?r group ?g)
     (possible value ?v row ?r group ?g2 id ?id
               (not= ?g2 ?g))
     (not possible value ?v row ?r2 group ?g
          (not= ?r2 ?r))
     (not impossible id ?id value ?v rank ?p)
     =>
     (asser impossible id ?id value ?v rank ?p reason "Locked Candidate Single Line"))

   (locked-candidate-single-line-column
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name Locked-Candidate-Single-Line rank ?p)
     (possible value ?v column ?c group ?g)
     (possible value ?v column ?c group ?g2 id ?id
               (not= ?g2 ?g))
     (not possible value ?v column ?c2 group ?g
          (not= ?c2 ?c))
     (not impossible id ?id value ?v rank ?p)
     =>
     (asser impossible id ?id value ?v rank ?p reason "Locked Candidate Single Line"))

   (locked-candidates-multiple-lines-row
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name Locked-Candidate-Multiple-Lines rank ?p)
     (possible value ?v row ?r group ?g)
     (possible value ?v row ?r2 group ?g id ?id
               (not= ?r2 ?r))
     (not possible value ?v row ?r group ?g2
          (not= ?g2 ?g))
     (not impossible id ?id value ?v rank ?p)
     =>
     (asser impossible id ?id value ?v rank ?p reason "Locked Candidate Multiple Lines"))

   (locked-candidate-multiple-lines-column
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name Locked-Candidate-Multiple-Lines rank ?p)
     (possible value ?v column ?c group ?g)
     (possible value ?v column ?c2 group ?g id ?id
               (not= ?c2 ?c))
     (not possible value ?v column ?c group ?g2
          (not= ?g2 ?g))
     (not impossible id ?id value ?v rank ?p)
     =>
     (asser impossible id ?id value ?v rank ?p reason "Locked Candidate Multiple Lines"))

   (naked-pairs-row
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name Naked-Pairs rank ?p)
     (possible value ?v1 row ?r column ?c1)
     (possible value ?v2 row ?r column ?c1
               (not= ?v2 ?v1))
     (possible value ?v1 row ?r column ?c2
               (not= ?c2 ?c1))
     (possible value ?v2 row ?r column ?c2)
     (possible value ?v row ?r column ?c3 id ?id
		([(= ?v ?v1) (= ?v ?v2)]
		 (not= ?c3 ?c2) (not= ?c3 ?c1)))
     (not possible value ?v4 row ?r column ?c1
          ((not= ?v4 ?v2) (not= ?v4 ?v1)))
     (not possible value ?v5 row ?r column ?c2
          ((not= ?v5 ?v2) (not= ?v5 ?v1)))
     (not impossible id ?id value ?v rank ?p)
     =>
     (asser impossible id ?id value ?v rank ?p reason "Naked Pairs"))

   (naked-pairs-column
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name Naked-Pairs rank ?p)
     (possible value ?v1 row ?r1 column ?c)
     (possible value ?v2 row ?r1 column ?c
               (not= ?v2 ?v1))
     (possible value ?v1 row ?r2 column ?c
               (not= ?r2 ?r1))
     (possible value ?v2 row ?r2 column ?c)
     (possible value ?v row ?r3 column ?c id ?id
		([(= ?v ?v1) (= ?v ?v2)]
		 (not= ?r3 ?r2) (not= ?r3 ?r1)))
     (not possible value ?v4 row ?r1 column ?c
          ((not= ?v4 ?v2) (not= ?v4 ?v1)))
     (not possible value ?v5 row ?r2 column ?c
          ((not= ?v5 ?v2) (not= ?v5 ?v1)))
     (not impossible id ?id value ?v rank ?p)
     =>
     (asser impossible id ?id value ?v rank ?p reason "Naked Pairs"))

   (naked-pairs-group
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name Naked-Pairs rank ?p)
     (possible value ?v1 group ?g id ?id1)
     (possible value ?v2 id ?id1
               (not= ?v2 ?v1))
     (possible value ?v1 group ?g id ?id2
               (not= ?id2 ?id1))
     (possible value ?v2 id ?id2)
     (possible value ?v group ?g id ?id
		([(= ?v ?v1) (= ?v ?v2)]
		 (not= ?id ?id2) (not= ?id ?id1)))
     (not possible value ?v4 id ?id1
          ((not= ?v4 ?v2) (not= ?v4 ?v1)))
     (not possible value ?v5 id ?id2
          ((not= ?v5 ?v2) (not= ?v5 ?v1)))
     (not impossible id ?id value ?v rank ?p)
     =>
     (asser impossible id ?id value ?v rank ?p reason "Naked Pairs"))

   (hidden-pairs-row
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name Hidden-Pairs rank ?p)
     (possible value ?v1 row ?r column ?c1)
     (possible value ?v2 row ?r column ?c1
               (not= ?v2 ?v1))
     (possible value ?v1 row ?r column ?c2
               (not= ?c2 ?c1))
     (possible value ?v2 row ?r column ?c2)
     (possible value ?v row ?r column ?c4 id ?id
               ((not= ?v ?v2) (not= ?v ?v1)
                [(= ?c4 ?c1) (= ?c4 ?c2)]))
     (not possible value ?v3 row ?r column ?c3
          ([(= ?v3 ?v1) (= ?v3 ?v2)]
           (not= ?c3 ?c2) (not= ?c3 ?c1)))
     (not impossible id ?id value ?v rank ?p)
     =>
     (asser impossible id ?id value ?v rank ?p reason "Hidden Pairs"))

   (hidden-pairs-column
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name Hidden-Pairs rank ?p)
     (possible value ?v1 row ?r1 column ?c)
     (possible value ?v2 row ?r1 column ?c
               (not= ?v2 ?v1))
     (possible value ?v1 row ?r2 column ?c
               (not= ?r2 ?r1))
     (possible value ?v2 row ?r2 column ?c)
     (possible value ?v row ?r4 column ?c id ?id
               ((not= ?v ?v2) (not= ?v ?v1)
                [(= ?r4 ?r2) (= ?r4 ?r1)]))
     (not possible value ?v3 row ?r3 column ?c
          ([(= ?v3 ?v2) (= ?v3 ?v1)]
           (not= ?r3 ?r2) (not= ?r3 ?r1)))
     (not impossible id ?id value ?v rank ?p)
     =>
     (asser impossible id ?id value ?v rank ?p reason "Hidden Pairs"))

   (hidden-pairs-group
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name Hidden-Pairs rank ?p)
     (possible value ?v1 group ?g id ?id1)
     (possible value ?v2 id ?id1
               (not= ?v2 ?v1))
     (possible value ?v1 group ?g id ?id2
               (not= ?id2 ?id1))
     (possible value ?v2 id ?id2)
     (possible value ?v id ?id
               ((not= ?v ?v2) (not= ?v ?v1)
                [(= ?id ?id2) (= ?id ?id1)]))
     (not possible value ?v3 group ?g id ?id3
          ([(= ?v3 ?v2) (= ?v3 ?v1)]
           (not= ?id3 ?id2) (not= ?id3 ?id1)))
     (not impossible id ?id value ?v rank ?p)
     =>
     (asser impossible id ?id1 value ?v rank ?p reason "Hidden Pairs"))

   (X-Wing-Row
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name X-Wing rank ?p)
     (possible value ?v row ?r1 column ?c1)
     (possible value ?v row ?r1 column ?c2
               (not= ?c2 ?c1))
     (possible value ?v row ?r2 column ?c1
               (not= ?r2 ?r1))
     (possible value ?v row ?r2 column ?c2)
     (possible value ?v row ?r3 column ?c5 id ?id
		([(= ?c5 ?c1) (= ?c5 ?c2)]
		 (not= ?r3 ?r1) (not= ?r3 ?r2)))
     (not possible value ?v row ?r1 column ?c3
          ((not= ?c3 ?c1) (not= ?c3 ?c2)))
     (not possible value ?v row ?r2 column ?c4
          ((not= ?c4 ?c1) (not= ?c4 ?c2)))
     (not impossible id ?id value ?v rank ?p)
     =>
     (asser impossible id ?id value ?v rank ?p reason "X Wing"))

   (X-Wing-Column
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name X-Wing rank ?p)
     (possible value ?v row ?r1 column ?c1)
     (possible value ?v row ?r2 column ?c1
               (not= ?r2 ?r1))
     (possible value ?v row ?r1 column ?c2
               (not= ?c2 ?c1))
     (possible value ?v row ?r2 column ?c2)
     (possible value ?v row ?r5 column ?c3 id ?id
               ([(= ?r5 ?r2) (= ?r5 ?r1)]
                (not= ?c3 ?c2) (not= ?c3 ?c1)))
     (not possible value ?v row ?r3 column ?c1
          ((not= ?r3 ?r2) (not= ?r3 ?r1)))
     (not possible value ?v row ?r4 column ?c2
          ((not= ?r4 ?r2) (not= ?r4 ?r1)))
     (not impossible id ?id value ?v rank ?p)
     =>
     (asser impossible id ?id value ?v rank ?p reason "X Wing"))

   (generate-triples
     0
     (rank value ?p process yes)
     (technique name Naked-Triples rank ?p)
     (size value ?s)
     (size-value size ?sv1 value ?v1
                 (<= ?sv1 ?s))
     (size-value size ?sv2 value ?v2
                 ((<= ?sv2 ?s) (> ?v2 ?v1)))
     (size-value size ?sv3 value ?v3
                 ((<= ?sv3 ?s) (> ?v3 ?v2)))
     =>
     (asser triple v1 ?v1 v2 ?v2 v3 ?v3))

   (naked-triples-row
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name Naked-Triples rank ?p)
     (triple v1 ?v1 v2 ?v2 v3 ?v3)
     (possible value ?v1 row ?r id ?id1)
     (possible value ?v2 row ?r id ?id2
               (not= ?id2 ?id1))
     (possible value ?v3 row ?r id ?id3
               ((not= ?id3 ?id2) (not= ?id3 ?id1)))
     (possible value ?v row ?r id ?id
               ([(= ?v ?v1) (= ?v ?v2) (= ?v ?v3)]
                (not= ?id ?id1) (not= ?id ?id2) (not= ?id ?id3)))
     (not possible value ?v4 id ?id1
          ((not= ?v4 ?v1) (not= ?v4 ?v2) (not= ?v4 ?v3)))
     (not possible value ?v5 id ?id2
          ((not= ?v5 ?v1) (not= ?v5 ?v2) (not= ?v5 ?v3)))
     (not possible value ?v6 id ?id3
          ((not= ?v6 ?v1) (not= ?v6 ?v2) (not= ?v6 ?v3)))
     (not impossible id ?id value ?v rank ?p)
     =>
     (asser impossible id ?id value ?v rank ?p reason "Naked Triples"))

   (naked-triples-column
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name Naked-Triples rank ?p)
     (triple v1 ?v1 v2 ?v2 v3 ?v3)
     (possible value ?v1 column ?c id ?id1)
     (possible value ?v2 column ?c id ?id2
               (not= ?id2 ?id1))
     (possible value ?v3 column ?c id ?id3
               ((not= ?id3 ?id2) (not= ?id3 ?id1)))
     (possible value ?v column ?c id ?id
               ([(= ?v ?v1) (= ?v ?v2) (= ?v ?v3)]
                (not= ?id ?id1) (not= ?id ?id2) (not= ?id ?id3)))
     (not possible value ?v4 id ?id1
          ((not= ?v4 ?v1) (not= ?v4 ?v2) (not= ?v4 ?v3)))
     (not possible value ?v5 id ?id2
          ((not= ?v5 ?v1) (not= ?v5 ?v2) (not= ?v5 ?v3)))
     (not possible value ?v6 id ?id3
          ((not= ?v6 ?v1) (not= ?v6 ?v2) (not= ?v6 ?v3)))
     (not impossible id ?id value ?v rank ?p)
     =>
     (asser impossible id ?id value ?v rank ?p reason "Naked Triples"))

   (naked-triples-group
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name Naked-Triples rank ?p)
     (triple v1 ?v1 v2 ?v2 v3 ?v3)
     (possible value ?v1 group ?g id ?id1)
     (possible value ?v2 group ?g id ?id2
               (not= ?id2 ?id1))
     (possible value ?v3 group ?g id ?id3
               ((not= ?id3 ?id2) (not= ?id3 ?id1)))
     (possible value ?v group ?g id ?id
               ([(= ?v ?v1) (= ?v ?v2) (= ?v ?v3)]
                (not= ?id ?id1) (not= ?id ?id2) (not= ?id ?id3)))
     (not possible value ?v4 id ?id1
          ((not= ?v4 ?v1) (not= ?v4 ?v2) (not= ?v4 ?v3)))
     (not possible value ?v5 id ?id2
          ((not= ?v5 ?v1) (not= ?v5 ?v2) (not= ?v5 ?v3)))
     (not possible value ?v6 id ?id3
          ((not= ?v6 ?v1) (not= ?v6 ?v2) (not= ?v6 ?v3)))
     (not impossible id ?id value ?v rank ?p)
     =>
     (asser impossible id ?id value ?v rank ?p reason "Naked Triples"))

   (hidden-triples-row
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name Hidden-Triples rank ?p)
     (triple v1 ?v1 v2 ?v2 v3 ?v3)
     (possible value ?v1 row ?r column ?c1)
     (possible value ?v2 row ?r column ?c2
               (not= ?c2 ?c1))
     (possible value ?v3 row ?r column ?c3
               ((not= ?c3 ?c2) (not= ?c3 ?c1)))
     (possible value ?v row ?r column ?c5 id ?id
               ((not= ?v ?v1) (not= ?v ?v2) (not= ?v ?v3)
                [(= ?c5 ?c1) (= ?c5 ?c2) (= ?c5 ?c3)]))
     (not possible value ?v4 row ?r column ?c4
          ([(= ?v4 ?v1) (= ?v4 ?v2) (= ?v4 ?v3)]
           (not= ?c4 ?c3) (not= ?c4 ?c2) (not= ?c4 ?c1)))
     (not impossible id ?id value ?v rank ?p)
     =>
     (asser impossible id ?id value ?v rank ?p reason "Hidden Triples"))

   (hidden-triples-column
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name Hidden-Triples rank ?p)
     (triple v1 ?v1 v2 ?v2 v3 ?v3)
     (possible value ?v1 row ?r1 column ?c)
     (possible value ?v2 row ?r2 column ?c
               (not= ?r2 ?r1))
     (possible value ?v3 row ?r3 column ?c
               ((not= ?r3 ?r2) (not= ?r3 ?r1)))
     (possible value ?v row ?r5 column ?c id ?id
               ((not= ?v ?v1) (not= ?v ?v2) (not= ?v ?v3)
                [(= ?r5 ?r1) (= ?r5 ?r2) (= ?r5 ?r3)]))
     (not possible value ?v4 row ?r4 column ?c
          ([(= ?v4 ?v1) (= ?v4 ?v2) (= ?v4 ?v3)]
           (not= ?r4 ?r3) (not= ?r4 ?r2) (not= ?r4 ?r1)))
     (not impossible id ?id value ?v rank ?p)
     =>
     (asser impossible id ?id value ?v rank ?p reason "Hidden Triples"))

   (hidden-triples-group
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name Hidden-Triples rank ?p)
     (triple v1 ?v1 v2 ?v2 v3 ?v3)
     (possible value ?v1 id ?id1 group ?g)
     (possible value ?v2 id ?id2 group ?g
               (not= ?id2 ?id1))
     (possible value ?v3 id ?id3 group ?g
               ((not= ?id3 ?id2) (not= ?id3 ?id1)))
     (possible value ?v id ?id
               ((not= ?v ?v1) (not= ?v ?v2) (not= ?v ?v3)
                [(= ?id ?id1) (= ?id ?id2) (= ?id ?id3)]))
     (not possible value ?v4 id ?id4 group ?g
          ([(= ?v4 ?v1) (= ?v4 ?v2) (= ?v4 ?v3)]
           (not= ?id4 id3) (not= ?id4 ?id2) (not= ?id4 ?id1)))
     (not impossible id ?id value ?v rank ?p)
     =>
     (asser impossible id ?id value ?v rank ?p reason "Hidden Triples"))

   (swordfish-row
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name Swordfish rank ?p)
     (triple v1 ?c1 v2 ?c2 v3 ?c3)
     (possible value ?v row ?r1 column ?c1)
     (possible value ?v row ?r2 column ?c2
               (not= ?r2 ?r1))
     (possible value ?v row ?r3 column ?c3
               ((not= ?r3 ?r2) (not= ?r3 ?r1)))
     (possible value ?v row ?r4 column ?c7 id ?id
               ((not= ?r4 ?r1) (not= ?r4 ?r2) (not= ?r4 ?r3)
                [(= ?c7 ?c1) (= ?c7 ?c2) (= ?c7 ?c3)]))
     (not possible value ?v row ?r1 column ?c4
          ((not= ?c4 ?c1) (not= ?c4 ?c2) (not= ?c4 ?c3)))
     (not possible value ?v row ?r2 column ?c5
          ((not= ?c5 ?c1) (not= ?c5 ?c2) (not= ?c5 ?c3)))
     (not possible value ?v row ?r3 column ?c6
          ((not= ?c6 ?c1) (not= ?c6 ?c2) (not= ?c6 ?c3)))
     (not impossible id ?id value ?v rank ?p)
     =>
     (asser impossible id ?id value ?v rank ?p reason "Swordfish"))

   (swordfish-column
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name Swordfish rank ?p)
     (triple v1 ?r1 v2 ?r2 v3 ?r3)
     (possible value ?v row ?r1 column ?c1)
     (possible value ?v row ?r2 column ?c2
               (not= ?c2 ?c1))
     (possible value ?v row ?r3 column ?c3
               ((not= ?c3 ?c2) (not= ?c3 ?c1)))
     (possible value ?v row ?r7 column ?c4 id ?id
               ([(= ?r7 ?r1) (= ?r7 ?r2) (= ?r7 ?r3)]
                (not= ?c4 ?c1) (not= ?c4 ?c2) (not= ?c4 ?c3)))
     (not possible value ?v row ?r4 column ?c1
          ((not= ?r4 ?r1) (not= ?r4 ?r2) (not= ?r4 ?r3)))
     (not possible value ?v row ?r5 column ?c2
          ((not= ?r5 ?r1) (not= ?r5 ?r2) (not= ?r5 ?r3)))
     (not possible value ?v row ?r6 column ?c3
          ((not= ?r6 ?r1) (not= ?r6 ?r2) (not= ?r6 ?r3)))
     (not impossible id ?id value ?v rank ?p)
     =>
     (asser impossible id ?id value ?v rank ?p reason "Swordfish"))

   (XY-Wing
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name XY-Wing rank ?p)
     (possible value ?x row ?r1 column ?c1 group ?g1 id ?id1)
     (possible value ?y id ?id1
               (not= ?y ?x))
     (possible value ?x row ?r2 column ?c2 group ?g2 id ?id2
               (not= ?id2 ?id1))
     (possible value ?z id ?id2
               ((not= ?z ?x)
                [(= ?r1 ?r2) (= ?c1 ?c2) (= ?g1 ?g2)]))
     (possible value ?y row ?r3 column ?c3 group ?g3 id ?id3
               ((not= ?id3 ?id2) (not= ?id3 ?id1)))
     (possible value ?z id ?id3
               ((not= ?z ?y)
                [(= ?r1 ?r3) (= ?c1 ?c3) (= ?g1 ?g3)]))
     (possible value ?z row ?r4 column ?c4 group ?g4 id ?id
               ((not= ?id ?id3) (not= ?id ?id2) (not= ?id ?id1)
                [(= ?r2 ?r4) (= ?c2 ?c4) (= ?g2 ?g4)]
                [(= ?r3 ?r4) (= ?c3 ?c4) (= ?g3 ?g4)]))
     (not possible value ?v1 id ?id1
          ((not= ?v1 ?y) (not= ?v1 ?x)))
     (not possible value ?v2 id ?id2
          ((not= ?v2 ?z) (not= ?v2 ?x)))
     (not possible value ?v3 id ?id3
          ((not= ?v3 ?z) (not= ?v3 ?y)))
     (not impossible id ?id value ?z rank ?p)
     =>
     (asser impossible id ?id value ?z rank ?p reason "XY-Wing"))

   (initialize-color-pairs
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name ?n rank ?p
                [(= ?n Duplicate-Color) 
                 (= ?n Color-Conjugate-Pair) 
                 (= ?n Multi-Color-Type-1) 
                 (= ?n Multi-Color-Type-2)])
     (not color-pair c1 ?c1)
     =>
     (asser color-pair c1 green c2 magenta)
     (asser color-pair c1 magenta c2 green)
     (asser color-pair c1 orange c2 azure)
     (asser color-pair c1 azure c2 orange)
     (asser color-pair c1 violet c2 chartruese)
     (asser color-pair c1 chartruese c2 violet)
     (asser color-pair c1 aquamarine c2 fuchsia)
     (asser color-pair c1 fuchsia c2 aquamarine)
     (asser color-pair c1 yellow c2 blue)
     (asser color-pair c1 blue c2 yellow)
     (asser color-pair c1 red c2 cyan)
     (asser color-pair c1 cyan c2 red))

   (color-row
     -10
     (phase value match)
     (rank value ?p process yes)
     (technique name ?n rank ?p
                [(= ?n Duplicate-Color) 
                 (= ?n Color-Conjugate-Pair) 
                 (= ?n Multi-Color-Type-1) 
                 (= ?n Multi-Color-Type-2)])
     (possible row ?r column ?c1 group ?g1 id ?id1 value ?v)
     (possible row ?r column ?c2 group ?g2 id ?id2 value ?v
               (not= ?c2 ?c1))
     (color-pair c1 ?color1 c2 ?color2)
     (not possible row ?r column ?c3 value ?v
          ((not= ?c3 ?c2) (not= ?c3 ?c1)))
     (not position-value-color value ?v color ?color0
          [(= ?color0 ?color1) (= ?color0 ?color2)])
     (not position-value-color row ?r column ?c0 value ?v
          [(= ?c0 ?c1) (= ?c0 ?c2)])
     =>
     (asser position-value-color
            row ?r
            column ?c1
            group ?g1
            id ?id1
            value ?v
            color ?color1)
     (asser position-value-color
            row ?r
            column ?c2
            group ?g2
            id ?id2
            value ?v
            color ?color2))

   (color-column
     -10
     (phase value match)
     (rank value ?p process yes)
     (technique name ?n rank ?p
                [(= ?n Duplicate-Color) 
                 (= ?n Color-Conjugate-Pair) 
                 (= ?n Multi-Color-Type-1) 
                 (= ?n Multi-Color-Type-2)])
     (possible row ?r1 column ?c group ?g1 id ?id1 value ?v)
     (possible row ?r2 column ?c group ?g2 id ?id2 value ?v
               (not= ?r2 ?r1))
     (color-pair c1 ?color1 c2 ?color2)
     (not possible row ?r3 column ?c value ?v
          ((not= ?r3 ?r2) (not= ?r3 ?r1)))
     (not position-value-color value ?v color ?color0
          [(= ?color0 ?color1) (= ?color0 ?color2)])
     (not position-value-color row ?r0 column ?c value ?v
          [(= ?r0 ?r1) (= ?r0 ?r2)])
     =>
     (asser position-value-color
            row ?r1
            column ?c
            group ?g1
            id ?id1
            value ?v
            color ?color1)
     (asser position-value-color
            row ?r2
            column ?c
            group ?g2
            id ?id2
            value ?v
            color ?color2))

   (propagate-color-row
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name ?n rank ?p
                [(= ?n Duplicate-Color) 
                 (= ?n Color-Conjugate-Pair) 
                 (= ?n Multi-Color-Type-1) 
                 (= ?n Multi-Color-Type-2)])
     (position-value-color row ?r column ?c1 value ?v color ?color1)
     (possible row ?r column ?c2 group ?g id ?id value ?v
               (not= ?c2 ?c1))
     (color-pair c1 ?color1 c2 ?color2)
     (not position-value-color row ?r column ?c2 value ?v)
     (not possible row ?r column ?c3 value ?v
          ((not= ?c3 ?c2) (not= ?c3 ?c1)))
     =>
     (asser position-value-color
            row ?r
            column ?c2
            group ?g
            id ?id
            value ?v
            color ?color2))

   (propagate-color-column
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name ?n rank ?p
                [(= ?n Duplicate-Color) 
                 (= ?n Color-Conjugate-Pair) 
                 (= ?n Multi-Color-Type-1) 
                 (= ?n Multi-Color-Type-2)])
     (position-value-color row ?r1 column ?c value ?v color ?color1)
     (possible row ?r2 column ?c group ?g id ?id value ?v
               (not= ?r2 ?r1))
     (color-pair c1 ?color1 c2 ?color2)
     (not position-value-color row ?r2 column ?c value ?v)
     (not possible row ?r3 column ?c value ?v
          ((not= ?r3 ?r2) (not= ?r3 ?r1)))
     =>
     (asser position-value-color
            row ?r2
            column ?c
            group ?g
            id ?id
            value ?v
            color ?color2))

   (propagate-color-group
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name ?n rank ?p
                [(= ?n Duplicate-Color) 
                 (= ?n Color-Conjugate-Pair) 
                 (= ?n Multi-Color-Type-1) 
                 (= ?n Multi-Color-Type-2)])
     (position-value-color column ?c1 row ?r1 group ?g id ?id1 value ?v color ?color1)
     (possible column ?c2 row ?r2 group ?g id ?id2 value ?v
               (not= ?id2 ?id1))
     (color-pair c1 ?color1 c2 ?color2)
     (not position-value-color column ?c2 row ?r2 value ?v)
     (not possible group ?g id ?id3 value ?v
          ((not= ?id3 ?id2) (not= ?id3 ?id1)))
     =>
     (asser position-value-color
            column ?c2
            row ?r2
            group ?g
            id ?id2
            value ?v
            color ?color2))

   (duplicate-color-in-row
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name Duplicate-Color rank ?p)
     (position-value-color row ?r
                           column ?c1
                           id ?id1
                           value ?v
                           color ?color)
     (position-value-color row ?r
                           column ?c2
                           id ?id2
                           value ?v
                           color ?color
                           (not= ?c2 ?c1))
     (not impossible id ?id1 value ?v rank ?p)
     =>
     (asser impossible id ?id1 value ?v rank ?p reason "Duplicate Color"))


   (duplicate-color-in-column
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name Duplicate-Color rank ?p)
     (position-value-color row ?r1
                           column ?c
                           id ?id1
                           value ?v
                           color ?color)
     (position-value-color row ?r2
                           column ?c
                           id ?id2
                           value ?v
                           color ?color
                           (not= ?r2 ?r1))
     (not impossible id ?id1 value ?v rank ?p)
     =>
     (asser impossible id ?id1 value ?v rank ?p reason "Duplicate Color"))

   (duplicate-color-in-group
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name Duplicate-Color rank ?p)
     (position-value-color group ?g
                           id ?id1
                           value ?v
                           color ?color)
     (position-value-color group ?g
                           id ?id2
                           value ?v
                           color ?color
                           (not= ?id2 ?id1))
     (not impossible id ?id1 value ?v rank ?p)
     =>
     (asser impossible id ?id1 value ?v rank ?p reason "Duplicate Color"))

   (color-conjugate-pair
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name Color-Conjugate-Pair rank ?p)
     (color-pair c1 ?color1 c2 ?color2)
     (position-value-color row ?r
                           column ?pc
                           value ?v
                           id ?id1
                           color ?color1)
     (position-value-color column ?c
                           row ?pr
                           value ?v
                           id ?id2
                           color ?color2
                           (not= ?id2 ?id1))
     (possible row ?r column ?c id ?id value ?v
               ((not= ?id ?id2) (not= ?id ?id1)))
     (not impossible id ?id value ?v rank ?p)
     =>
     (asser impossible id ?id value ?v rank ?p reason "Color Conjugate Pairs"))

   (multi-color-type-1
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name Multi-Color-Type-1 rank ?p)
     (color-pair c1 ?color1 c2 ?color2)
     (position-value-color row ?r1
                           column ?c1
                           group ?g1
                           id ?id1
                           value ?v
                           color ?color1)
     (position-value-color row ?r2
                           column ?c2
                           group ?g2
                           id ?id2
                           value ?v
                           color ?color2
                           (not= ?id2 ?id1))
     (color-pair c1 ?other-color c2 ?other-color2
                 ((not= ?other-color ?color1) (not= ?other-color2 ?color1)))
     (position-value-color row ?r3
                           column ?c3
                           group ?g3
                           value ?v
                           color ?other-color
                           [(= ?r1 ?r3) (= ?c1 ?c3) (= ?g1 ?g3)])
     (position-value-color row ?r4
                           column ?c4
                           group ?g4
                           value ?v
                           color ?other-color
                           [(= ?r2 ?r4) (= ?c2 ?c4) (= ?g2 ?g4)])
     (position-value-color id ?id
                           value ?v
                           color ?other-color)
     (not impossible id ?id value ?v rank ?p)
     =>
     (asser impossible id ?id value ?v rank ?p reason "Multi Color Type 1"))

   (multi-color-type-2
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name Multi-Color-Type-2 rank ?p)
     (color-pair c1 ?color1 c2 ?color2)
     (position-value-color row ?r1
                           column ?c1
                           group ?g1
                           value ?v
                           color ?color1)
     (color-pair c1 ?other-color1 c2 ?other-color2
                 ((not= ?other-color1 ?color1) (not= ?other-color2 ?color1)))
     (position-value-color row ?r2
                           column ?c2
                           group ?g2
                           value ?v
                           color ?other-color1
                           [(= ?r1 ?r2) (= ?c1 ?c2) (= ?g1 ?g2)])
     (position-value-color row ?r3
                           column ?c3
                           group ?g3
                           id ?id3
                           value ?v
                           color ?color2)
     (position-value-color row ?r4
                           column ?c4
                           group ?g4
                           id ?id4
                           value ?v
                           color ?other-color2)
     (possible row ?r5 column ?c5 id ?id group ?g5 value ?v
               ((not= ?id ?id3) (not= ?id ?id4)
                [(= ?r3 ?r5) (= ?c3 ?c5) (= ?g3 ?g5)]
                [(= ?r4 ?r5) (= ?c4 ?c5) (= ?g4 ?g5)]))
     (not impossible id ?id value ?v rank ?p)
     =>
     (asser impossible id ?id value ?v rank ?p reason "Multi Color Type 2"))

   (start-chain
     -10
     (phase value match)
     (rank value ?p process yes)
     (technique name ?n rank ?p
                [(= ?n Forced-Chain-Convergence) (= ?n Forced-Chain-XY)])
     (possible row ?r column ?c group ?g id ?id value ?v1)
     (possible id ?id value ?v2
               (not= ?v2 ?v1))
     (not possible id ?id value ?v3
          ((not= ?v3 ?v1) (not= ?v3 ?v2)))
     (not chain
          start-row ?r
          start-column ?c
          start-value ?v1
          row ?r
          column ?c
          value ?v1)
     =>
     (asser chain
            start-row ?r
            start-column ?c
            start-value ?v1
            row ?r
            column ?c
            group ?g
            id ?id
            value ?v1))

   (continue-chain-row
     -10
     (phase value match)
     (rank value ?p process yes)
     (technique name ?n rank ?p
                [(= ?n Forced-Chain-Convergence) (= ?n Forced-Chain-XY)])
     (chain row ?r column ?c1 value ?v1 start-row ?sr start-column ?sc start-value ?sv)
     (possible row ?r column ?c2 value ?v1
               (not= ?c2 ?c1))
     (possible row ?r column ?c2 group ?g id ?id value ?v2
               (not= ?v2 ?v1))
     (not possible row ?r column ?c2 value ?v3
          ((not= ?v3 ?v2) (not= ?v3 ?v1)))
     (not chain
          row ?r
          column ?c2
          value ?v2
          start-row ?sr
          start-column ?sc
          start-value ?sv)
     =>
     (asser chain
            start-row ?sr
            start-column ?sc
            start-value ?sv
            column ?c2
            row ?r
            group ?g
            id ?id
            value ?v2))

   (continue-chain-column
     -10
     (phase value match)
     (rank value ?p process yes)
     (technique name ?n rank ?p
                [(= ?n Forced-Chain-Convergence) (= ?n Forced-Chain-XY)])
     (chain row ?r1 column ?c value ?v1 start-row ?sr start-column ?sc start-value ?sv)
     (possible row ?r2 column ?c value ?v1
               (not= ?r2 ?r1))
     (possible row ?r2 column ?c group ?g id ?id value ?v2
               (not= ?v2 ?v1))
     (not possible row ?r2 column ?c value ?v3
          ((not= ?v3 ?v2) (not= ?v3 ?v1)))
     (not chain
          row ?r2
          column ?c
          value ?v2
          start-row ?sr
          start-column ?sc
          start-value ?sv)
     =>
     (asser chain
            start-row ?sr
            start-column ?sc
            start-value ?sv
            row ?r2
            column ?c
            group ?g
            id ?id
            value ?v2))

   (continue-chain-group
     -10
     (phase value match)
     (rank value ?p process yes)
     (technique name ?n rank ?p
                [(= ?n Forced-Chain-Convergence) (= ?n Forced-Chain-XY)])
     (chain group ?g id ?id1 value ?v1 start-row ?sr start-column ?sc start-value ?sv)
     (possible row ?r column ?c group ?g id ?id2 value ?v1
               (not= ?id2 ?id1))
     (possible id ?id2 value ?v2
               (not= ?v2 ?v1))
     (not possible id ?id2 value ?v3
          ((not= ?v3 ?v2) (not= ?v3 ?v1)))
     (not chain row ?r column ?c value ?v2 start-row ?sr start-column ?sc start-value ?sv)
     =>
     (asser chain
            start-row ?sr
            start-column ?sc
            start-value ?sv
            row ?r
            column ?c
            group ?g
            id ?id2
            value ?v2))

   (forced-chain-convergence
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name Forced-Chain-Convergence rank ?p)
     (chain start-row ?r1
            start-column ?c1
            start-value ?v1
            row ?r2
            column ?c2
            value ?v2)
     (chain start-row ?r1
            start-column ?c1
            start-value ?v4
            row ?r2
            column ?c2
            value ?v2
            (not= ?v4 ?v1))
     (possible row ?r2 column ?c2 id ?id value ?v3
               (not= ?v3 ?v2))
     (not impossible id ?id value ?v3 rank ?p)
     =>
     (asser impossible id ?id value ?v3 rank ?p reason "Forced Chain Convergence"))

   (forced-chain-XY
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name Forced-Chain-XY rank ?p)
     (chain start-row ?r1
            start-column ?c1
            start-value ?v1
            row ?r1
            column ?c1
            value ?v1
            id ?id1
            group ?g1)
     (chain start-row ?r1
            start-column ?c1
            start-value ?v2
            row ?r1
            column ?c1
            value ?v2
            (not= ?v2 ?v1))
     (chain start-row ?r1
            start-column ?c1
            start-value ?v2
            row ?r2
            column ?c2
            group ?g2
            id ?id2
            value ?v1
            (not= ?id2 ?id1))
     (possible row ?r3 column ?c3 id ?id group ?g3 value ?v1
               ((not= ?id ?id2) (not= ?id ?id1)
                [(= ?g1 ?g3) (= ?r1 ?r3) (= ?c1 ?c3)]
                [(= ?g2 ?g3) (= ?r2 ?r3) (= ?c2 ?c3)]))
     (not impossible id ?id value ?v1 rank ?p)
     =>
     (asser impossible id ?id value ?v1 rank ?p reason "Forced Chain XY"))

   (Unique-Rectangle-Row
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name Unique-Rectangle rank ?p)
     (possible value ?v1 group ?g1 row ?r1 column ?c1)
     (possible value ?v2 group ?g1 row ?r1 column ?c1
               (not= ?v2 ?v1))
     (possible value ?v1 group ?g1 row ?r1 column ?c2
               (not= ?c2 ?c1))
     (possible value ?v2 group ?g1 row ?r1 column ?c2)
     (possible value ?v1 group ?g2 row ?r2 column ?c1
               (not= ?g2 ?g1))
     (possible value ?v2 group ?g2 row ?r2 column ?c1)
     (possible value ?v1 id ?id1 group ?g2 row ?r2 column ?c2)
     (possible value ?v2 id ?id2 group ?g2 row ?r2 column ?c2)
     (possible value ?v6 group ?g2 row ?r2 column ?c2
               ((not= ?v6 ?v2) (not= ?v6 ?v1)))
     (not possible value ?v3 row ?r1 column ?c1
          ((not= ?v3 ?v2) (not= ?v3 ?v1)))
     (not possible value ?v4 row ?r1 column ?c2
          ((not= ?v4 ?v2) (not= ?v4 ?v1)))
     (not possible value ?v5 group ?g2 row ?r2 column ?c1
          ((not= ?v5 ?v2) (not= ?v5 ?v1)))
     (not impossible id ?id1 value ?v1 rank ?p)
     =>
     (asser impossible id ?id1 value ?v1 rank ?p reason "Unique Rectangle"))

   (Unique-Rectangle-Column
     0
     (phase value match)
     (rank value ?p process yes)
     (technique name Unique-Rectangle rank ?p)
     (possible value ?v1 group ?g1 row ?r1 column ?c1)
     (possible value ?v2 group ?g1 row ?r1 column ?c1
               (not= ?v2 ?v1))
     (possible value ?v1 group ?g1 row ?r2 column ?c1
               (not= ?r2 ?r1))
     (possible value ?v2 group ?g1 row ?r2 column ?c1)
     (possible value ?v1 group ?g2 row ?r1 column ?c2
               (not= ?g2 ?g1))
     (possible value ?v2 group ?g2 row ?r1 column ?c2)
     (possible value ?v1 id ?id1 group ?g2 row ?r2 column ?c2)
     (possible value ?v2 id ?id2 group ?g2 row ?r2 column ?c2)
     (possible value ?v6 group ?g2 row ?r2 column ?c2
               ((not= ?v6 ?v2) (not= ?v6 ?v1)))
     (not possible value ?v3 row ?r1 column ?c1
          ((not= ?v3 ?v2) (not= ?v3 ?v1)))
     (not possible value ?v4 row ?r2 column ?c1
          ((not= ?v4 ?v2) (not= ?v4 ?v1)))
     (not possible value ?v5 group ?g2 row ?r1 column ?c2
          ((not= ?v5 ?v2) (not= ?v5 ?v1)))
     (not impossible id ?id1 value ?v1 rank ?p)
     =>
     (asser impossible id ?id1 value ?v1 rank ?p reason "Unique Rectangle"))

   ;;; OUTPUT RULES

   (print-initial
     10
     (phase value initial-output)
     =>
     (println "\nThe puzzle is:\n"))

   (print-final
     10
     (phase value final-output)
     =>
     (println "The solution is:\n"))

   (print-position-value-found
     0
     (phase value ?p
            [(= ?p initial-output) (= ?p final-output)])
     (print-position a1 ?r a2 ?c)
     (possible row ?r column ?c value ?v)
     (size value ?s)
     (not possible row ?r column ?c value ?v2
          (not= ?v2 ?v))
     =>
     (asser position-printed a1 ?r a2 ?c)
     (println (str "   ROW: " ?r " COLUMN: " ?c " VALUE: " ?v )))

   (print-position-value-not-found
     -1
     (phase value ?p
            [(= ?p initial-output) (= ?p final-output)])
     (print-position a1 ?r a2 ?c)
     (size value ?s)
     (not position-printed a1 ?r a2 ?c)
     =>
     (asser position-printed a1 ?r a2 ?c)
     (println (str "   ROW: " ?r " COLUMN: " ?c " VALUE: *" )))

   (next-position-column
     -10
     (phase value ?p
            [(= ?p initial-output) (= ?p final-output)])
     (size value ?s)
     ?f1 (print-position a1 ?r a2 ?c
                         (not= ?c (* ?s ?s)))
     ?f2 (position-printed a1 ?r a2 ?c)
     =>
     (retract ?f1 ?f2)
     (asser print-position a1 ?r a2 (inc ?c)))

   (next-position-row
     -10
     (phase value ?p
            [(= ?p initial-output) (= ?p final-output)])
     (size value ?s)
     ?f1 (print-position a1 ?r a2 ?c
                         ((not= ?r (* ?s ?s)) (= ?c (* ?s ?s))))
     ?f2 (position-printed a1 ?r a2 ?c)
     =>
     (retract ?f1 ?f2)
     (asser print-position a1 (inc ?r) a2 1))

   (output-done-rule-listing
     -10
     ?f1 (phase value final-output)
     (size value ?s)
     ?f2 (print-position a1 ?r a2 ?c
                         ((= ?r (* ?s ?s)) (= ?c (* ?s ?s))))
     ?f3 (position-printed a1 ?r a2 ?c)
     (technique-employed rank ?rank)
     =>
     (println   "\nRules used:\n")
     (retract ?f1 ?f2 ?f3)
     (asser phase value list-rules))

   (output-done-no-rule-listing
     -10
     (phase value final-output)
     (size value ?s)
     ?f1 (print-position a1 ?r a2 ?c
                         ((= ?r (* ?s ?s)) (= ?c (* ?s ?s))))
     ?f2 (position-printed a1 ?r a2 ?c)
     (not technique-employed rank ?rank)
     =>
     (println)
     (retract ?f1 ?f2))

   (initial-output-done
     -10
     (phase value initial-output)
     (size value ?s)
     ?f1 (print-position a1 ?r a2 ?c
                         ((= ?r (* ?s ?s)) (= ?c (* ?s ?s))))
     ?f2 (position-printed a1 ?r a2 ?c)
     =>
     (println)
     (retract ?f1 ?f2))

   (list-rule
     0
     (phase value list-rules)
     ?f (technique-employed rank ?p reason ?reason)
     (not technique-employed rank ?p2
          (< ?p2 ?p))
     =>
     (println (str "   " ?reason ))
     (retract ?f))

   (list-rule-done
     -10
     ?f (phase value list-rules)
     =>
     (println)
     (retract ?f)))

 (functions)

 (facts
   (size value 2)
   (possible row 1 column 1 value any group 1 id 1)
   (possible row 1 column 2 value any group 1 id 2)
   (possible row 2 column 1 value 2 group 1 id 3)
   (possible row 2 column 2 value any group 1 id 4)

   (possible row 1 column 3 value 1  group 2 id 10)
   (possible row 1 column 4 value any group 2 id 11)
   (possible row 2 column 3 value any group 2 id 12)
   (possible row 2 column 4 value any group 2 id 13)

   (possible row 3 column 1 value any group 3 id 19)
   (possible row 3 column 2 value any group 3 id 20)
   (possible row 4 column 1 value any group 3 id 21)
   (possible row 4 column 2 value 2 group 3 id 22)

   (possible row 3 column 3 value any group 4 id 28)
   (possible row 3 column 4 value 4 group 4 id 29)
   (possible row 4 column 3 value any group 4 id 30)
   (possible row 4 column 4 value any group 4 id 31)
   (phase value start)))













