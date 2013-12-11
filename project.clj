(defproject nethud "0.1.0-SNAPSHOT"
  :description "Send stuff to a thing that's on a thing"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main nethud.core
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [http-kit "2.1.13"]
                 [hiccup "1.0.4"]
                 [ring "1.2.1"]
                 [org.clojure/data.json "0.2.3"]
                 [compojure "1.1.6"]])
