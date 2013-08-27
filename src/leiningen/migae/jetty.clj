(ns leiningen.migae.jetty
  "jetty - launch jetty-runner"
  (:require [clojure.java.shell :as sh]
            [clojure.string :as str]
            [clojure.contrib.java-utils :as utils]))
;            [leiningen.core [eval :as eval] [classpath :as cp]]))
  ;;           [stencil.core :as stencil]
  ;;           [leiningen.classpath :as cp]
  ;;           [leiningen.new.templates :as tmpl]
  ;;           [leiningen.core [eval :as eval] [main :as main]]
;;           [clojure.string :as string]))


;; TODO:  add subcommands:  start|stop|restart

(defn jetty [project]
  (let [home (utils/get-system-property "user.home")
        clj "/.m2/repository/org/clojure/clojure/1.5.1/clojure-1.5.1.jar"
        jetty-runner  "/.m2/repository/org/eclipse/jetty/jetty-runner/9.0.5.v20130815/jetty-runner-9.0.5.v20130815.jar"
        jetty-deploy "/.m2/repository/org/eclipse/jetty/jetty-deploy/8.0.0.RC0/jetty-deploy-8.0.0.RC0.jar"]
    (do (println "launching jetty-runner")
        ;;      (eval/eval-in-project project
        (println (sh/sh "sh" "-c"
                        (str/join " " ["java -jar"
                                       (str home jetty-runner)
                                       "--out" "jetty.err.log"
                                       "--log" "jetty.rqst.log"
                                       "--classes" "src/"
                                       "--jar" (str home clj)
                                       "--jar" (str home jetty-deploy)
                                       ;; "--config jetty.xml"
                                       "war"
                                       "1>jetty.err.log 2>&1 &"]))))))
