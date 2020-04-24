(ns alternate-main)
(require '[ops-search :as search])

; ================ WORLD DEFINITIONS ================
; World definition for a scenario with two platforms and two objects, which do not change throughout execution
(def world
  '#{(Agent agent)
     (object box1)
     (object box2)
     (grabbable box1)
     (grabbable box2)
     (position floor)
     (position base)
     (position platform1)
     (position platform2)
     (grabbable platform1)
     (grabbable platform2)
     (on floor base)
     })

; ================ STATE DEFINITIONS ================
;(ops-search state '((on platform1 platform2)) ops :world world)
;(ops-search state '((on platform1 box1) (on-top box1 agent)) ops :world world)
;(ops-search state '((on box2 box1)) ops :world world)
(def state
  '#{(can-climb platform1)
     (can-climb platform2)
     (can-climb box1)
     (can-climb box2)
     (holds nil agent)
     (at base agent)
     (on floor agent)
     (on floor platform1)
     (on floor platform2)
     (on floor box1)
     (on floor box2)
     (clear platform1)
     (clear platform2)
     (clear box1)
     (clear box2)})

; (ops-search state2 '((on floor agent)) ops :world world)
(def state2
  '#{(has-climbed platform1)
     (has-climbed platform2)
     (can-climb box1)
     (can-climb box2)
     (holds nil agent)
     (on-top platform2 agent)
     (on floor platform1)
     (on platform1 platform2)
     (on floor box1)
     (on floor box2)
     (not-clear platform1)
     (clear platform2)
     (clear box1)
     (clear box2)})

; (ops-search state3 '((on floor box1)) ops :world world)
(def state3
  '#{(has-climbed platform1)
     (can-climb platform2)
     (has-climbed box1)
     (can-climb box2)
     (holds nil agent)
     (on-top box1 agent)
     (on floor platform1)
     (on floor platform2)
     (on platform1 box1)
     (on floor box2)
     (not-clear platform1)
     (clear platform2)
     (clear box1)
     (clear box2)})

;(ops-search state4 '((on floor platform2)) ops :world world)
(def state4
  '#{(can-climb platform1)
     (can-climb platform2)
     (can-climb box1)
     (can-climb box2)
     (holds nil agent)
     (at floor agent)
     (on floor agent)
     (on floor platform1)
     (on box1 platform2)
     (on floor box1)
     (on floor box2)
     (clear platform1)
     (clear platform2)
     (not-clear box1)
     (clear box2)})

;(ops-search state5 '((on platform1 box1)) ops :world world)
(def state5
  '#{(can-climb platform1)
     (can-climb platform2)
     (can-climb box1)
     (can-climb box2)
     (holds nil agent)
     (at floor agent)
     (on floor agent)
     (on floor platform1)
     (on floor platform2)
     (on floor box1)
     (on box1 box2)
     (clear platform1)
     (clear platform2)
     (not-clear box1)
     (clear box2)})

; ================ OPS DEFINITIONS ================
; Set of operators
; move-across-floor : Allows an agent to move to platforms on the floor
; move-to-an-object-on-floor : Allows an agent to move to objects on the floor
; move-to-an-object-on-platform : Allows an agent to move to objects on platforms
; climb-platform-from-floor : Allows an agent to climb onto a platform from the floor
; climb-platform-from-platform : Allows an agent to climb onto a platform from another platform
; climb-object-from-floor : Allows an agent to climb onto an object from the floor
; climb-object-from-platform : Allows an agent to climb onto an object from a platform
; climb-off-to-floor : Allows an agent to climb off of a platform or object to the floor
; climb-off-to-platform : Allows an agent to climb off of a platform or object to a platform
; pickup-object-off-floor : Allows an agent to pick up an object off the floor
; pickup-object-off-platform : Allows an agent to pick up an object off a platform that they are on
; pickup-platform-off-floor : Allows an agent to pick up an platform off the floor
; pickup-platform-off-platform : Allows an agent to pick up an platform off a platform that they are on
; pickup-off-object : Allows an agent to pick up an object or platform off of another object
; drop-on-floor : Allows an agent to drop an object or platform on the floor
; drop-on-platform : Allows an agent to drop on object or platform onto the platform that they are on
; drop-on-object : Allows an agent to drop an object or platform on another object

(def ops
  '{move-across-floor
    {:pre ((Agent ?agent)
           (on floor ?agent)
           (position ?pos2)
           (on floor ?pos2)
           (at ?pos1 ?agent))
     :del ((at ?pos1 ?agent))
     :add ((at ?pos2 ?agent))
     :txt (?agent moved to ?pos2)
     }

    move-to-an-object-on-floor
    {:pre ((Agent ?agent)
           (on floor ?agent)
           (object ?obj)
           (on floor ?obj)
           (at ?pos1 ?agent))
     :del ((at ?pos1 ?agent))
     :add ((at ?obj ?agent))
     :txt (?agent moved towards ?obj)
     }

    move-to-an-object-on-platform
    {:pre ((Agent ?agent)
           (on-top ?pos ?agent)
           (object ?obj)
           (on ?pos ?obj)
           (at ?pos1 ?agent))
     :del ((at ?pos1 ?agent))
     :add ((at ?obj ?agent))
     :txt (?agent moved towards ?obj)
     }

    climb-platform-from-floor
    {:pre ((Agent ?agent)
           (at ?pos ?agent)
           (can-climb ?pos)
           (on floor ?agent)
           (on floor ?pos))
     :del ((at ?pos ?agent)
           (can-climb ?pos)
           (on floor ?agent))
     :add ((on-top ?pos ?agent)
           (has-climbed ?pos))
     :txt (?agent climbed ?pos)
     }

    climb-platform-from-platform
    {:pre ((Agent ?agent)
           (on-top ?pos ?agent)
           (on ?pos ?pos2)
           (can-climb ?pos2))
     :del ((at ?pos ?agent)
           (can-climb ?pos)
           (on-top ?pos ?agent))
     :add ((on-top ?pos2 ?agent)
           (has-climbed ?pos2))
     :txt (?agent climbed ?pos2 from ?pos)
     }

    climb-object-from-floor
    {:pre ((Agent ?agent)
           (on floor ?agent)
           (at ?obj ?agent)
           (can-climb ?obj)
           (object ?obj)
           (on floor ?obj))
     :del ((can-climb ?obj)
           (on floor ?agent))
     :add ((on-top ?obj ?agent)
           (has-climbed ?obj))
     :txt (?agent climbed ?obj from floor)
     }

    climb-object-from-platform
    {:pre ((Agent ?agent)
           (on-top ?pos ?agent)
           (at ?obj ?agent)
           (can-climb ?obj)
           (object ?obj)
           (on ?pos ?obj))
     :del ((can-climb ?obj)
           (on-top ?pos ?agent))
     :add ((on-top ?obj ?agent)
           (has-climbed ?obj))
     :txt (?agent climbed ?obj from ?pos)
     }

    climb-off-to-floor
    {:pre ((Agent ?agent)
           (on-top ?pos ?agent)
           (has-climbed ?pos)
           (on floor ?pos))
     :del ((on-top ?pos ?agent)
           (has-climbed ?pos)
           (on ?pos ?agent))
     :add ((on floor ?agent)
           (at ?pos ?agent)
           (can-climb ?pos))
     :txt (?agent climbed-off ?pos)
     }

    climb-off-to-platform
    {:pre ((Agent ?agent)
           (on-top ?pos ?agent)
           (on ?pos2 ?pos)
           (has-climbed ?pos))
     :del ((on-top ?pos ?agent)
           (has-climbed ?pos)
           (on-top ?pos ?agent))
     :add ((on-top ?pos2 ?agent)
           (can-climb ?pos))
     :txt (?agent climbed-off ?pos to ?pos2)
     }

    pickup-object-off-floor
    {:pre ((Agent ?agent)
           (holds nil ?agent)
           (on ?pos ?agent)
           (at ?obj ?agent)
           (on ?pos ?obj)
           (grabbable ?obj)
           (clear ?obj))
     :del ((on ?pos ?obj)
           (holds nil ?agent))
     :add ((holds ?obj ?agent))
     :txt (?agent picked-up ?obj from ?pos)
     }

    pickup-object-off-platform
    {:pre ((Agent ?agent)
           (holds nil ?agent)
           (on-top ?pos ?agent)
           (at ?obj ?agent)
           (on ?pos ?obj)
           (grabbable ?obj)
           (clear ?obj))
     :del ((holds nil ?agent)
           (on ?pos ?obj)
           (not-clear ?pos))
     :add ((holds ?obj ?agent)
           (clear ?pos))
     :txt (?agent picked-off ?obj from ?pos)
     }

    pickup-platform-off-floor
    {:pre ((Agent ?agent)
           (holds nil ?agent)
           (on ?pos ?agent)
           (at ?plat ?agent)
           (on ?pos ?plat)
           (grabbable ?plat)
           (clear ?plat))
     :del ((on ?pos ?plat)
           (holds nil ?agent))
     :add ((holds ?plat ?agent))
     :txt (?agent picked up ?plat from ?pos)
     }

    pickup-platform-off-platform
    {:pre ((Agent ?agent)
           (holds nil ?agent)
           (on-top ?pos ?agent)
           (on ?pos ?plat)
           (grabbable ?plat)
           (clear ?plat))
     :del ((holds nil ?agent)
           (on ?pos ?plat)
           (not-clear ?pos))
     :add ((holds ?plat ?agent)
           (clear ?pos))
     :txt (?agent picked up ?plat from ?pos)
     }

    pickup-off-object
    {:pre ((Agent ?agent)
           (holds nil ?agent)
           (at ?obj ?agent)
           (on ?obj ?plat)
           (grabbable ?plat)
           (clear ?obj))
     :del ((holds nil ?agent)
           (on ?obj ?plat)
           (not-clear ?plat))
     :add ((holds ?plat ?agent)
           (clear ?plat))
     :txt (?agent picked up ?plat from ?obj)
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
           (clear ?pos)
           (:not (holds nil ?agent)))
     :del ((holds ?obj ?agent)
           (clear ?pos))
     :add ((holds nil ?agent)
           (on ?pos ?obj)
           (not-clear ?pos))
     :txt (?agent dropped ?obj onto ?pos)
     }

    drop-on-object
    {:pre ((Agent ?agent)
           (at ?obj ?agent)
           (on ?pos ?obj)
           (holds ?obj2 ?agent)
           (clear ?obj)
           (:not (holds nil ?agent)))
     :del ((holds ?obj2 ?agent)
           (clear ?obj))
     :add ((holds nil ?agent)
           (on ?obj ?obj2)
           (not-clear ?pos))
     :txt (?agent dropped ?obj2 onto ?obj)
     }
    })