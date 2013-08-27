(ns leiningen.migae.config
  "config - a migae subtask for configuring a gae app"
;;  (:import [java.io File])
  (:use [leiningen.new.templates :only [render-text slurp-resource sanitize year]])
  (:require [clojure.java.io :as io]
            [clojure.contrib.io :as cio]
            [stencil.core :as stencil]
            [leiningen.classpath :as cp]
;            [leiningen.new.templates :as tmpl]
            [leiningen.core [eval :as eval] [main :as main]]
            [clojure.string :as string]))

(defn classpath
  []
  (let [cp (System/getProperty "java.class.path")
        cps (clojure.string/split cp #":")]
    (doseq [p cps] (println p))))

(defn copy-tree [from to]
  ;; (println "\nFiles in " from " to " to)
  (doseq [f (.listFiles (io/as-file from))]
    (let [fn  (.getName (io/as-file f))]
      (do ;(print "\ttgt: " f "\n")
        (if (.isDirectory f)
          (copy-tree (.getPath f) (str to "/" (.getName f)))
          (do
            ;; (print (format "\tfrom %s to %s/%s\n" f to fn))
            ;;                             (print "copying\n")
            (io/make-parents to fn)
            (io/copy f (io/file to fn))))))))

;;       (with-open [of (io/writer (io/file to fn))]
;; )))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; steal from leiningen templates.clj functions
;; that uses resources; we use files since we'll always use templates in ./etc

;; (def xrender-text stencil/render-string)
(defn renderer [name]
  (fn [template & [data]]
;;orig: (let [path (string/join "/" ["leiningen" "new" (sanitize name) template])]
    (let [path (string/join "/" [(sanitize name) template])
          cpath  (.getCanonicalPath (io/file path))
          ;; p (println (str "canonical path: " cpath))
          f (io/file cpath)
          ;; ok (println (str "isFile " (.isFile f)))
          ;; a (println (str "+name: " name))
          ;; b (println (str "template: " template))
          ;; c (println (str "path: " path))
          ]
      ;; (if-let [resource (io/resource path)]
      (if-let [resource (io/as-relative-path path)]
        (if data
          (do (println (format "slurping %s" (str (cio/pwd) "/" resource)))
              (render-text (slurp resource) data))
          (do (println (format "io/reading %s" (str (cio/pwd) "/" resource)))
              (io/reader resource)))
        ;; (if (.isFile f)
        ;;   (if data
        ;;         (render-text (slurp-resource cpath) data))
        ;;         (io/reader (str cpath))))
        (main/abort (format "Template resource '%s' not found in %s." path (cio/pwd)))))))

;;;;;;;;;;;;;;;;
;; The original code (in leiningen/src/leiningen/new/templates.clj)
;; creates the project dir.  That's no good for us - we're already in
;; the proj dir, we want to process templates in etc and put the
;; results in the war dir.
(defn- template-path [name path data]
  (io/file name (render-text path data)))
(def ^{:dynamic true} *dir* nil)
(defn ->files
  [{:keys [name] :as data} & paths]
  (do
    ;; (let [dir (or *dir*
    ;;               (.getPath (io/file
    ;;                          (System/getProperty "leiningen.original.pwd" name))))]
    ;;   (println (format "->files: installing %s" dir))
;      (if (or *dir* (.mkdir (io/file dir)))
        (let [dir "./"]
          (doseq [path paths]
            (do
              (println (format "->files: installing to %s" (first path)))
              (if (string? path)
                (.mkdirs (template-path dir path data))
                (let [[path content] path
                      path (template-path dir path data)]
                  (.mkdirs (.getParentFile path))
                  (io/copy content (io/file path)))))))))

;;                                         ;    (println "Could not create directory " dir ". Maybe it already exists?"))))
;; ;; end of overrides

(defn config
  "copy/transform files into the war dir structure - 'lein migae :config'

This task is designed to support the need to distribute all the other
files you need for a (java) webapp: what goes in the war dir, WEB-INF,
etc.  The idea is to control all that via the project.clj file.  At
the moment all it does is create the xml config
files (appengine-web.xml and web.xml) and write them to <war>/WEB-INF.
The files are created by processing the (stencil/mustache) templates
in <project>/.project using the data fields from project.clj.  So you
should not edit the files directly; if you need to make a
change (e.g. change the version number), edit the project.clj and then
run 'lein migae config'."
  [project & args]
  (do
    ;; (println (str "classpath: " (classpath)))
    ;; (println (str "compiling " (:name project)))
    ;; (jar/jar project)
    (let [render (renderer "etc") ;; (:name project))
          config (:gae-app project)
          static_exclude (:pattern (:exclude (:statics   config)))
          resource_exclude (:pattern (:exclude (:resources   config)))
          ;; foo (println static_exclude)
          ;; NOTE:  data maps of migae-template and migae plugin must match
          data {:name	(:name project) ;; :name required by ->files
                :project	(:name project)
                :projname	(:name project)
                :app-id		(:id config)
                :display-name	(:display-name config)
                :version	(:dev   (:version config))
                :war		(:war	    config)

                :servlets	[(:servlets config)]

                ;; TODO: conditional processing of include/expire/exclude
                :static_src    (:src (:statics config))
                :static_dest   (:dest (:statics config))
                :static_include_pattern	(:pattern
                                         (:include (:statics config)))
                :static_expire	(:expire (:include (:statics config)))
                :static_exclude (if (nil? static_exclude)
                                  false
                                  {:static_exclude_pattern
                                   (:pattern (:exclude (:statics   config)))})

                :resource_src  (:src (:resources config))
                :resource_dest (:dest (:resources config))
                :resource_include_pattern (:pattern
                                           (:include (:resources config)))
                :resource_expire (:expire (:include (:resources config)))
                :resource_exclude (if (nil? resource_exclude)
                                    false
                                    {:resource_exclude_pattern
                                     (:pattern (:exclude (:resources config)))})

                :welcome	(:welcome   config)
                :threads	(:threads   config)
                :sessions	(:sessions  config)
                :java-logging	(:java-logging config)}]

      (println (format "copying static files from src tree to war tree"))
      ;; TODO:  use {{statics}} instead of hardcoded paths, e.g.
                 ;; ["{{war}}/{{static_dest}}/css/{{project}}.css"
                 ;;  (render (render-text "{{static_src}}/css/{{project}}.css" data))]
                 ;; ["{{war}}/{{static_dest}}/js/{{project}}.js"
                 ;;  (render (render-text "{{static_src}}/js/{{project}}.js" data))]

      (copy-tree "src/main/public" "war")
      ;; (copy-tree "src/main/public/css" "war/css")
      ;; (copy-tree "src/main/public/js" "war/js")

                 ;; TODO: handle binary files??
                 ;; ["{{war}}/favicon.ico"
                 ;;  (render (render-text "{{resource_src}}/favicon.ico" data))]
      ;;      (println (format "copying resource files from src tree to war tree"))

      (println (format "installing templates"))
      (do
        (->files data
                 ;; [to file  		from template]

                 ["{{war}}/WEB-INF/appengine-web.xml"
                  (render "appengine-web.xml.mustache" data)]

                 ["{{war}}/WEB-INF/web.xml"
                  (render "web.xml.mustache" config)]

                 ["{{war}}/WEB-INF/{{java-logging}}"
                  (render (render-text "{{java-logging}}" data))]

                 )
        (println "ok"))
      )))
