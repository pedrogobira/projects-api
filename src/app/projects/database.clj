(ns app.projects.database
  (:require [datomic.api :as d]))

(def db-uri "datomic:mem://foo")

(comment
  (d/create-database db-uri)
  (def conn (d/connect db-uri)))

(def project-schema [{:db/ident :project/id
                      :db/valueType :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc "The id of the project"}

                     {:db/ident :project/title
                      :db/valueType :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc "The title of the project"}

                     {:db/ident :project/people-quantity
                      :db/valueType :db.type/long
                      :db/cardinality :db.cardinality/one
                      :db/doc "The quantity of people involved in the project"}])

(comment
  @(d/transact conn project-schema))

(def first-projects [{:project/id (str (java.util.UUID/randomUUID))
                      :project/title "Project 1"
                      :project/people-quantity 10}
                     {:project/id (str (java.util.UUID/randomUUID))
                      :project/title "Project 2"
                      :project/people-quantity 15}
                     {:project/id (str (java.util.UUID/randomUUID))
                      :project/title "Project 3"
                      :project/people-quantity 20}])

(comment
  @(d/transact conn first-projects))


(def all-titles-q '[:find ?project-title
                    :where
                    [_ :project/title ?project-title]])

(comment
  (d/q all-titles-q (d/db conn)))


(def projects (atom {}))
