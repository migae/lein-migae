(ns leiningen.migae.deploy
  "deploy - a migae subtask for deploying a gae app to the server (appcfg.sh update)"
  (:import com.google.appengine.tools.admin.AppCfg)
  (:use [leiningen.core.main :only [abort]])
  (:require [leiningen.jar :as jar]
            [leiningen.classpath :as cp]
                                        ;          [com.google.appengine/appengine-tools-api "1.7.4"]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [leiningen.core.eval :as eval]
            [leiningen.core.main :as main]
            [leiningen.new.templates :as tmpl]
            [stencil.core :as stencil]))

;; AppCfg will barf if run from the jar in ~/.m2
;; set appengine.sdk.root to prevent this
(defn deploy-impl [project phase]
  (let [foo (print (str "migae deploy " phase "..."))
        appengine-sdk (:sdk (:migae project))
        appengine-sdk (cond (nil? appengine-sdk)
                            (if-let [from-prof
                                     (:sdk
                                      (:migae
                                       (:profiles project)))]
                              from-prof
                              (if-let [from-env
                                       (System/getenv "APPENGINE_HOME")]
                                from-env
                                (abort
                                 (str "deploy "
                                      (str "no App Engine SDK specified\n"
                                           "(set :migae {:sdk \"path/to/sdk\"...} in project.clj,"
                                           " or :user profile in ~/.lein/profiles.clj, e.g. {:user {:migae {:sdk \"/usr/local/java/appengine/\"}}}"
                                           " or APPENGINE_HOME in the environment)")))))
                            (string? appengine-sdk) appengine-sdk
                            (map? appengine-sdk)
                            (let [username (System/getProperty "user.name")
                                  usersdk (get appengine-sdk username)]
                              (when (nil? usersdk)
                                (abort
                                 (format "no valid App Engine SDK directory defined for user %s"
                                         username)))
                              usersdk))
        appengine-sdk (let [appengine-sdk (io/as-file appengine-sdk)]
                        (when-not (.isDirectory appengine-sdk)
                          (abort (format "%s is not a valid App Engine SDK directory"
                                         appengine-sdk)))
                        appengine-sdk)]
    ;; version (if (not (nil? version))
    ;;           version ; just use the given version
    ;;           (let [versions (if (contains? project :appengine-app-versions)
    ;;                            (:appengine-app-versions project)
    ;;                            (abort (str task-name
    ;;                                             " requires :appengine-app-versions"
    ;;                                             " in project.clj")))]
    ;;             (cond
    ;;              ;; not a map
    ;;              (not (map? versions))
    ;;              (abort "bad format for :appengine-app-versions")
    ;;              ;; check the given app-name
    ;;              (not (contains? versions app-name))
    ;;              (abort (format ":appengine-app-versions does not contain %s"
    ;;                                  app-name))
    ;;              ;; looks fine now
    ;;              :else (versions app-name))))]
    (do
      (System/setProperty "appengine.sdk.root" (.getCanonicalPath appengine-sdk))
      ;; (.addShutdownHook (Runtime/getRuntime) (proxy [Thread] []
      ;;                                          (run [] (when (.exists out-appengine-web-xml)
      ;;                                                    (.delete out-appengine-web-xml)))))
      (if (= phase "dev")
        (println "ok")
         (let [path (.getCanonicalPath (io/as-file (:war (:migae project))))]
           (do
             ;; (println
             ;;  (format "running AppCfg using %s" appengine-sdk))
             (print
              (format "appengine.sdk.root %s" (System/getProperty
                                               "appengine.sdk.root")))
               ;; TODO: support args to AppCfg
               (AppCfg/main (into-array [ "--enable_jar_splitting"
                                          "--jar_splitting_excludes=clj"
                                          "--enable_jar_classes"
                                          "update"
                                          path]))))))))
                                    ;; "--retain_upload_dir"

(defn deploy
  "syntax: lein migae deploy [dev | beta | prod]"
  [project phase & args]
  (println phase)
  ;; (main/apply-task "bluuugh" project args)
  (main/apply-task "deps" project args)
  (main/apply-task ["cljsbuild" "clean"] project args)
  (main/apply-task "clean" project args)
  (main/apply-task ["migae" "clean"] project args)
  (main/apply-task ["migae" "config" phase] project args)
  (main/apply-task "compile" project args)
  (if (not= phase "dev")
    (main/apply-task "jar" project args))
  (deploy-impl project phase))
  ;; (eval/eval-in-project
  ;;  project
  ;;  (deploy-impl project phase)
  ;;  nil)

