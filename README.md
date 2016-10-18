# ads-mockup

## Description

This is a Clojure application it uses Pedestal and clj-time as dependencies.
The application simulates an ad server, it serve json ads to defined channels.

### Why pedestal

In Clojure are a lot of libraries that can be used for this purpose like
ring + compojure + liberator or http-kit.

I choose pedestal because it uses ring under the hood and offers routing out of the box.
But the feature I like most is that pedestal uses core async channels to serve non blocking
micro-services, I think that asynchronous request handling is a huge benefit on performance.   
 

## Getting Started

1. Clone or download the repository.
2. Start the application: `lein run` inside the application folder
3. Go to [localhost:8080](http://localhost:8080/) to see the application running
4. Read your app's source code at src/ads_mockup/
5. Run your app's tests with `lein test`. Read the tests at test/ads_mockup/service_test.clj.

### Supported platforms.

The Pedestal website states that it can't be run in Windows but I run the service on windows without any problem.
So be careful running on windows because is not yet officially supported by the current version of pedestal.
I also run this application in my Debian 8 Linux box without any troubles.
It should work well on OSX too but I didn't testes it. And any other platforms where the JVM is supported.
My version of the JDK is:
- `java version "1.8.0_77"`

Lein version:
- `Leiningen 2.6.1 on Java 1.8.0_77 Java HotSpot(TM) Server VM`

To see the individual libraries version, refer to:
[project.clj](https://github.com/jboadas/ads-mockup/blob/master/project.clj)


## Configuration

To configure logging see config/logback.xml. By default, the app logs to stdout and logs/.
To learn more about configuring Logback, read its [documentation](http://logback.qos.ch/documentation.html).

## Get ads
1. [localhost:8080](http://localhost:8080/) is the root of the server.
2. [localhost:8080/api](http://localhost:8080/api) is the api end point.
3. To get an ad you must pass query parameters to the application in the `api` end point.

### View the services.

The services can be viewed with:
. curl, on the command line.
. postman plug-in, on Chrome.
. HttpRequester plug-in, on Firefox.
. Or even the browser window can show the json content but if you want to examine the request/response in more detail you need to use one of these programs. 

### Get more information of whats going on.

I Extensively use the logging service, if you need to view how the request is processed in detail I suggest to open the logs and check it on every request.
The logs will help to figure out what is happening with the ads, also if you lost an ad check the date of the ad because some ads have five minutes of life.

### Query parameters.
The ads are being served to certain channels and to get an ad you must pass a channel parameter.

- `http://localhost:8080/api?channel=www.news.com`

All defined channels and ads are in the in-memory data file [ads_data.clj](https://github.com/jboadas/ads-mockup/blob/master/src/ads_mockup/ads_data.clj).

From here you can filter your ads using more query parameters:

Filter by country: 

- `http://localhost:8080/api?channel=www.news.com&country=US`


Filter by language: 

- `http://localhost:8080/api?channel=www.news.com&lang=en`

Filter by gender: 

- `http://localhost:8080/api?channel=www.news.com&gender=F`

Filter by age: 

- `http://localhost:8080/api?channel=www.news.com&age=33`

Or you can mix all the parameters and see what you can get, only the channel parameter is mandatory because you need to know who is requesting the ad 
 

- `http://localhost:8080/api?channel=www.news.com&country=US&lang=en&gender=F&age=33`


## Further development

1. Start a new REPL: `lein repl`
2. Start your service in dev-mode: `(def dev-serv (run-dev))`
3. Connect your editor to the running REPL session.
   Re-evaluated code will be seen immediately in the service.

### [Docker](https://www.docker.com/) container support

1. Build an uberjar of your service: `lein uberjar`
2. Build a Docker image: `sudo docker build -t ads-mockup .`
3. Run your Docker image: `docker run -p 8080:8080 ads-mockup`

### [OSv](http://osv.io/) unikernel support with [Capstan](http://osv.io/capstan/)

1. Build and run your image: `capstan run -f "8080:8080"`

Once the image it built, it's cached.  To delete the image and build a new one:

1. `capstan rmi ads-mockup; capstan build`

# ads-mockup end
