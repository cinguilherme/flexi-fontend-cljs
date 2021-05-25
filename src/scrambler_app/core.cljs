(ns scrambler-app.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as d]))

;; Data
(def scrambleData (r/atom {:sourc "abc" :targ "acd" :scramble? false}))

;; -------------------------
;; Views

(defn title-component []
  [:h2 {:class "col display-4"} "Welcome to Scrambler App"])

(defn scramble-form []
  [:div {:class "col-md-12"}

   [:input {:type "text" :placeholder "string to be used" :class "form-control"} ] 
   [:input {:type "text" :placeholder "string to be looked for" :class "form-control"} ] 
   [:input {:type "button" :title "request" :value "Request Scrable Test"  :class "form-control"} ]])

(defn checked [data]
  (if (= true (:scramble? data)) "possible" "impossible"))

(defn results-presenter [data]
  [:div {:class "col-md-12"}
    [:p (str "results of requested scramble test: " 
          (:targ data) " " (:sourc data) 
          " and it is " (checked data) " to scramble." ) ]])

(defn home-page []
  [:div {:class "jumbotron vertial-center"}
   [:div {:class "container"}
    [:div {:class "row"}
     [:div {:class "col-md-10" :style {:align "center" :padding "10px" :margin "10px"}}
        [:br]
        (title-component)
        [:br]
        (scramble-form)]
        [:br]
        (results-presenter @scrambleData)]]])



;; -------------------------
;; Initialize app


(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
