# About this project
This is an example of using Twitter Storm (http://engineering.twitter.com/2011/08/storm-is-coming-more-details-and-plans.html)
and processing real live information comming from twitter continuosly.

In this case this example use the Storm framework + redis and nodejs to show the user real time information about
hashtags, links, relevant tweets and applications that are being tweeted and are related to the Android world.

This is just a 'Hello World' project following the storm-starter project (https://github.com/nathanmarz/storm-starter).

Please look for more information into the projects mentionend above.


# Compiling and running

Configure your Twitter account. Edit file src/twitter4j.properties and enter your Twitter oAuth credentials (https://dev.twitter.com/docs/auth/oauth/faq) 

Using Leiningen (https://github.com/technomancy/leiningen) :

```
lein deps
lein compile
mvn clean install
java -cp `lein classpath` storm.starter.TwitterTopology
```

Run a local instance of redis and execute:

```
cd node
npm install
node app.js
```


[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/arcturus/android-live-storm/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

