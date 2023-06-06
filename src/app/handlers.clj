(ns app.handlers
  (:require [app.projects.database :as database]))

(defn create-project [{project :body-params}]
  (let [id (str (java.util.UUID/randomUUID))
        projects (->> (assoc project :id id)
                      (swap! database/projects assoc id))]
    {:status 200
     :body projects}))

(defn get-projects [_]
  {:status 200
   :body @database/projects})

(defn get-project-by-id
  [{{id :id} :path-params}]
  {:status 200
   :body (get @database/projects id)})