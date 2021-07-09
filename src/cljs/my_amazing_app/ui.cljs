(ns my-amazing-app.ui
  (:require
   [herb.core :refer [<class]]))

(defn px [v] (str v "px"))

(def font-css
  {:font-family "monospace"})

(defn button-css []
  (merge
   {:border "1px solid #ccc"
    :background "#eee"
    :padding "4px 8px"
    :vertical-align "middle"
    :border-radius "4px"}
   font-css))

(defn button [{:keys [label on-click disabled]}]
  [:button {:on-click on-click
            :disabled disabled
            :class (<class button-css)} label])

(defn label-css []
  font-css)

(defn label [txt]
  [:label {:class (<class label-css)} txt])

(defn p [txt]
  [:p {:class (<class label-css)} txt])

(defn input-field-css [valid disabled]
  (merge
   {:border "1px solid #ccc"
    :background (cond
                  disabled "#eee"
                  valid "white"
                  :else "#FF9999")

    :padding "4px 8px"
    :border-radius "4px"}
   font-css))

(defn select-field-css [width]
  (merge (input-field-css true false)
         {:width width}))

(defn input-field [{:keys [placeholder value on-change on-key-down type valid disabled]}]
  [:input {:class (<class input-field-css valid disabled)
           :type type
           :placeholder placeholder
           :disabled disabled
           :value value
           :on-change on-change
           :on-key-down on-key-down}])

(defn select-field [{:keys [value options width on-change size]}]
  [:select {:class (<class select-field-css width)
            :size size
            :value value
            :on-change on-change}
   (reverse (into () options))])

(defn popover-css [x y]
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
  [:div {:class (<class popover-css x y)
         :on-click #(.stopPropagation %)}
   content])