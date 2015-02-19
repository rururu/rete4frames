;;; The puzzle is:
;;;
;;;    * * *  8 * 3  1 * 7
;;;    1 * 6  * * *  5 * *
;;;    * * *  * * 1  * * *
;;;
;;;    * * *  * 2 *  7 * 3
;;;    9 * *  6 * 5  * * 4
;;;    4 * 3  * 8 *  * * *
;;;
;;;    * * *  4 * *  * * *
;;;    * * 7  * * *  2 * 5
;;;    2 * 1  5 * 6  * * *
;;;
;;; The solution is:
;;;
;;;    5 2 4  8 6 3  1 9 7
;;;    1 3 6  7 4 9  5 2 8
;;;    7 8 9  2 5 1  4 3 6
;;;
;;;    8 1 5  9 2 4  7 6 3
;;;    9 7 2  6 3 5  8 1 4
;;;    4 6 3  1 8 7  9 5 2
;;;
;;;    3 5 8  4 9 2  6 7 1
;;;    6 9 7  3 1 8  2 4 5
;;;    2 4 1  5 7 6  3 8 9
;;;
;;; Rules used:
;;;
;;;    Naked Single
;;;    Hidden Single
;;;    Locked Candidate Single Line
;;;    Locked Candidate Multiple Lines
;;;    Naked Pairs
;;;    Hidden Pairs
;;;    Naked Triples
;;;    Swordfish
;;;    Multi Color Type 2

((size value 3)

(possible row 1 column 1 value any group 1 id 1)
(possible row 1 column 2 value any group 1 id 2)
(possible row 1 column 3 value any group 1 id 3)
(possible row 2 column 1 value 1 group 1 id 4)
(possible row 2 column 2 value any group 1 id 5)
(possible row 2 column 3 value 6 group 1 id 6)
(possible row 3 column 1 value any group 1 id 7)
(possible row 3 column 2 value any group 1 id 8)
(possible row 3 column 3 value any group 1 id 9)

(possible row 1 column 4 value 8 group 2 id 10)
(possible row 1 column 5 value any group 2 id 11)
(possible row 1 column 6 value 3 group 2 id 12)
(possible row 2 column 4 value any group 2 id 13)
(possible row 2 column 5 value any group 2 id 14)
(possible row 2 column 6 value any group 2 id 15)
(possible row 3 column 4 value any group 2 id 16)
(possible row 3 column 5 value any group 2 id 17)
(possible row 3 column 6 value 1 group 2 id 18)

(possible row 1 column 7 value 1 group 3 id 19)
(possible row 1 column 8 value any group 3 id 20)
(possible row 1 column 9 value 7 group 3 id 21)
(possible row 2 column 7 value 5 group 3 id 22)
(possible row 2 column 8 value any group 3 id 23)
(possible row 2 column 9 value any group 3 id 24)
(possible row 3 column 7 value any group 3 id 25)
(possible row 3 column 8 value any group 3 id 26)
(possible row 3 column 9 value any group 3 id 27)

(possible row 4 column 1 value any group 4 id 28)
(possible row 4 column 2 value any group 4 id 29)
(possible row 4 column 3 value any group 4 id 30)
(possible row 5 column 1 value 9 group 4 id 31)
(possible row 5 column 2 value any group 4 id 32)
(possible row 5 column 3 value any group 4 id 33)
(possible row 6 column 1 value 4 group 4 id 34)
(possible row 6 column 2 value any group 4 id 35)
(possible row 6 column 3 value 3 group 4 id 36)

(possible row 4 column 4 value any group 5 id 37)
(possible row 4 column 5 value 2 group 5 id 38)
(possible row 4 column 6 value any group 5 id 39)
(possible row 5 column 4 value 6 group 5 id 40)
(possible row 5 column 5 value any group 5 id 41)
(possible row 5 column 6 value 5 group 5 id 42)
(possible row 6 column 4 value any group 5 id 43)
(possible row 6 column 5 value 8 group 5 id 44)
(possible row 6 column 6 value any group 5 id 45)

(possible row 4 column 7 value 7 group 6 id 46)
(possible row 4 column 8 value any group 6 id 47)
(possible row 4 column 9 value 3 group 6 id 48)
(possible row 5 column 7 value any group 6 id 49)
(possible row 5 column 8 value any group 6 id 50)
(possible row 5 column 9 value 4 group 6 id 51)
(possible row 6 column 7 value any group 6 id 52)
(possible row 6 column 8 value any group 6 id 53)
(possible row 6 column 9 value any group 6 id 54)

(possible row 7 column 1 value any group 7 id 55)
(possible row 7 column 2 value any group 7 id 56)
(possible row 7 column 3 value any group 7 id 57)
(possible row 8 column 1 value any group 7 id 58)
(possible row 8 column 2 value any group 7 id 59)
(possible row 8 column 3 value 7 group 7 id 60)
(possible row 9 column 1 value 2 group 7 id 61)
(possible row 9 column 2 value any group 7 id 62)
(possible row 9 column 3 value 1 group 7 id 63)

(possible row 7 column 4 value 4 group 8 id 64)
(possible row 7 column 5 value any group 8 id 65)
(possible row 7 column 6 value any group 8 id 66)
(possible row 8 column 4 value any group 8 id 67)
(possible row 8 column 5 value any group 8 id 68)
(possible row 8 column 6 value any group 8 id 69)
(possible row 9 column 4 value 5 group 8 id 70)
(possible row 9 column 5 value any group 8 id 71)
(possible row 9 column 6 value 6 group 8 id 72)

(possible row 7 column 7 value any group 9 id 73)
(possible row 7 column 8 value any group 9 id 74)
(possible row 7 column 9 value any group 9 id 75)
(possible row 8 column 7 value 2 group 9 id 76)
(possible row 8 column 8 value any group 9 id 77)
(possible row 8 column 9 value 5 group 9 id 78)
(possible row 9 column 7 value any group 9 id 79)
(possible row 9 column 8 value any group 9 id 80)
(possible row 9 column 9 value any group 9 id 81)

(phase value start))
