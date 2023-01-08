## multiple-hibernate-data-sources

#### Note: This example assumes basic familiarity with the included technologies including  Spring Boot, REST architecture, hibernate/ORM, and CRUD repositories.

This example application shows how to configure multiple hibernate data sources for use with hibernate and spring jpa repositories in a simple spring boot application. In this case, two data sources are configured for a h2 database and a mysql database. This will work for any database type that hibernate supports and simply requires modifications to the configs to use the right dialect, driver, connection string, etc. Since this application's primary purpose is to demonstrate the hibernate/JPA config, REST features and endpoints are minimal.

Within your properties file, configuration properties for each data source need to be specified. To distinguish between the data sources, a unique properties prefix needs to be specified for each property group. In this example, `app.datasource.primary` is used for the primary JPA config properties, and `app.hibernateconfig.primary` is used for the primary hibernate configuration properties. `app.datasource.secondary` is used for the secondary JPA config properties, and `app.hibernateconfig.secondary` is used for the secondary hibernate configuration properties.

The model classes and JPA repositories for each datasource must be in independent packages that can be separately scanned. In this example, the primary model is under `matt.sb.example.entities.primary` and the primary jpa repository is under `matt.sb.example.repositories.primary`. The secondary model is under `matt.example.sb.entities.secondary` and the repository is under `matt.sb.repositories.secondary`.

A config class is needed for each data source. Within this config you need to:
- Specify the package to scan for the jpa repositories
- Create beans for the hibernate and jpa configs, specifying the datasource-specific naming conventions previously mentioned.
- Create beans for the `DataSource`, `PlatformTransactionManager`, and `LocalContainerEntityManagerFactoryBean` beans for usage by hibernate and spring-data.

To build this project and run the unit/integration tests, use `mvn clean package`.