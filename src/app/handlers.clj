(ns app.handlers
  (:require [app.projects.database :as database]))

(comment (defn create-project [{project :body-params}]
           (database/create-project (let [title (str (java.util.UUID/randomUUID))]( project :title title)))
           {:status 201}))
(defn create-project [{project :body-params}]
  (database/create-project (assoc project :title (:title project)))
  {:status 201})

(defn get-projects [_]
  {:status 200
   :body   (database/all-projects)})


(defn get-project-by-title
  [{{title :title} :path-params}]
  {:status 200
   :body   (database/get-project-by-title title)})


(comment (defn create-project [{project :body-params}]
           (let [id (str (java.util.UUID/randomUUID))
                 result (->> (assoc project :title title)
                             (database/create-project))]
             {:status 201}))
         )