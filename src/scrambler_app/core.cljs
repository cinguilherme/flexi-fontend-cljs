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

;; -------------------------
;; Views

(defn scram-form [] 
  (let [firstInpStr (r/atom "") secondInpStr (r/atom "")]
    (fn [] 
      [:form {:on-submit (fn [e] 
        (.preventDefault e)
        (log firstInpStr secondInpStr)
      
        (swap! scrambleData conj {:sourc @firstInpStr :targ @secondInpStr})

        (go (let [response (<! (http/get "http://localhost:8080"
          {:with-credentials? false
           :headers {"Content-Type" "application/json" }
           :query-params {"str1" firstInpStr "str2" secondInpStr}}))]
            (prn (:status response))
            (swap! scrambleData conj {:scramble? (:isScramblable? (:body response))} )))

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
        [scram-form]

        (results-presenter @scrambleData)]]]])


;; -------------------------
;; Initialize app


(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
