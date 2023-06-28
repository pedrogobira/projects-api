(ns app.projects.database
  (:require [datomic.api :as d]))

(def db-uri "datomic:mem://foo")

(d/create-database db-uri)
(def conn (d/connect db-uri))

(def project-schema [{:db/ident       :project/id
                      :db/valueType   :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc         "The id of the project"}

                     {:db/ident       :project/title
                      :db/valueType   :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc         "The title of the project"}

                     {:db/ident       :project/people-quantity
                      :db/valueType   :db.type/long
                      :db/cardinality :db.cardinality/one
                      :db/doc         "The quantity of people involved in the project"}])

@(d/transact conn project-schema)

(comment (def first-projects [{:project/id              (str (java.util.UUID/randomUUID))
                               :project/title           "Project 1"
                               :project/people-quantity 10}
                              {:project/id              (str (java.util.UUID/randomUUID))
                               :project/title           "Project 2"
                               :project/people-quantity 15}
                              {:project/id              (str (java.util.UUID/randomUUID))
                               :project/title           "Project 3"
                               :project/people-quantity 20}]))

(comment
  @(d/transact conn first-projects))

(defn parse-projects [results]
  (apply concat
         (map (fn [[id title quantity]]
                [{:id id :title title :quantity quantity}])
              results)))

(defn all-projects [] (let [query '[:find ?id ?title ?quantity
                                    :where
                                    [?p :project/id ?id]
                                    [?p :project/title ?title]
                                    [?p :project/people-quantity ?quantity]]]
                        (parse-projects (d/q query (d/db conn)))
                        ))



(defn create-project [project]
  (let [new {:project/id              (:id project)
             :project/title           (:title project)
             :project/people-quantity (:quantity project)}]
    @(d/transact conn [new])
    ))

(defn get-project-by-id [id]
  (let [query '[:find ?id ?title ?people-quantity
                :in $ ?id
                :where
                [?p :project/id ?id]
                [?p :project/title ?title]
                [?p :project/people-quantity ?people-quantity]]
        result (d/q query (d/db conn) id)]
    (parse-projects result)
    ))
