(ns test-main)
(require '[main :refer :all])
; ================ TESTING : World Size =======================
; World Size : 1 platform, 1 object
; Command : (time (ops-search test-state1 '((on floor box)) ops :world world1))
; Steps taken : 5
; Times : 14.0617 msecs, 14.053 msecs, 14.4068 msecs, 14.1693 msecs, 14.0275 msecs
; Average Time : 14.14366 msecs

; World Size : 2 platforms, 2 objects
; Command : (time (ops-search test-state2 '((on floor box)) ops :world world2))
; Steps taken : 5
; Times : 44.9945 msecs, 44.0025 msecs, 44.8904 msecs, 45.5879 msecs, 49.475 msecs
; Average Time : 45.79006 msecs

; World Size : 3 platforms, 3 objects
; Command : (time (ops-search test-state3 '((on floor box)) ops :world world3))
; Steps taken : 5
; Times : 101.1271 msecs, 101.619 msecs, 119.5458 msecs, 121.0916 msecs, 121.937 msecs
; Average Time : 113.0641 msecs

; Conclusion : The bigger the world size, the less efficient the search becomes, as there are more states to check.
; =============================================================
; ================ TESTING : Problem Direction ================
; Direction1 : Agent moves from floor to on top of platform1
; Command : (time (ops-search test-state4 '((on-top platform1 agent)) ops :world world1))
; Steps taken : 2
; Times : 4.7453 msecs, 4.7141 msecs, 4.9336 msecs, 4.6991 msecs, 5.2745 msecs
; Average Time: 4.87332 msecs

; Direction2 : Agent moves from on top of platform1 to floor
; Command : (time (ops-search test-state5 '((on floor agent)(at floor agent)) ops :world world1))
; Steps taken : 2
; Times : 4.1897 msecs, 4.2518 msecs, 4.5426 msecs, 4.2915 msecs, 4.1294 msecs
; Average Time : 4.281 msecs

; Conclusion : Moving from a platform to the floor is slightly more efficient than moving from the floor to a platform.
; This could be due to the state definition order, having the platform state above the floor state.
; It could also be down to the :pre order on the different operators used.
; =============================================================
; ================ TESTING : State Definition Order ===========
; Order1 : Platform state first in list
; Command : (time (ops-search test-state5 '((on floor agent)(at floor agent)) ops :world world1))
; Steps taken : 2
; Times : 4.2999 msecs, 4.2313 msecs, 4.2771 msecs,  4.3368 msecs, 4.3242 msecs
; Average Time : 4.29386 msecs

; Order2 : Floor state first in list
; Command : (time (ops-search test-state6 '((on floor agent)(at floor agent)) ops :world world1))
; Steps taken : 2
; Times : 4.2423 msecs, 4.3856 msecs, 4.2321 msecs, 4.1201 msecs, 4.2657 msecs
; Average Time : 4.24916 msecs

; Conclusion : The order of the state definitions, has little to no effect on the efficiency of the search.
; =============================================================
; ================ TESTING : Operator :pre Order ===========
; Order1 : Most defining :pre first
; Command : (time (ops-search test-state5 '((on floor agent)(at floor agent)) test-ops1 :world world1))
; Steps taken : 2
; Times : 2.2464 msecs, 2.3298 msecs, 2.3501 msecs, 2.3949 msecs,  2.337 msecs
; Average Time : 2.33164

; Order2 : Least defining :pre first
; Command : (time (ops-search test-state5 '((on floor agent)(at floor agent)) test-ops2 :world world1))
; Steps taken : 2
; Times : 2.4606 msecs, 2.4524 msecs, 2.5622 msecs, 2.4532 msecs, 2.5372 msecs,
; Average Time : 2.49312msecs

; Conclusion : The order of the :pre conditions in an operator, slightly affect the timings.
; By ordering the :pre with the most defining state first, the search becomes slighlty more efficient.
; =============================================================

; Agent on floor, box on platform1
(def test-state1
  '#{(can-climb platform1)
     (holds nil agent)
     (at floor agent)
     (on floor agent)
     (on floor platform1)
     (on platform1 box)})

; Agent on floor, box and bag on platform1
(def test-state2
  '#{(can-climb platform1)
     (can-climb platform2)
     (holds nil agent)
     (at floor agent)
     (on floor agent)
     (on floor platform1)
     (on floor platform2)
     (on platform1 box)
     (on platform1 bag)})

; Agent on floor, box, bag and barrel on platform1
(def test-state3
  '#{(can-climb platform1)
     (can-climb platform2)
     (can-climb platform3)
     (holds nil agent)
     (at floor agent)
     (on floor agent)
     (on floor platform1)
     (on floor platform2)
     (on floor platform3)
     (on platform1 box)
     (on platform1 bag)
     (on platform1 barrel)})

; Agent and box on floor
(def test-state4
  '#{(can-climb platform1)
     (holds nil agent)
     (at floor agent)
     (on floor agent)
     (on floor platform1)
     (on floor box)})

; Agent and box on platform1
(def test-state5
  '#{(has-climbed platform1)
     (on-top platform1 agent)
     (holds nil agent)
     (at platform1 agent)
     (on floor platform1)
     (on platform1 box)})

; Agent and box on platform1
(def test-state6
  '#{(on platform1 box)
     (on floor platform1)
     (at platform1 agent)
     (holds nil agent)
     (on-top platform1 agent)
     (has-climbed platform1)})

(def test-ops1
  '{move-across-floor
    {:pre ((on floor ?agent)
            (at ?pos1 ?agent)
            (on floor ?pos2)
            (position ?pos2)
            (Agent ?agent))
     :del ((at ?pos1 ?agent))
     :add ((at ?pos2 ?agent))
     :txt (?agent moved from ?pos1 to ?pos2)
     }

    climb-on
    {:pre ((can-climb ?pos)
            (at ?pos ?agent)
            (on floor ?agent)
            (Agent ?agent))
     :del ((at ?pos ?agent)
            (can-climb ?pos)
            (on floor ?agent))
     :add ((on-top ?pos ?agent)
            (has-climbed ?pos))
     :txt (?agent climbed ?pos)
     }

    climb-off
    {:pre ((has-climbed ?pos)
            (on-top ?pos ?agent)
            (Agent ?agent))
     :del ((on-top ?pos ?agent)
            (climbed ?pos)
            (on ?pos ?agent))
     :add ((on floor ?agent)
            (at ?pos ?agent)
            (can-climb ?pos))
     :txt (?agent climbed-off ?pos)
     }
    }
  )


(def test-ops2
  '{move-across-floor
    {:pre ((Agent agent)
            (position ?pos2)
            (on floor ?pos2)
            (at ?pos1 ?agent)
            (on floor ?agent))
     :del ((at ?pos1 ?agent))
     :add ((at ?pos2 ?agent))
     :txt (?agent moved from ?pos1 to ?pos2)
     }

    climb-on
    {:pre ((Agent ?agent)
            (on floor ?agent)
            (at ?pos ?agent)
            (can-climb ?pos))
     :del ((at ?pos ?agent)
            (can-climb ?pos)
            (on floor ?agent))
     :add ((on-top ?pos ?agent)
            (has-climbed ?pos))
     :txt (?agent climbed ?pos)
     }

    climb-off
    {:pre ((Agent ?agent)
            (on-top ?pos ?agent)
            (has-climbed ?pos))
     :del ((on-top ?pos ?agent)
            (climbed ?pos)
            (on ?pos ?agent))
     :add ((on floor ?agent)
            (at ?pos ?agent)
            (can-climb ?pos))
     :txt (?agent climbed-off ?pos)
     }
    }
  )


