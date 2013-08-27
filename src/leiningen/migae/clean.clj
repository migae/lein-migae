(ns leiningen.migae.clean
  "deploy - a migae subtask for cleaning a gae app.  lein clean only
cleans the target-path (i.e. jar dirs).  This one also cleans classes
dirs."
  (:require [clojure.java.io :as io]
            [stencil.core :as stencil]
            [leiningen.classpath :as cp]
            [leiningen.new.templates :as tmpl]
            [leiningen.core [eval :as eval] [main :as main]]
            [clojure.string :as string]))

(defn clean [& args]
  (println "migae cleaning...someday"))
