# my-distributed-database

Simple Spring Boot GraphQL distributed database based on [custom-database](https://github.com/pirosveta/custom-database)

## Tools

* Java 11+
* Spring Boot, Cloud, Eureka
* Maven (Tested on 3.8.1)
* Gradle (Tested on 7.4.2)
* GraphQL
* Lombok
* SnakeYaml
* Spring Boot DevTools

## How to run
* Run EurekaServer
```shell
$ gradle bootRun
```

* Run MainNode
```shell
$ mvn spring-boot:run
```

* Run Nodes
```shell
$ SERVER_PORT=8088 mvn spring-boot:run
$ SERVER_PORT=9092 mvn spring-boot:run
$ SERVER_PORT=7022 mvn spring-boot:run
$ ...
```

## Start with GUI

[http://localhost:8080/api/graphiql](http://localhost:8080/api/graphiql)

![GraphQL GUI](docs/img/graphiql.png)

## Usage

```
Add user

mutation {
  createUser(
    request: { alias: "1", id: 1, name: "1 User", email: "1user@email.com" }
  ) {
    id
    name
    lastName
    email
  }
}

Get users

query {
  users(alias: "1") {
    id
    name
    lastName
    email
  }
}

Get user

query {
  user(request: { alias: "1", id: 1 }) {
    id
    name
    lastName
    email
  }
}

Delete user

mutation {
  deleteUser(
    request: { alias: "1", id: 1 }
  ) {
    id
    name
    lastName
    email
  }
}

//TODO
```

## Supported operations

See Supported operations.txt <br />
//TODO

## IDE Support

To use these projects in an IDE you will need the [project Lombok](http://projectlombok.org/features/index.html) agent. Full instructions can be found in the Lombok website. The sign that you need to do this is a lot of compiler errors to do with missing methods and fields.

## Access to Eureka Server

[http://localhost:7000/](http://localhost:7000/)

![Spring Eureka Server status pane](docs/img/eureka-server.png)

