(ns nethud.core
  (:require compojure.handler
            compojure.route
            [clojure.data.json :as json]
            [clojure.java.io :as io])
  (:use org.httpkit.server
        compojure.core
        hiccup.core
        hiccup.page)
  (:gen-class))

(defonce channels (atom #{}))

(defn socket-handler
  [req]
  (println "Connected from:" req)
  (with-channel req ch
    (swap! channels conj ch)
    (on-close ch (fn [_]
                   (println "Closed channel.")
                   (swap! channels disj ch)))
    (on-receive ch (fn [data]
                     ;; echo
                     (send! ch data)))))

(defn broadcast
  [message]
  (doseq [ch @channels]
    (send! ch message)))

(defn home-page
  [req]
  (let [websocket-url (str "ws://" (get (:headers req) "host") "/socket")]
    (html5 [:h1 "Hello"] [:p "Socket URL: " websocket-url]
           [:div.messages [:p "Messages will appear here"]]
           [:script {:type "text/javascript"} "var config = " (json/write-str {:endpoint websocket-url}) ";"]
           [:script {:type "text/javascript" :src "/static/hud.js"}])))

(def app-routes
  (routes
   (GET "/socket" [] socket-handler)
   (POST "/" [& params] (do
                          (broadcast (json/write-str params))
                          "OK\n"))
   (GET "/" [] home-page)
   (compojure.route/resources "/static")))

(defn json?
  [req]
  (= "application/json" (get (:headers req) "content-type")))

(defn wrap-json-params
  [handler]
  (fn [req]
    (handler
     (if (json? req)
       (assoc req :params (json/read (io/reader (:body req)) :key-fn keyword))
       req))))

(def handler
  (-> app-routes
      wrap-json-params
      compojure.handler/site))

(defonce server-atom (atom nil))

(defn start
  [server port]
  (when server
    (server))
  (run-server (bound-fn [req] (handler req)) {:port port}))

(comment
  ;; to start the server:
  (swap! server-atom start 8080)
  )

(defn -main
  [port-str]
  (let [port (Integer. port-str)]
    (swap! server-atom start port)))
