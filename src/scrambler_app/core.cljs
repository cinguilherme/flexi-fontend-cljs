(ns scrambler-app.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as d])
   (:require-macros [cljs.core.async.macros :refer [go]])
   (:require [cljs-http.client :as http]
             [cljs.core.async :refer [<!]]))

;; helpers
(def log (.-log js/console))

;; Data
(def scrambleData (r/atom {:sourc "abc" :targ "acd" :scramble? false}))

;; Const
(def baseUrl "http://localhost:8080/?str1=%22 %22&str2=%22 %22")

;; -------------------------
;; Views

(defn scram-form [] 
  (let [firstInpStr (r/atom "") secondInpStr (r/atom "")]
    (fn [] 
      [:form {:on-submit (fn [e] 
        (.preventDefault e)
        (log firstInpStr secondInpStr)
        (go (let [response (<! (http/get "http://localhost:8080"
          {:with-credentials? false
           :query-params {"str1" firstInpStr "str2" secondInpStr}}))]
            (prn (:status response))
            (prn (:body response) )))

      )}
        [:input {:type "text" 
                  :value @firstInpStr 
                  :placeholder "first str here"
                  :on-change (fn [e] 
                            (reset! firstInpStr (.-value (.-target e)))) }]
        
        [:input {:type "text" :placeholder "second str here"
                  :value @secondInpStr
                  :on-change (fn [e] 
                            (reset! secondInpStr (.-value (.-target e))))}]
        
        [:input {:type "submit" :value "Request Scramble Check"}]
        ]
      )
    ))

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
        [title-component]
        
        [:br]
        [scramble-form]
        
        [:br]
        [scram-form]

        (results-presenter @scrambleData)]]]])


;; -------------------------
;; Initialize app


(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
