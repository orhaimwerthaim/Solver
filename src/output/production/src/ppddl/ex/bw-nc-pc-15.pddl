;; Generated by blocksworld generator
;; http://www.cs.rutgers.edu/~jasmuth/blocksworld.tar.gz
;; by John Asmuth (jasmuth@cs.rutgers.edu)
;; and Dave Weissman

(define (domain bw-nc-pc-15)
 (:requirements :adl :probabilistic-effects :fluents :rewards)
 (:types block table)
 (:constants table - table)
 (:predicates
  (holding ?block - block)
  (on-top-of ?block - block ?obj)
 )
 (:action pick-up-block-from
  :parameters (?top - block ?bottom)
  :precondition (and (not (= ?top ?bottom))
                     (forall (?b - block) (not (holding ?b)))
                     (on-top-of ?top ?bottom)
                     (forall (?b - block) (not (on-top-of ?b ?top))))
  :effect                 (and (decrease (reward) 1)
               (probabilistic 0.75 (and (holding ?top)
                                        (not (on-top-of ?top ?bottom)))
                              0.25 (when (not (= ?bottom table))
                                         (and (not (on-top-of ?top ?bottom))
                                              (on-top-of ?top table)))))
 )
 (:action put-down-block-on
  :parameters (?top - block ?bottom)
  :precondition (and (not (= ?top ?bottom))
                     (holding ?top)
                     (or (= ?bottom table)
                         (forall (?b - block) (not (on-top-of ?b ?bottom)))))
  :effect                 (and (not (holding ?top))
               (probabilistic 0.75 (on-top-of ?top ?bottom)
                              0.25 (on-top-of ?top table)))
 )
)
(define (problem bw-nc-pc-15)
 (:domain bw-nc-pc-15)
 (:objects block0 block1 block2 block3 block4 block5 block6 block7 block8 block9 block10 block11 block12 block13 block14 - block)
 (:init
  (on-top-of block0 block1)
  (on-top-of block1 block2)
  (on-top-of block2 block3)
  (on-top-of block3 table)
  (on-top-of block4 block5)
  (on-top-of block5 block6)
  (on-top-of block6 table)
  (on-top-of block7 block8)
  (on-top-of block8 block9)
  (on-top-of block9 block10)
  (on-top-of block10 block11)
  (on-top-of block11 block12)
  (on-top-of block12 table)
  (on-top-of block13 table)
  (on-top-of block14 table)
 )
 (:goal
  (and
   (on-top-of block0 block6)
   (on-top-of block6 block4)
   (on-top-of block4 block10)
   (on-top-of block10 block2)
   (on-top-of block2 block13)
   (on-top-of block13 block9)
   (on-top-of block9 block11)
   (on-top-of block11 block1)
   (on-top-of block1 block8)
   (on-top-of block8 block14)
   (on-top-of block14 block3)
   (on-top-of block3 block7)
   (on-top-of block7 block5)
   (on-top-of block5 table)
   (on-top-of block12 table)

  )
 )
 (:goal-reward 500)
)
