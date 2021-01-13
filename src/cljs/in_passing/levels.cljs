(ns in-passing.levels)

(def levels {:jan {:pieces  {0 [:king "Appt/ Dr. King" :active]
                             1 [:pawn "Work" :taken]
                             2 [:pawn "Work" :active]
                             3 [:pawn "Work" :active]
                             4 [:pawn "Work" :active]}
                   :events  {7 [0 1]
                             9 [2]
                             16 [3]
                             23 [4]}}
             :feb {:pieces  {0 [:king "Appt/ Dr. King" :active]
                             2 [:pawn "Work" :active]
                             3 [:pawn "Work" :active]}
                   :events  {11 [0]
                             18 [2]
                             25 [3]}}})

(def levels-2 {:jan [{:event/day 7
                      :event/piece :king
                      :event/name "Appt/ Dr. King"
                      :event/status :active}
                     {:event/day 7
                      :event/piece :pawn
                      :event/name "Work"
                      :event/status :taken}

                     {:event/day 9
                      :event/piece :pawn
                      :event/name "Work"
                      :event/status :active}

                     {:event/day 16
                      :event/piece :pawn
                      :event/name "Work"
                      :event/status :active}

                     {:event/day 23
                      :event/piece :pawn
                      :event/name "Work"
                      :event/status :active}]
               :feb [{:event/day 11
                      :event/piece :king
                      :event/name "Appt/ Dr. King"
                      :event/status :active}
                     {:event/day 18
                      :event/piece :pawn
                      :event/name "Work"
                      :event/status :active}
                     {:event/day 25
                      :event/piece :pawn
                      :event/name "Work"
                      :event/status :active}]})