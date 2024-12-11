# About

This project is a bug-tracking application (WIP).


# Getting Started


Pre-requisites :
* Java 17.0.12
* Maven 3.9.8
* Docker Desktop 27.0.3
* Postman 11.19.0


Configuration Setup :
* Setup config server (bug-tracker-poc-configserver)
* Create a .env file and define the following fields :
    * MYSQL_DB
    * MYSQL_USER
    * MYSQL_PASSWORD
    * MYSQL_ROOT_PASSWORD
    * ENCRYPT_KEY (align with configserver)
    * CLOUD_CONFIG_USER (align with configserver)
    * CLOUD_CONFIG_PASS (align with configserver)
* Open src/main/resources/application.yaml and update the field below with your log directory :
   * logging.file.path


Run :
* Start config server (bug-tracker-poc-configserver)
* Start Docker Desktop
* Run `mvn clean install`
* Run `mvn spring-boot:run`


Verification :
* Go to phpmyadmin at http://localhost:8080 and login as 'root' and 'bug_user' with passwords specified in the .env file and ensure /src/main/resources/db/*.sql are executed
* Import environment and postman_collection (not management) under postman/, select 'local' environment, set 'current value' from 'initial value', run the collection and verify


Others :
* Check bugs :
   - mvn compile
   - mvn spotbugs:spotbugs
   - mvn spotbugs:gui
* Check database property is refreshed via GIT config change :
   - Start configserver and main application
   - Remove container and verify in Docker Desktop
   - Update database property in .env file
   - Run `docker compose up`
   - Open configserver management postman_collection and run "Encrypt credentials", passing in raw property in body
   - Update property in GIT (config) - {cipher}[[enc value]] and commit
   - Open management postman_collection and run "actuator refresh"
   - Verify the property is returned in the response body
   - Verify the property is updated using management postman_collection "Actuator - Env"
   - Run postman_collection to verify all tests are working
