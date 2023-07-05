(ns app.projects.database
  (:require [datomic.api :as d]))

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

(comment (def first-projects [{:project/title           "Project 1"
                               :project/people-quantity 10}
                              {:project/title           "Project 2"
                               :project/people-quantity 15}
                              {:project/title           "Project 3"
                               :project/people-quantity 20}]))

(comment
  @(d/transact conn first-projects))

(defn parse-projects [results]
  (apply concat
         (map (fn [[title quantity]]
                [{:title title :quantity quantity}])
              results)))

(defn all-projects [] (let [query '[:find ?title ?quantity
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

  (let [query '[:find ?title ?people-quantity
                :in $ ?title
                :where
                [?p :project/title ?title]
                [?p :project/people-quantity ?people-quantity]]
        result (d/q query (d/db conn) title)]
    (parse-projects result)
    ))


; pull generico, vantagem preguica, desvantagem pode trazer mais do que eu queira
(defn todos-os-projetos [db]
  (d/q '[:find (pull ?entidade [*])
         :where [?entidade :project/title]] db))

;db/add passando a entidade , atributo e valor a ser adicionado/ou mudado.
;:db/add 17592186045419 :project/people-quantity 9