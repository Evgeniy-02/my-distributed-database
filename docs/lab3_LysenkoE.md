<!-- Latex tags

\documentclass[oneside,final,14pt]{extreport}
\usepackage[utf8]{inputenc}
\usepackage[russian]{babel}
\usepackage{vmargin}
\setpapersize{A4}
\setmarginsrb{2cm}{1.5cm}{1cm}{1.5cm}{0pt}{0mm}{0pt}{13mm}
\usepackage{indentfirst}
\usepackage{hyperref}
\sloppy
\usepackage{minted}
\usepackage[hashEnumerators,smartEllipses,fencedCode,hybrid]{markdown}


\begin{document}
\begin{markdown}
-->


# Лабораторная работа №3 

Написать отчет: пункты, реализованные в системе: хэш-мап, распределение, консенсус. Что требует улучшения. Написать про свою часть работы, указать, с кем в команде работал, включить README.

## Введение

Целью лабораторной работы было создание распределенной системы в команде. Для распределения была взяла база данных с GraphQL API ([custom-database](https://github.com/pirosveta/custom-database)), разработанная Светланой Пирожковой. Комада состояла из 2 человек: Светланы Пирожковой и Евгения Лысенко.

Для получения распределенной системы, необходимо было решить 4 задачи: распределение, репликация, консенсус транзакций и модель времени. Задачи были раделены между участниками разработки: распределением и репликацией занимался я, консенсусом транзакций и моделью времени занималась Светлана.

## Распеределение

Для получения распределенной системы первым шагом было принято разработать схему системы. Схема распределенной системы, к которой мы со Светланой пришли, получилась следующей:

<!-- setkeys{Gin}{width=1.inewidth} -->
![distributed system scheme](img/distributed-database-scheme.png "Схема распределенной системы")

Система состоит из:

* Главного узла (Main Node)
* Узлов базы данных (Node)

Пользователь взаимодействует с главным узлом (Main node), отправляя запросы и получая ответы, используя язык GraphQL. Остальные узлы (Node 1, 2, ..., N) взаимодействуют только с главным узлом и координатором, выполняют функцию хранения данных, являются базами данных.
За координацию узлов отвечает сервис EurekaServer. Все узлы регистрируются в базе данных координатора, отправля имя, адрес и порт, координатор сохраняет эти значения и, затем, выдает URL-адрес узла по его названию. 


## Репликация

Данные распределяются между узлами по следующему алгоритму: 

1. определяется количество подключенных узлов Node, обозначим n;
2. вычисляется значение i = id % n;
3. запись с номером id сохраняется на узел i;
4. копия записи сохраняется на узел (i + 1) % n;

Добавление, получение, обновление и удаление данных в распределенной системе производится по id записи согласно указанному алгоритму.

## Задачи на улучшение системы

Направления развития и соответствующие им задачи можно изобразить следующим иерархическим списком, в котором направление будет соотвествовать 1 уровню, задача - 2 уровню, подзадача - 3 уровню.

1. Устойчивость к отказам:
    1. обработка операций при отказе узлов Node;
2. покрытие возможностей GraphQL:
    1. обработка запросов на всем множестве языка GraphQL;


# Readme

## my-distributed-database

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

<!-- setkeys{Gin}{width=1.inewidth} -->
![GraphQL GUI](img/graphiql.png)

## Usage

```graphql
# Add user

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
```

```graphql
# Get users

query {
  users(alias: "1") {
    id
    name
    lastName
    email
  }
}
```

```graphql
# Get user

query {
  user(request: { alias: "1", id: 1 }) {
    id
    name
    lastName
    email
  }
}
```

```graphql
# Delete user

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

### Transactions

To start a transaction, you need to send a request to start it, passing the transaction user as the `alias` parameter. At this moment, a temporary database is created -- a copy of the permanent database.

```graphql
mutation {
  begin(alias: "1") {
    message
  }
}
```

Next, you can start sending CRUD requests on behalf of the `alias` that was passed when the transaction began. During the transaction, the entire history of operations will be logged to the appropriate file. And the operations are performed in a temporary database. If the node falls, then, when it is restored, an attempt will be made to restore the log file as well. In this case, operations with records are already performed in a permanent database, and not in a temporary one.

To complete the transaction, you need to send a request to commit the changes made. When such a request is sent, changes from the temporary database are transferred to the permanent one, and the log file is cleared.

```graphql
mutation {
  commit(alias: "1") {
    message
  }
}
```

## Supported operations

See Supported operations.txt <br />
//TODO

## IDE Support

To use these projects in an IDE you will need the [project Lombok](http://projectlombok.org/features/index.html) agent. Full instructions can be found in the Lombok website. The sign that you need to do this is a lot of compiler errors to do with missing methods and fields.

## Access to Eureka Server

[http://localhost:7000/](http://localhost:7000/)

<!-- \setkeys{Gin}{width=1.\linewidth} -->
![Spring Eureka Server status pane](img/eureka-server.png)


<!-- 
\end{markdown}
\end{document}
-->
