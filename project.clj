(defproject app "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [javax.servlet/servlet-api "2.5"]
                 [ring "1.10.0"]
                 [metosin/reitit "0.7.0-alpha4"]
                 [metosin/muuntaja "0.6.8"]
                 [com.datomic/datomic-free "0.9.5697"]]
  :repl-options {:init-ns app.core}
  :main "app.core")
