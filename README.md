
Pre Requisites:
Java 8
Maven 3
Git

Build and run unit and integration tests with Maven:

mvn clean install

This should build, run unit tests and integration tests

Run the application in an embedded server for dev:

mvn spring-boot:run

To run the application standalone you can also run:

java jar <path-to-jar>
e.g. java jar target/vf-account-service-0.0.1-SNAPSHOT.jar
