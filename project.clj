(defproject android-live-storm "0.0.1-SNAPSHOT"
  :source-path "src/clj"
  :java-source-path "src/jvm"
  :javac-options {:debug "true" :fork "true"}
  :aot :all
  :jvm-opts ["-Djava.library.path=/usr/local/lib:/opt/local/lib:/usr/lib"]
  :repositories {
                 "twitter4j" "http://twitter4j.org/maven2",
		 "akka" "http://repo.typesafe.com/typesafe/releases/",
		 "local" ~(str (.toURI (java.io.File. "maven_repository")))
                 }

  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [org.twitter4j/twitter4j-core "3.0.3"]
                 [org.twitter4j/twitter4j-stream "3.0.3"]
		 [storm/storm-kestrel "0.6.0"]
		 [log4j/log4j "1.2.16"]
		 [redis.clients/jedis "2.0.0"]
                 [org.jsoup/jsoup "1.6.1"]
		 [goose "2.1.10"]
		 [storm "0.6.0"]
                 ]

  :dev-dependencies [[storm "0.6.0"]
                     ])

