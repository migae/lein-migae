(ns leiningen.migae.libdir
  "libdir - a migae subtask copying sdk jars to war/WEB-INF/lib."
  (:import java.io.File)
  (:use [leiningen.libdir :exclude [libdir]])
  (:require [clojure.java.io :as io]
            [stencil.core :as stencil]
            [leiningen.classpath :as cp]
            [leiningen.new.templates :as tmpl]
            [leiningen.core [eval :as eval] [main :as main]]
            [clojure.string :as string]))

(defn flat-copy-tree [from to]
  (doseq [f (.listFiles (io/as-file from))]
    (let [fn  (.getName (io/as-file f))]
      (if (.isDirectory f)
        (flat-copy-tree (.getPath f) to)
        (do
          (println (format "\t%s" f))
          (io/make-parents to fn)
          (io/copy f (io/file to fn)))))))

(defn libdir [project & args]
  (let [lib (str (:war (:migae project)) "/WEB-INF/lib/")]
    (do
      (println (format
                "copying gae sdk jars to %s/WEB-INF/lib:"
                (:war (:migae project ))))
      (flat-copy-tree (str (:sdk (:migae project)) "/lib/user") lib)
      (println (format
                "copying project dependencies to %s/WEB-INF/lib using libdir plugin:"
                (:war (:migae project ))))
      (leiningen.libdir/libdir project args)
    )))

    ;; TODO: iterate over (:dependencies project) and copy jars to lib
;;     (doseq [dep (:dependencies project)]
;;       (let [[name nbr] dep]
;;         (do ; (println (format "name: %s - nbr: %s" name nbr))
;;             ; (println (format "local repo: %s" (:local-repo project)))
;;             (let [[a class] (.split (str name) "/")
;;                   group (.replace a "." "/")
;;                   fpath (str home "/.m2/repository/" group "/" class "/" nbr)
;;                   fnm (str class "-" nbr ".jar")
;;                   from (.getCanonicalPath (io/as-file (str fpath "/" fnm)))]
;; ;;              (println (format "\tgroup: %s - class: %s\n\t%s" group class fnm))
;;               (do
;;                 (println (format "deleining %s" from))
;;                 (io/copy (io/file from) (io/file lib fnm)))))))))
