## SummonerStats.com

### Project description
SummonerStats.com is a java based (currently only backend) application that retrieves and stores data for League of Legends accounts using the Riot Games API.
The application is set up with REST endpoints that receives and retrieves Json based on the data and endpoint given.
Most data is saved in our own Database in PostgresSQL so previously retrieved data can be reused.

### Program features
Since the project is still under development and only backend
some features are left for further development down the line. Here's a list of the current features that are implemented.

#### Current features:
1. Search for League of Legends account. (Identity, Rank entries and Customization)
2. Add League of Legends account to DB from API.
3. Find League of Legends account in DB.
4. Update League of Legends account via API to DB.
5. Search, add and find Match Histories from API.
6. Add and Update League of Legends champions to DB.
7. Register website User.
8. Login as website User.

### Technologies

The project uses the following technologies and libraries:

- **Java 25:** The programming language used to develop the application.
- **Maven:** Build automation and dependency management.
- **Javalin 6.7.0:** Lightweight web framework for HTTP endpoints and REST API development.
- **Javalin Rendering:** Integration for server-side template rendering.
- **Thymeleaf 3.1.3:** Server-side HTML template engine.
- **PostgreSQL:** Relational database for persistent data storage.
- **Hibernate ORM 7.2.3:** Object-relational mapping using JPA.
- **HikariCP:** JDBC connection pooling through Hibernate's HikariCP integration.
- **Jackson:** JSON serialization and deserialization, including Java date and time support.
- **Lombok:** Reduces boilerplate code in Java classes.
- **JWT (Nimbus JOSE + JWT):** JSON Web Token functionality for authentication.
- **TokenSecurity:** Security utilities for token-based authentication.
- **jBCrypt:** Password hashing.
- **SLF4J and Logback:** Application logging.

### Testing

The project uses the following testing libraries:

- **JUnit 6:** Unit testing and test execution.
- **Mockito:** Mocking dependencies during unit tests.
- **REST Assured:** Testing HTTP endpoints and REST API responses.
- **Hamcrest:** Matchers for expressive test assertions.
- **Testcontainers:** Running integration tests against containerized dependencies, including PostgreSQL.

### Prerequisites

The following software and resources must be met to run the application.
- **Java 25** Our compatible Java version.
- **Maven** For project building.
- **Docker** If Postgres is up via. Docker.
- **Riot API key** Our project hasn't been officially authorized by Riot Games, therefore the dev must get a new key every 24h from the Riot Games Developer Portal.

### Architecture
The application divides HTTP request handling, logic, data access,
external API communication, and database configuration into dedicated packages:

- **routes:** Defines the application's REST endpoints and connects incoming HTTP requests to corresponding controllers.
- **controllers:** Handles incoming requests, extracts request data (typically Json or an object extracted from Json), calls services, daos or other components, and returns HTTP responses.
- **service:** Contains business logic and application-level operations, such as account processing, statistics calculations, and user-related functionality. The User service is mostly as of now.
- **daos:** Provides DAOs for retrieving, creating, and updating persistent data. There's also a common IDAO interface, that most of the DAOs implement.
- **entities:** Contains the application's persistent data, including users, roles, champions, match previews, and League of Legends account data (see entity registry in config).
- **dtos:** Contains Data Transfer Objects used for non-persistent data between the application, external APIs, and clients. The dtos usually represent data of the entities before they're persisted or data from more than one entity (see AccountOverviewDTO for example).
- **mappers:** Converts data between DTOs and entities when it's required.
- **client:** Handles communication with the Riot Games API through RiotApiClient and APIReader.
- **security:** Contains authentication and authorization components, including token handling, security interfaces, and security-related messages.
- **config:** Handles application configuration and Hibernate setup, including database connection and entity registration.
- **concurrency:** Separates concurrency-related functionality from the main application logic.
- **exceptions:** Contains custom exceptions for API errors, token creation and verification, and authorization failures.
- **utils:** Contains shared utility functionality used across the application.

### Environment Variables

The application uses specific variables the Dev must implement themselves.

Create a config.properties in the resources folder.
The properties must include the following...

- DB_NAME= (The DB's name)
- DB_USERNAME= (Username for DB login)
- DB_PASSWORD= (password for DB login)
- ISSUER=SummonerStats
- TOKEN_EXPIRE_TIME=3600000
- SECRET_KEY= (Your own secret key)
- API_KEY= (Your own key from Riot's API)

### Future Improvements

As mentioned before this application is currently only backend but the original idea was very frontend-minded. Therefore, some features are left out for further development with frontend in the roster as they'll be easier to visual and work around.
Here are some of the future improvements and features that has been planned:
1. Users can add LOL accounts to their tracking list for easier access.
2. Users can stop tracking LOL accounts.
3. Users can add their Main account to their profile for easier access.
4. A thread system that can run different actions parallel, since the API allows for 20 request at once.
5. A system that adds customization via. the user's profile and the connection with the Main LOL account.
6. Better statistics. This could include where the LOL account is placed on a leaderboard and so on...

Again, these are all potential improvements and might be added further as development progresses.

### Developers

This project was developed as part of the Computer Science Programme in Lyngby in Denmark.
Project: SummonerStatsRehauled

Authors:

**Christopher U. Christensen**,

**Peter Maersk Frilund Søndergaard**

### Links
Deployed application:  
http://summonerstats.com

Source code repository:  
https://github.com/cuc2808/SummonerStatsRehauled

Portfolio websites:  
Christopher: https://cuc2808.github.io/Portfolio/
Peter: https://peter-kunosu.github.io/
