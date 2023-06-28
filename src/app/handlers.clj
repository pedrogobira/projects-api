(ns app.handlers
  (:require [app.projects.database :as database]))

(defn create-project [{project :body-params}]
  (database/create-project (let [id (str (java.util.UUID/randomUUID))] (assoc project :id id)))
  {:status 201})

(defn get-projects [_]
  {:status 200
   :body   (database/all-projects)})


(defn get-project-by-id
  [{{id :id} :path-params}]
  {:status 200
   :body   (database/get-project-by-id id)})

(comment (defn create-project [{project :body-params}]
           (let [id (str (java.util.UUID/randomUUID))
                 result (->> (assoc project :id id)
                             (database/create-project))]
             {:status 201}))
         )