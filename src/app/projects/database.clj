(ns app.projects.database
  (:require [datomic.api :as d]
            [clojure.java.io :as io]))

(def db-uri "datomic:mem://foo")

(d/create-database db-uri)
(def conn (d/connect db-uri))

; id_entidade atributo valor
; 15 :project/title  projeto inovar     ID_TX     operacao
; 15 :project/people-quantity  30    ID_TX     operacao
; 17 :project/title  projeto ESG     ID_TX     operacao
; 17 :project/people-quantity  10    ID_TX     operacao
(def project-schema [{:db/ident       :project/title
                      :db/valueType   :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc         "The title of the project"}

                     {:db/ident       :project/people-quantity
                      :db/valueType   :db.type/long
                      :db/cardinality :db.cardinality/one
                      :db/doc         "The quantity of people involved in the project"}])

@(d/transact conn project-schema)

(def first-projects [{:project/title           "Project 1"
                               :project/people-quantity 10}
                              {:project/title           "Project 2"
                               :project/people-quantity 15}
                              {:project/title           "Project 3"
                               :project/people-quantity 20}])

(defn parse-projects [results]
  (apply concat
         (map (fn [[id title quantity]]
                [{:id id :title title :quantity quantity}])
              results)))

(defn all-projects [] (let [query '[:find ?p ?title ?quantity
                                    :where
                                    [?p :project/title ?title]
                                    [?p :project/people-quantity ?quantity]]]
                        (parse-projects (d/q query (d/db conn)))
                        ))

(defn create-project [project]
  (let [new {:project/title           (:title project)
             :project/people-quantity (:quantity project)}]
    @(d/transact conn [new])
    ))

(defn get-project-by-title [title]

  (let [query '[:find ?p ?title ?people-quantity
                :in $ ?title
                :where
                [?p :project/title ?title]
                [?p :project/people-quantity ?people-quantity]]
        result (d/q query (d/db conn) title)]
    (parse-projects result)
    ))

(defn get-project-by-id [id]
  (let [query '[:find ?id ?title ?people-quantity
                :in $ ?id
                :where
                [?id :project/title ?title]
                [?id :project/people-quantity ?people-quantity]]
        result (d/q query (d/db conn) id)]
    (parse-projects result)
    ))

(defn get-title-by-id [id]
  (let [query '[:find [?title]
                :in $ ?id
                :where
                [?id :project/title ?title]]
        result (d/q query (d/db conn) id)]
    (first result)))

(defn get-quantity-by-id [id]
  (let [query '[:find [?quantity]
                :in $ ?id
                :where
                [?id :project/people-quantity ?quantity]]
        result (d/q query (d/db conn) id)]
    (first result)))
(comment
  ; pull generico, vantagem preguica, desvantagem pode trazer mais do que eu queira
  (defn todos-os-projetos [db]
    (d/q '[:find (pull ?entidade [*])
           :where [?entidade :project/title]] db))

  @(d/transact conn first-projects)
  (println (todos-os-projetos (d/db conn)))
  (defn teste [id-entidade]
    @(d/transact conn [[:db/add id-entidade :project/title "inovar meu amor"]
                       [:db/add id-entidade :project/people-quantity 99]] ))
  (teste 17592186045418)
  (println (todos-os-projetos (d/db conn)))
  (defn delete-title-by-id [id title]
    @(d/transact conn [[:db/retract id :project/title title]] ))
  (defn delete-quantity-by-id [id quantity]
    @(d/transact conn [[:db/retract id :project/people-quantity quantity]] ))
  )
(defn update-quantity-by-id [id quantity]
  @(d/transact conn [[:db/add id :project/title quantity]] ))
(defn update-title-by-id [id title]
  @(d/transact conn [[:db/add id :project/title title]] ))

(d/transact conn first-projects)

(defn delete-project-by-id [id]
  @(d/transact conn [[:db/retract id :project/title (get-title-by-id id)]
                     [:db/retract id :project/people-quantity (get-quantity-by-id id)]])
  )
;(delete-project-by-id 17592186045418)
