(ns my-amazing-app.color)

(def palette
  {:maroon "#B18491"
   :red "#C692A2"
   :purple "#B298CC"
   :orange "#C6A688"
   :green "#A0C789"
   :light-blue "#83C9C5"
   :blue "#8386CA"})

(def ui-color (:maroon palette))

(defn piece->color [p]
  (case p
    :pawn :red
    :knight :purple
    :bishop :green
    :king :orange
    :queen :light-blue
    :castle :blue))

(defn piece->hex [p]
  (get palette (piece->color p)))
