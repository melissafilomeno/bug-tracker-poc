# About

This project is a bug-tracking application (WIP).


# Getting Started


Pre-requisites :
* Java 17.0.12
* Maven 3.9.8
* Docker Desktop 27.0.3
* Postman 11.19.0


Configuration Setup :
* Create a .env file and define the following fields :
    * MYSQL_PASSWORD
    * MYSQL_ROOT_PASSWORD
* Open src/main/resources/application.yaml and update the field below with your log directory :
   * logging.file.path


Run :
* Start Docker Desktop
* Run `mvn clean install`
* Run `mvn spring-boot:run`


Verification :
* Go to phpmyadmin at http://localhost:8080 and login as 'root' and 'bug_user' with passwords specified in the .env file and ensure /src/main/resources/db/*.sql are executed
* Import environment and postman_collection under postman/, select 'local' environment, set 'current value' from 'initial value', run the collection and verify


Others :
* Check bugs :
   - mvn compile
   - mvn spotbugs:spotbugs
   - mvn spotbugs:gui
