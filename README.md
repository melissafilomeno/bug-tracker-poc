# About

This project is a bug-tracking application (WIP).


# Getting Started


Pre-requisites :
* Java 17.0.12
* Maven 3.9.8
* Docker Desktop 27.0.3


Configuration Setup :
* Create a .env file and define the following fields :
    * MYSQL_PASSWORD
    * MYSQL_ROOT_PASSWORD


Run :
* Start Docker Desktop
* Run `mvn clean install`
* Run `mvn spring-boot:run`


Verification :
* Go to phpmyadmin at http://localhost:8080 and login as 'root' and 'bug_user' with passwords specified in the .env file and ensure tables and data specified in db/db_schema.sql and db/db_data.sql are created
* Go to application at http://localhost:9090/bugs and ensure you can see the mock json response
