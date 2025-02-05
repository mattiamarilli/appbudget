[![Università degli studi di Firenze](https://i.imgur.com/1NmBfH0.png)](https://ingegneria.unifi.it)

# Budget Manager

Made by [Mattia Marilli](https://github.com/mattiamarilli)

[![Java CI with Maven (Linux, Macos, Windows) (Java 8/11)](https://github.com/mattiamarilli/appbudget/actions/workflows/maven.yml/badge.svg)](https://github.com/mattiamarilli/appbudget/actions/workflows/maven.yml)
[![Coverage Status](https://coveralls.io/repos/github/mattiamarilli/appbudget/badge.svg?branch=master)](https://coveralls.io/github/mattiamarilli/appbudget?branch=master)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=mattiamarilli_appbudget&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=mattiamarilli_appbudget)

### Get started

Clone the repository and navigate to the project folder and run the project:

```sh
$ git clone https://github.com/mattiamarilli/appbudget.git
$ cd appbudget
```

Run the db inside the docker container (the db will listen on localhost:3306, adminer available on localhost:8080)
```sh
$ docker compose --project-name=appbudget up
```

Build and run the java application (without executing the test stack)
```sh
$ mvn clean package -DskipTests exec:java 
```

## Project Overview

Budget Manager is a simple application designed to help manage personal finances using the 50/30/20 philosophy. It automatically calculates how to divide your income: 50% for needs, 30% for wants, and 20% for savings or debt repayment.

In addition to providing practical budget management features, the main goal of this project is to serve as a hands-on exercise for exploring software testing techniques and improving development practices. The project incorporates various testing methodologies, including unit testing, integration testing, end-to-end testing, and GUI testing using AssertJ Swing.

To ensure reliability, mutation testing is performed with PIT, and test coverage is monitored with JaCoCo. SonarCloud is integrated to analyze and maintain high code quality. The project also uses GitHub Actions for Continuous Integration and remote builds, ensuring that every commit is tested and built in a consistent environment. This setup provides immediate feedback on how changes impact the application.

## Technologies

The application is built with Java 8, using Hibernate for database persistence and MariaDB as the backend database. Maven is used as the build and dependency management tool. Testing tools include JUnit, AssertJ, AssertJ Swing, Mockito, and PIT Mutation Testing, with JaCoCo ensuring comprehensive test coverage. Code quality is continuously analyzed with SonarCloud, while GitHub Actions provides a robust CI/CD pipeline for automated testing and remote builds.
