# Bloom Omni

A collection of lein tools, namespaces and functions, commonly used across Bloom projects.

## Using Omni

Clone omni and install it locally:
```
git clone git@github.com:bloomventures/omni.git`
cd omni
lein install
```

Then, in your project:

(1) Add the following to your `project.clj`:

```clojure
:dependencies [[io.bloomventures/omni "0.11.0"]]

:plugins [[io.bloomventures/omni "0.11.0"]] ; for "omni compile" task

:omni-config path.to/config

:profiles {:uberjar {:aot :all
                     :prep-tasks [["omni" "compile"]
                                 "compile"]}}
```

(2) Create an `omni.config.edn` file in the project's root directory, or, somewhere in your app:

```clojure
{:title "New App"
 :css {:styles "path.to/styles"}
 :cljs {:main "path.to.cljs.core"}
 :routes [...]
 :figwheel-port 1234
 :http-port 1111}
```

## Developing Omni

- should only expose what's necessary (`defn-` the rest)
- should have unit tests for public functions
- should have docstring for namespace w/ example usage
- files should be cljc if possible (unless it doesn't make sense)
- namespace that are not meant to be used externally should go in `bloom.omni.impl.*`

### Versioning

- use [BreakVer](https://github.com/ptaoussanis/encore/blob/master/BREAK-VERSIONING.md)
- have seperate commits for "Start 0.X.0" (with version: `0.X.0-SNAPSHOT`) and "Release 0.X.0" (with version: `0.X.0`)
- tag the release commit `git tag 0.X.0'
