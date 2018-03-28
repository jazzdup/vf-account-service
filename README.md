
- Pre Requisites

Java 8

Maven 3

Git

- Overview

Application provides a RESTful api which allows clients to enrich Customer Account data through interaction with ER IF.

- Commands

To build and run unit and integration tests:
1. Add the following servers to your maven settings.xml:```
    <server>
      <id>er</id>
      <username>read-only</username>
      <password>simplepassword</password>
    </server>
    <server>
      <id>ppe</id>
      <username>read-only</username>
      <password>simplepassword</password>
    </server>```
2. mvn clean verify

To run the application in an embedded Tomcat server for dev:

mvn spring-boot:run

To run the application standalone you can also run:

`java jar <path-to-jar>`

e.g. java jar target/vf-account-service-0.0.1-SNAPSHOT.jar



REST Api Documentation can be seen at the following url:

`http://<HOST>:<PORT>/v2/api-docs`

With the Swagger-UI location:

`http://<HOST:<PORT>/swagger-ui.html`

Note that the application must be running to see the documentation.

 
