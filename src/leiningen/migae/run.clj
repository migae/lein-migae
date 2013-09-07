(ns leiningen.migae.run
  "run the local official GAE DevAppServer"
  (:require [leiningen.core [eval :as eval] [classpath :as cp]]
            [clojure.java.shell :as shell]))

(defn run [project]
  (let [sdkdir (:sdk (:migae project))
        cmd (str sdkdir "/bin/dev_appserver.sh"
                 " "
                 (:war (:migae project))
                 " &>"
                 (:devlog (:migae project))
                 ;;" &"
                 )]

    (do
      (println "launching GAE DevAppServer")
;      (print
       (shell/sh "sh" "-c"  cmd))))
