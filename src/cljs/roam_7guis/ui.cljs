(ns roam-7guis.ui
  (:require
   [herb.core :refer [<class]]))

(defn px [v] (str v "px"))

(def font-style
  {:font-family "monospace"})

(defn button-style []
  (merge
   {:border "1px solid #ccc"
    :background "#eee"
    :padding "4px 8px"
    :vertical-align "middle"
    :border-radius "4px"}
   font-style))

(defn button [{:keys [label on-click]}]
  [:button {:on-click on-click
            :class (<class button-style)} label])

(defn label-style []
  font-style)

(defn label [txt]
  [:label {:class (<class label-style)} txt])

(defn p [txt]
  [:p {:class (<class label-style)} txt])

(defn input-field-style [valid]
  (merge
   {:border "1px solid #ccc"
    :background (if valid "white" "#FF9999")
    :padding "4px 8px"
    :border-radius "4px"}
   font-style))

(defn select-field-style []
  (merge (input-field-style true)
         {:min-width "256px"}))

(defn input-field [{:keys [placeholder value on-change on-key-down type valid]}]
  [:input {:class (<class input-field-style valid)
           :type type
           :placeholder placeholder
           :value value
           :on-change on-change
           :on-key-down on-key-down}])

(defn select-field [{:keys [value options on-change size]}]
  [:select {:class (<class select-field-style)
            :size size
            :value value
            :on-change on-change}
   (reverse (into () options))])

(defn popover-style [x y]
  ^{:pseudo {::before {:content "" :width "8px" :height "8px" :background "red"}}}
  {:position "absolute"
   :top (px x)
   :left (px y)
   :background "white"
   :border "1px solid #ccc"
   :transform "translate(-50%, 24px)"
   :box-shadow "0px 10px 22px 0px rgba(0,0,0,0.25)"
   :padding "4px"
   :border-radius "3px"})

(defn popover [{:keys [x y content]}]
  [:div {:class (<class popover-style x y)
         :on-click #(.stopPropagation %)}
   content])