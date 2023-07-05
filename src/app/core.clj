(ns app.core
  (:require [app.handlers :as handlers]
            [muuntaja.core :as m]
            [reitit.ring :as ring]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [ring.adapter.jetty :as ring-jetty])
  (:gen-class))

(def app
  (ring/ring-handler
   (ring/router
    ["/api/v1"
     ["/projects/:title" handlers/get-project-by-title]
     ["/projects" {:get handlers/get-projects
                   :post handlers/create-project}]]
    {:data {:muuntaja m/instance
            :middleware [muuntaja/format-middleware]}})))

(defn start []
  (ring-jetty/run-jetty app {:port  3000
                             :join? false}))

(defn -main
  [& args]
  (start))
