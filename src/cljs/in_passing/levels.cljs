(ns in-passing.levels)

(def levels {:jan {:pieces  {0 [:king "Appt/ Dr. King" :active]
                             1 [:pawn "Work" :taken]
                             2 [:pawn "Work" :active]
                             3 [:pawn "Work" :active]
                             4 [:pawn "Work" :active]}
                   :events  {7 [0 1]
                             9 [2]
                             16 [3]
                             23 [4]}}})
