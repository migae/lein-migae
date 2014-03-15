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

(defn jetty [project & args]
  (let [home (utils/get-system-property "user.home")
        jardir (System/getenv "JARDIR")
        clj (str jardir "/clojure.jar")
        jetty-runner (str jardir  "/jetty-runner.jar")
        cmd (str/join " " ["java -jar"
                           jetty-runner
                           "--out" "jetty.err.log"
                           "--log" "jetty.rqst.log"
                           "--classes" "src/"
                           "--jar" clj
                           ;; "--config jetty.xml"
                           "--stop-port" "8123"
                           "--stop-key" "migae"
                           "war"
                           "1>jetty.err.log 2>&1 &"])]
    (do (println "launching jetty-runner")
        ;;      (eval/eval-in-project project
        (println (sh/sh "sh" "-cv" cmd)))))

    ;;         war \
    ;;         1>jetty.err.log 2>&1 &
    ;;     ;;
    ;; stop)
    ;;     (echo "migae"; echo "stop"; sleep 1;)| telnet localhost 8123
    ;;     ;;
    ;; help)

