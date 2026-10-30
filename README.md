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

#### Environment Variables

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
- **utils:** Contains a method for reaching environment variables across the application. In this case Config.properties.

Simplified diagram that explains the architecture nicely.
![img.png](img.png)
***Disclaimer:*** Generated image from ChatGPT

### Key Design Decisions
We've made following design decisions to improve maintainability, performance, and the overall structure of the application.


#### Storing Riot API Data Locally
Data retrieved from the Riot Games API is stored in the application's PostgreSQL database. When an account is searched for, the application first checks whether the account already exists locally. If it does, the stored information can be retrieved without making unnecessary requests to Riot's API. Account information can also be updated using new data from the API.
We've taken this approach to reduce the number of external API requests, as the API is a bit slower than preferred due to heavy trafficking.
That's also the reason for the following.

#### Caching match previews
The application stores up to 20 match previews for each account in the database. Instead of requesting the same match history from Riot's API every time an account is searched for, the application can retrieve the previously stored previews locally.
A match contains a ton of data, therefore it's easier to only store or retrieve what the User usually wants to see, if the User then wishes to see more details the external API is called.

#### Clean cut rehaul and Client
In the early production the project was built simpler and as said, with more frontend in mind, therefore a lot of the application weren't low coupled and most of the features were programmed around the external API.
So a decision was made to make a complete overhaul of the application, hence the name of the project on GitHub. The use of external API is now handled and called upon from and as a client.
This has made further development easier and simpler, since we're able to use more components from the application without needing other unnecessary components.


### Data Model

The application uses PostgreSQL to store user information, League of Legends account data, match previews, and champion information.

#### ERD
![img_1.png](img_1.png)

#### Account Data

League of Legends account information is divided into three tables:

AccountIdentity: Stores account identification information, including game name, tagline, region, and PUUID.

AccountState: Stores profile-related information, including profile icon, summoner level, and revision date.

AccountEntries: Stores ranked information, such as queue type, tier, rank, league points, wins, losses, and ranked status indicators.

The PUUID is used to associate the different account records with the same League of Legends account. It's Riot Games universal player id PUUID. Most of the external API is also based around the PUUID, so it's a must-have in application.

#### Match Previews
The MatchPreview table stores summary information about individual matches, including:

- Match ID and PUUID.
- Champion played.
- Kills, deaths, and assists.
- Win or loss.
- Game creation time and duration.
- Region.

Match previews are associated with accounts through their PUUID and are used to provide access to recent match history without repeatedly requesting the same information from the Riot Games API.

#### Champions

The Champion table stores champion information retrieved from Riot Games, including the champion's ID, numerical key, name, and title.

This allows champion information to be stored locally and reused throughout the application. However, the use of the champions have yet to be implemented properly or be used.

#### Users and Roles

The application has a separate user and role system:

Users: Stores registered users, including username, password, main account PUUID, and tracked accounts.

Roles: Stores the available user roles.

Users_Roles: A join table that connects users and roles through a many-to-many relationship.

This structure allows users to have multiple roles and makes it possible to implement role-based access control. As seen in the security system, only ADMIN can access the Update Champions endpoint.

### API Documentation
The application provides REST endpoints for searching League of Legends accounts and managing user authentication.

Base URL
- Deployed: http://summonerstats.com
- For local development:http://localhost:7070

#### Find Account

Searches for an account using its game name, tagline, and region.

If the account already exists in the local database, the stored information is returned. Otherwise, the application retrieves the account data from the Riot Games API and stores it locally.

**POST** `/account/find`

Request body:

```json
{
  "gameName": "CUC",
  "tagLine": "2808",
  "region": "eun1"
}
```
Response body:
```json
{
  "accountIdentity": {
    "id": "02f07597-02f2-4a4a-9586-4a08e70fc0b4",
    "puuid": "QBf454gEybbOmbkG8Q_2G7XKsjYS43xJPfvm5qALijmXxUGmt162DUF_95YEEp2qXp3Ewv484IS4eg",
    "gameName": "CUC",
    "tagLine": "2808",
    "region": "eun1"
  }
}
...
...
```

#### Register User

Creates a new user account. Newly registered users receive the default USER role.

**POST** `/auth/register`

Request body:

```json
{
  "username": "testuser",
  "password": "testpassword"
}
```

#### Login
Authenticates an existing user and returns a JWT token.

**POST** `/auth/login`
```json
{
  "username": "testuser",
  "password": "testpassword"
}
```
Response body:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJTdW1tb25lclN0YXRzIiwic3ViIjoidGVzdHVzZXIiLCJleHAiOjE3OTMzMjI0ODcsInJvbGVzIjoiVVNFUiIsInVzZXJuYW1lIjoidGVzdHVzZXIifQ.5FqjHoTwciLWsNi76gA3l569vcHBH4qXl4DVsrdOkgI",
  "username": "testuser"
}
```

The returned JWT token can be used to access protected endpoints by including it in the Authorization header:
```
Authorization: Bearer YOUR_JWT_TOKEN
```

### User Stories

**US-01 – Search for a League of Legends Account**

As a user, I want to search for a League of Legends account using its game name, tagline, and region, so that I can view the account's information and statistics.

**US-02 – View Account Statistics and Match History**

As a user, I want to view an account's ranked statistics and recent match history, so that I can evaluate the account's performance.

**US-03 – User Authentication**

As a user, I want to register and log in using a username and password, so that I can access protected features of the application.

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
