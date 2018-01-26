
- Pre Requisites

Java 8

Maven 3

Git

- Overview

Application provides a RESTful api which allows clients to enrich Customer Account data through interaction with ER IF.

- Commands

Build and run unit and integration tests:

mvn clean verify

Run the application in an embedded Tomcat server for dev:

mvn spring-boot:run

To run the application standalone you can also run:

java jar <path-to-jar>

e.g. java jar target/vf-account-service-0.0.1-SNAPSHOT.jar
