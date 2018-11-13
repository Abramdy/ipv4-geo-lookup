# Purpose

A small app for finding the approximate geo location of ipv4 addresses. The application demonstrates

*  orchestrating a [spring boot app](https://spring.io/projects/spring-boot) and [postgres](https://www.postgresql.org/) database using [docker-compose](https://docs.docker.com/compose/)
* managing database structure using [liquibase](http://www.liquibase.org/)
* using [TestContainers](https://www.testcontainers.org/) for creating a database for running tests
* use of the [GeoLite2 Free Downloadable Database](https://dev.maxmind.com/geoip/geoip2/geolite2/)


# Quick Start

Build and run the application & database:

```
docker-compose up
```

_Note_: The first run will take some time -- external dependencies are downloaded, and the data set is loaded into the databae.

A simple UI is available to interact with the serivce on your browser on port `8080`, or you can query from the command line:

```
curl localhost:8080/search?ipv4Address=1.0.0.0 | python -m json.tool

{
    "network": "1.0.0.0/24",
    "latitude": -33.494,
    "longitude": 143.2104,
    "accuracyRadius": 1000
}
```

# Development

**Setup**

* Java 8+ [installation instructions](https://www.java.com/en/download/help/download_options.xml)
* The codebase uses [lombok](https://projectlombok.org/) to reduce boiler-plate code. Several IDEs support this natively (as well as `javac`), but some don't. For instance, there are instructions for [setting up in eclipse](https://projectlombok.org/setup/eclipse)

**Build+Test**

```
./mvnw package
```

_Note:_ If tests fail locally, check your machine has the [prerequisites](https://www.testcontainers.org/usage.html#prerequisites) for ``TestContainers``.

**Run** _(locally)_

You will want to have a local ``Postgres`` instance running. 

```
docker run --name test-postgres -p 5432:5432 -e POSTGRES_PASSWORD=password -d postgres:11.1
```

Then start the application

```
./mvnw spring-boot:run
```


# Implementation Notes

## Embedded Data

This project has embedded a copy of the [GeoLite2 Free Downloadable Database](https://dev.maxmind.com/geoip/geoip2/geolite2/) from November, 2018 in CSV format (compressed). The location of the file to use can be overridden with the ``ipv4-geo-lookup.loader.source-location`` property. There are [many ways](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html) in spring boot to override a property.

While it is not good practice to embed such a large dataset, it is done here for the purpose of having a _working example_ with minimal dependencies.

## Search Details

The _search_ over the dataset is implemented by using the built-in [network address functions](https://www.postgresql.org/docs/11/functions-net.html) within ``Postgres``. This is indexable, and reasonably performant. For production grade applications, you should consider using MAXMIND's [own database](https://dev.maxmind.com/geoip/geoip2/downloadable/), which has libraries for most all major languages. Postgres was chosen here to easier demonstrate orchestration with [docker-compose](https://docs.docker.com/compose/).


