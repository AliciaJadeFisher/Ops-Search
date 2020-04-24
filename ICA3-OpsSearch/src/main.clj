(ns main)
(require '[ops-search :as search])

; ================ WORLD DEFINITIONS ================
; World definition for a scenario with one platform and one object, which do not change throughout execution
(def world1
  '#{(Agent agent)
     (object box)
     (grabbable box)
     (position floor)
     (position base)
     (position platform1)
     (on floor base)
     })

; World definition for a scenario with two platforms and two objects, which do not change throughout execution
(def world2
  '#{(Agent agent)
     (object box)
     (object bag)
     (grabbable box)
     (grabbable bag)
     (position floor)
     (position base)
     (position platform1)
     (position platform2)
     (on floor base)
     })
; World definition for a scenario with three platforms and three objects, which do not change throughout execution
(def world3
  '#{(Agent agent)
     (object box)
     (object bag)
     (object barrel)
     (grabbable box)
     (grabbable bag)
     (grabbable barrel)
     (position floor)
     (position base)
     (position platform1)
     (position platform2)
     (position platform3)
     (on floor base)
     })
; ================ STATE DEFINITIONS ================
; For world1 : facts that will change due to operators
; Agent and box on floor
; (ops-search state1 '((holds box agent)) ops :world world1)
(def state1
  '#{(can-climb platform1)
     (holds nil agent)
     (at floor agent)
     (on floor agent)
     (on floor platform1)
     (on floor box)})

; Agent on floor, box on platform1
; (ops-search state2 '((on floor box)) ops :world world1)
(def state2
  '#{(can-climb platform1)
     (holds nil agent)
     (at floor agent)
     (on floor agent)
     (on floor platform1)
     (on platform1 box)})

; Agent on platform1, box on floor
; (ops-search state3 '((on platform1 box)) ops :world world1)
(def state3
  '#{(has-climbed platform1)
     (holds nil agent)
     (at platform1 agent)
     (on-top platform1 agent)
     (on floor platform1)
     (on floor box)})

; For world2 : facts that will change due to operators
; Agent, box and bag on floor
; (ops-search state4 '((on platform1 box) (on platform2 bag)) ops :world world2)
(def state4
  '#{(can-climb platform1)
     (can-climb platform2)
     (holds nil agent)
     (at floor agent)
     (on floor agent)
     (on floor platform1)
     (on floor platform2)
     (on floor box)
     (on floor bag)})

; Agent on platform2, box and bag on platform1
; (ops-search state5 '((on platform2 box) (on platform2 bag)) ops :world world2)
(def state5
  '#{(can-climb platform1)
     (has-climbed platform2)
     (holds nil agent)
     (at platform2 agent)
     (on-top platform2 agent)
     (on floor platform1)
     (on floor platform2)
     (on platform1 box)
     (on platform1 bag)})

; Agent on platform1, box and bag on platform2
; (ops-search state6 '((on floor box) (on floor bag)) ops :world world2)
(def state6
  '#{(has-climbed platform1)
     (can-climb platform2)
     (holds nil agent)
     (at platform1 agent)
     (on-top platform1 agent)
     (on floor platform1)
     (on floor platform2)
     (on platform2 box)
     (on platform2 bag)})

; For world3 : facts that will change due to operators
; Agent, box, bag and barrel on floor
; (ops-search state7 '((on platform1 box) (on platform2 bag) (on platform3 barrel)) ops :world world3)
(def state7
  '#{(can-climb platform1)
     (can-climb platform2)
     (can-climb platform3)
     (holds nil agent)
     (at floor agent)
     (on floor agent)
     (on floor platform1)
     (on floor platform2)
     (on floor platform3)
     (on floor box)
     (on floor bag)
     (on floor barrel)})

; For world3 : facts that will change due to operators
; Agent on floor, box on platform1, bag on platform2 and barrel on platform3
; (ops-search state8 '((on floor box) (on floor bag) (on floor barrel)) ops :world world3)
(def state8
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
     (on platform2 bag)
     (on platform3 barrel)})

; For world3 : facts that will change due to operators
; Agent on platform1, and box, bag and barrel on platform3
; (ops-search state9 '((on platform1 box) (on platform1 bag) (on platform2 barrel)) ops :world world3)
(def state9
  '#{(has-climbed platform1)
     (can-climb platform2)
     (can-climb platform3)
     (holds nil agent)
     (at platform1 agent)
     (on-top platform1 agent)
     (on floor platform1)
     (on floor platform2)
     (on floor platform3)
     (on platform3 box)
     (on platform3 bag)
     (on platform3 barrel)})

; ================ OPS DEFINITIONS ================
; Set of operators
; move-across-floor : Allows an agent to move to platforms on the floor
; move-to-an-object : Allows an agent to move to objects on the floor
; climb-on : Allows an agent climb onto a platform
; climb-off : Allows an agent to climb off of a platform
; pickup-off-floor : Allows an agent to pick up an object off the floor
; pickup-off-platform : Allows an agent to pick up an object off a platform that they are on
; drop-on-floor : Allows an agent to drop an object on the floor
; drop-on-platform : Allows an agent to drop on object onto the platform that they are on
(def ops
  '{move-across-floor
    {:pre ((Agent ?agent)
           (on floor ?agent)
           (position ?pos2)
           (on floor ?pos2)
           (at ?pos1 ?agent)
           )
     :del ((at ?pos1 ?agent))
     :add ((at ?pos2 ?agent))
     :txt (?agent moved from ?pos1 to ?pos2)
     }

    move-to-an-object
    {:pre ((Agent ?agent)
           (on floor ?agent)
           (object ?obj)
           (on floor ?obj)
           (at ?pos1 ?agent))
     :del ((at ?pos1 ?agent))
     :add ((at ?obj ?agent))
     :txt (?agent moved towards ?obj)
     }

    climb-on
    {:pre ((Agent ?agent)
           (at ?pos ?agent)
           (can-climb ?pos)
           (on floor ?agent))
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

    pickup-off-floor
    {:pre ((Agent ?agent)
           (holds nil ?agent)
           (on ?pos ?agent)
           (at ?obj ?agent)
           (on ?pos ?obj)
           (grabbable ?obj))
     :del ((on ?pos ?obj)
           (holds nil ?agent))
     :add ((holds ?obj ?agent))
     :txt (?agent picked-up ?obj from ?pos)
     }

    pickup-off-platform
    {:pre ((Agent ?agent)
           (holds nil ?agent)
           (on-top ?pos ?agent)
           (on ?pos ?obj)
           (grabbable ?obj))
     :del ((holds nil ?agent)
           (on ?pos ?obj))
     :add ((holds ?obj ?agent))
     :txt (?agent picked-off ?obj from ?pos)
     }

    drop-on-floor
    {:pre ((Agent ?agent)
           (on floor ?agent)
           (holds ?obj ?agent)
           (:not (holds nil ?agent)))
     :del ((holds ?obj ?agent))
     :add ((holds nil ?agent)
           (on floor ?obj))
     :txt (?agent dropped ?obj onto floor)
     }

    drop-on-platform
    {:pre ((Agent ?agent)
           (on-top ?pos ?agent)
           (holds ?obj ?agent)
           (:not (holds nil ?agent)))
     :del ((holds ?obj ?agent))
     :add ((holds nil ?agent)
           (on ?pos ?obj))
     :txt (?agent dropped ?obj onto ?pos)
     }})
