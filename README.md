# Barrierelos Backend

[![pipeline status](https://gitlab.ost.ch/barrierelos/backend/badges/main/pipeline.svg)](https://gitlab.ost.ch/barrierelos/backend/-/commits/main)
[![coverage report](https://gitlab.ost.ch/barrierelos/backend/badges/main/coverage.svg)](https://gitlab.ost.ch/barrierelos/backend/-/commits/main)

This is the repository for the Barrierelos backend that contains the business logic of the Barrierelos project. The also
includes submitting website accessibility scan jobs to the [scanner](https://gitlab.ost.ch/barrierelos/scanner) via
RabbitMQs and retrieving and processing the results.
It is written in Kotlin with Spring Boot and exposes a REST API used by the frontend. A PostgreSQL database
is used for persistence.

## Directory Structure

The project skeleton was generated with [Spring Initializr](https://start.spring.io/) which is using the standard Spring
Boot directory structure, which is based on the [Gradle directory
structure](https://docs.gradle.org/current/userguide/organizing_gradle_projects.html). Hence, this chapter will only
describe our project specific directories.

- `src/main/kotlin/ch/ost/barrierelos/backend` contains the Kotlin source code. The contained folders are called
  packages in Kotlin / Java terminology.
  - `security` contains the authentication logic as well as the security context.
  - `controller`, `service` and `repsitory` are standard Spring Boot packages and correspond to the logical layers
    Presentation, Domain and Persistence.
  - `message`, `model` and `entity` are the corresponding data classes for the packages / layers above. In order to
    reduce duplication of code, we didn't always create a separate data class for each layer. We opted to only create
    a separate data class if the one from the layer below uses different fields or types.
  - `converter` contains the converter classes which are used to convert between the data classes in the different
    layers.
  - `exception` contains the custom exceptions that are thrown by the application as well as the global exception
    handler which ensures that technical errors in the exceptions are not exposed to the API, which would be a
    security risk. All unmapped exceptions are returned as generic errors.
  - `enums` contains the enums that are shared between the different layers. Note that we had to use the plural form
    because `enum` is a reserved keyword.
  - `configuration` contains configuration logic concerning the security, the api and the communication with RabbitMQ (
    Queue).
- `src/main/resources/db.migration` contains the database migration scripts. These scripts are executed automatically on
  startup by [Flyway](https://flywaydb.org/), if the database is not up-to-date. If you want to make changes to the
  database, you have to create a new migration script so that existing data in the database doesn't become corrupted.
- `src/test/kotlin/ch/ost/barrierelos/backend` contains the Kotlin test cases as well as helper classes
  - `helper` contains helper classes that are shared between test cases.
  - `unit` contains the unit tests.
  - `integration` contains the integration tests. They are further subdivided into the three logical layers.

## Development

Before getting started, have a look at the [deployment](https://gitlab.ost.ch/barrierelos/deployment) repository where
you will learn how to set up the development environment for the project overall. This sections will only
describe the steps that are specific to the backend.

We developed the backend with IntelliJ IDEA, and we highly recommend using it for development since our development
workflow is well integrated into the IDE. When you open the project in IntelliJ, everything should be set up configured
correctly.

We were developing on Linux and as such, some scripts might not work on Windows or macOS. MacOS should
technically work, but we didn't test it.

The project already comes with three run configurations:

> Before executing these running configuration, make sure you started the required components as per the instructions
> in the [deployment](https://gitlab.ost.ch/barrierelos/deployment) repository.

- `Setup Database` will run all database migration scripts to set up the database and insert the initial data. This will
  also delete the existing tables and data before recreating them. This is useful if you want to reset the database.
- `Run Locally` to run the application locally. The application can either be started normally or in debug mode.
- `Run All Tests` to run all tests. The tests can also either be run normally or in debug mode. Note that the Gradle
  cache can lead to the tests not being found, to fix this, you can run `./gradlew clean` in the terminal. You might
  have to make `./gradlew` executable first with `chmod +x ./gradlew`.

### Accessing the PostgreSQL Database

Both the local and the database on the server are preconfigured as data sources in IntelliJ. You can access them in
the `Database` tab on the right side of the IDE. The default username and password for the local database
is `barrierelos` for both. On the server, the username is also `barrierelos`, but the password is stored in
the [GitLab CI/CD Variables](https://gitlab.ost.ch/groups/barrierelos/-/settings/ci_cd#ci-variables).

### Accessing OpenAPI / Swagger UI

Spring automatically generates an OpenAPI specification based on the REST API. This specification can be accessed as
JSON or YAML and the documentation is available in the form of a web UI called `Swagger`:
specification.

* [JSON](http://barrierelos.ch:40001/openapi)
* [YAML](http://barrierelos.ch:40001/openapi.yaml)
* [Swagger UI](http://barrierelos.ch:40001/swagger/swagger-ui/index.html)

The OpenAPI specification is also used by the [frontend](https://gitlab.ost.ch/barrierelos/frontend) to generate the
client code.
