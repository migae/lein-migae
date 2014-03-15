(ns leiningen.migae.version
  "print lein-migae version id."
  (:require [clojure.java.io :as io]
            [stencil.core :as stencil]
            [leiningen.classpath :as cp]
            [leiningen.new.templates :as tmpl]
            [leiningen.core [eval :as eval] [main :as main]]
            [clojure.string :as string]))

(defn version [& args]
  (println "lein-migae version 0.1.7"))

