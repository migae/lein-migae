(ns leiningen.migae.gserver
  "gserver - run the local official GAE DevAppServer in a repl"
  (:import com.google.appengine.tools.development.DevAppServerMain)
           ;; [com.google.apphosting.api ApiProxy ApiProxy$Environment])
  (:require [leiningen.core [eval :as eval] [classpath :as cp]]))
  ;;           [stencil.core :as stencil]
  ;;           [leiningen.classpath :as cp]
  ;;           [leiningen.new.templates :as tmpl]
  ;;           [leiningen.core [eval :as eval] [main :as main]]
  ;;           [clojure.string :as string]))

(defn gserver [project]
  (do (println "launching GAE DevAppServer")
      (eval/eval-in-project project
                            ;; (System/setProperty "--enable_all_permissions"
                            ;;                     true)
                       ;;      (System/setProperty "appengine.sdk.root"
                       ;;                     "/usr/local/java/appengine")
                       ;; (println (cp/get-classpath project))
                       ;; (ApiProxy/setEnvironmentForCurrentThread
                       ;;  (make-thread-environment-proxy)))))
                       ;; ["-javaagent:/usr/local/java/appengine/lib/agent/appengine-agent.jar"
                       ;; "-Xbootclasspath/p:war/WEB-INF/lib/appengine-dev-jdk-overrides.jar"
                       ;; "-Ddatastore.auto_id_allocation_policy=scattered"
                       ;; "-Dappengine.sdk.root=/usr/local/java/appengine"
                       ;; "-D--property=kickstart.user.dir=migae"
                       ;; "-D--enable_all_permissions=true"
                       ;; "-Djava.awt.headless=true"]
                       (DevAppServerMain/main
                        (into-array String
                                    [;;"--address=localhost"
                                     ;;"--port=8082"
                                     ;; (str "--sdk_root=" sdk_root)
                                     "--sdk_root=/usr/local/java/appengine"
                                     "--disable_update_check"
                                     "--generated_dir=tmp"
                                     "--property=kickstart.user.dir=test"
                                     (:war (:gae-app project))])))))
