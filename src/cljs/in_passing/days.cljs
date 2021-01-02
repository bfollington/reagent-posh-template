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

(defn gen-month [days]
  (partition 7 7 [] (map inc (range days))))

