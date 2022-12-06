# Recipe Manager

This is a standalone Java application that allows users to manage their favorite recipes. It provides a REST API that
allows users to perform the following actions:

- Add a new recipe
- Update an existing recipe
- Remove a recipe
- Fetch a single recipe
- Fetch a list of all ingredients used by recipes
- Fetch a list of recipes, filtered by one or more criteria:
    - Whether the dish is vegetarian
    - The number of servings
    - Specific ingredients (either include or exclude)
    - Text search within the instructions

## Architectural choices

- The application is built using the Spring Boot framework, which provides a convenient way to create a stand-alone,
  production-grade Java application that can be started using a single command.
- The application is designed using API-First approach where the API interfaces and DTOs are generated from [OpenAPI](src/main/resources/recipes-api.yml) specification. This approach helps with the following goals:
    - define requirements and constraints of the API before starting the implementation
    - conveniently shared the specification with the API consumers where it can be used for client code generation
    - define data validations that are translated to JSR380 API
    - provide interactive documentation interface via Swagger UI library
- The application uses a relational database (PostgreSQL) to store the recipes. Spring Boot's built-in support for JPA (Java
  Persistence API) is used for all the database operations. Use of ORM like JPA helps to focus on feature development while leaving possibilities to switch to custom (or native) queries later in case performance requirements will change.
- Application test bed uses [Testcontainers](https://www.testcontainers.org) framework because it helps to avoid the use of mocks for the application dependencies. Implemented test suite mainly consists of integration tests since the application itself doesn't contain much business logic.

## Running the application

To run the application, you will need to have the following installed on your system:

- Java 17 or higher
- Maven
- Docker

Once you have these dependencies installed, follow these steps:

1. Clone the repository from GitHub:
    ```
    git clone https://github.com/my-username/recipe-manager.git
    ```
2. From the root directory of the project, run the following command to build and run the application:
    ```shell
    mvn clean package
    ```
3. Once the build has completed, you can start the docker environment with the following command:
    ```shell
    docker-compose up
    ```
4. Once the application has started, you can access the REST API documentation at the following URL:
    ```
    http://localhost:8080/swagger-ui.html
    ```
5. You can use the API documentation to try out the different endpoints and see how the application works.

## REST API documentation

The application's REST API is defined and documented using the OpenAPI specification. You can access the interactive documentation
interface at the following URL:

```
http://localhost:8080/swagger-ui.html
```

This documentation provides detailed information about each of the endpoints, including the request and response
formats, as well as examples of how to use the API.

## Testing

To run the tests, make sure you have Docker running locally and use the following command:

```shell
mvn verify
```

This will run both the unit tests and the integration tests.

## Dependencies

- [Spring Boot](https://spring.io/projects/spring-boot) - Java framework
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa) - ORM library
- [Testcontainers](https://www.testcontainers.org) - testing library
- [Docker](https://www.docker.com) - container runtime
