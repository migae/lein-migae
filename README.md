# migae

*DEPRECATED* - use [boot-gae](https://github.com/migae/boot-gae) instead.

migae is a leiningen plugin for managing migae (Google App Engine) applications.

It is designed to work with the results of
[migae-template](https://github.com/greynolds/migae-template)
(.i.e. "lein new migae").

**WARNING** Alpha software.  Seems to work for me but needs more
  bullet-proofing.  I wouldn't try to use it with existing projects.
  But it is suitable for exploring migae, not to mention
  leiningen.  Documentation will have to wait, in the meantime the
  code should be clear enough provided you understand leiningen
  templates and plugins, and mustache.

  Collaborators welcomed.

## Usage

This plugin tries to run the whole show from project.clj.

Step 1.  Create a new migae project by using the migae template:

    $ lein new migae app myapp:app-id /path/to/gae/sdk

Here myapp is the clojure appname and app-id is the GAE application
ID.  The created app contains a few static files and two servlets, one
of which services two distinct paths.  It's a little more complicated
than the usual "Hello world" example.  Easier to subtract than to add.

Step 2.  Configure the app - generate and install appengine-web.xml
and web.xml, and install other source files to the war tree.

    $ cd myapp
    $ lein migae libdir  ## copy required jars to war/WEB-INF/lib
    $ lein migae config  ## generates web.xml, appengine-web.xml, etc. from mustache files in etc, data in project.clj

It is a requirement of GAE that all required libraries be in
war/WEB-INF/lib; this is due to the security restrictions of the GAE
development server.  This includes certain jars from the GAE SDK as
well as any clojure jars needed.  The migae libdir command takes care
of this.

Step 3.  Develop

See the documentation at [migae](https://github.com/greynolds/migae)

_CAVEAT_: `lein clean` will delete war/WEB-INF and everything in it.
That should be fine, since everything there is generated.  After you
do it, don't forget to run `lein migae libdir` and `lein migae config`
again, in that order.  If it's a problem, edit the :clean-targets
clause in project.clj.

Step 4.  Deploy to the cloud:

    $ lein migae deploy

Don't forget to set the version number in project.clj first!

## Contributing

Download from https://github.com/greynolds/lein-migae, cd to the dir, and
run "lein install"

```
:profiles {:dev {:plugins [[lein-migae "0.1.6-SNAPSHOT"]
                             [lein-libdir "0.1.1"]]}}
```



or whatever the latest version number is.


### Bugs



## License

Copyright © 2013 Gregg Reynolds

Distributed under the Eclipse Public License, the same as Clojure.
