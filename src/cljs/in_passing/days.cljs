(ns in-passing.days
  (:require [clojure.test :refer [deftest is]]))

(defn day->coords [d]
  (let [idx (dec d)]
    [(mod idx 7) (quot idx 7)]))

(defn coords->day [[x y]]
  (+ (* y 7) x 1))

(deftest days-to-coords
  (is (= [0 0] (day->coords 1)))
  (is (= [1 0] (day->coords 2)))
  (is (= [0 1] (day->coords 8)))
  (is (= [0 3] (day->coords 22)))
  (is (= [6 3] (day->coords 28)))
  (is (= [2 4] (day->coords 31))))

(deftest coords-to-days
  (is (= (coords->day [0 0]) 1))
  (is (= (coords->day [1 0]) 2))
  (is (= (coords->day [0 1]) 8))
  (is (= (coords->day [0 3]) 22))
  (is (= (coords->day [6 3]) 28))
  (is (= (coords->day [2 4]) 31)))

(defn month->length [month]
  (case month
    :jan 31
    :feb 29
    :mar 31
    :apr 30
    :may 31
    :jun 30
    :jul 31
    :aug 31
    :sep 30
    :oct 31
    :nov 30
    :dec 31))

;; day offset 

(defn month->first-day-offset [month]
  (case month
    :jan 2
    :feb 5
    :mar 6
    :apr 2
    :may 4
    :jun 0
    :jul 2
    :aug 5
    :sep 1
    :oct 3
    :nov 6
    :dec 1))

(defn gen-days [length]
  (map inc (range length)))

(defn days->weeks [days]
  (partition 7 7 [] days))


(defn gen-month [month]
  (let [length (month->length month)
        first-day-offset (month->first-day-offset month)
        days (gen-days length)
        days-with-offset (concat  (take first-day-offset (repeat :blank)) days)]
    days-with-offset))

