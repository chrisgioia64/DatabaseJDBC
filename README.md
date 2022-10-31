

This is a Maven project that performs basic CRUD operations via direct JDBC calls on a database with a single table named `Employees`.

### Features

- Ability to run the database application using two different configurations (staging and unit testing).
- A `DAO` (Data Access Object) for the Employee table that performs the CRUD operations using a `PreparedStatement`.
- Use of static proxies for the JDBC `Connection` and `PreparedStatement` object that allow the logging of sql statements.
- Use of Lombok for addition of getters/setters, builders, and logging.
- Unit Testing of `DAO` methods via `TestNG`. Test both sequential transactions and interleaving concurrent transactions.
- Addition of a custom annotation `@DbFile` for specifying the location of a database properties file (e.g. containing database url, username, password)