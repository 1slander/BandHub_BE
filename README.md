# BandHub

BandHub is a hybrid web platform for musicians and bands. The project combines a public social layer for music discovery with a private backstage layer for band management.

The main goal of this repository is to build a professional full-stack application while practicing clean architecture, Spring Boot, JPA, REST APIs, security, and later Angular integration.

## Project Vision

BandHub has two main areas:

- Public area: musician and band profiles, posts, music discovery, and public concerts.
- Private area: internal tools for band members to manage rehearsals, songs, setlists, concerts, and documents.

The MVP starts with a small relational model and will grow iteratively.

## Tech Stack

- Backend: Java 21, Spring Boot 3.5.x
- Frameworks: Spring Web, Spring Data JPA, Spring Security, Bean Validation
- Database: PostgreSQL
- Build tool: Maven
- Helpers: Lombok, Spring Boot DevTools
- Future frontend: Angular with TypeScript
- Future cloud: AWS S3, AWS RDS, AWS EC2

## Current Backend Structure

```text
src/main/java/com/bandhub
+-- BandhubApplication.java
+-- controller
|   +-- UserController.java
+-- dto
|   +-- UserResponseDTO.java
+-- model
|   +-- UserEntity.java
+-- repository
|   +-- UserRepository.java
+-- service
    +-- UserService.java
    +-- UserServiceImpl.java
```

### Layer Responsibilities

- `model`: JPA entities mapped to database tables.
- `repository`: database access through Spring Data JPA.
- `service`: business logic and orchestration between controllers and repositories.
- `controller`: REST endpoints exposed to the frontend.
- `dto`: objects used to define API input and output data.

## Current Database Model

The first implemented table is `users`, mapped by `UserEntity`.

Current fields:

- `id`
- `name`
- `surname`
- `email`
- `password_hash`
- `global_role`
- `main_instrument`
- `location`
- `bio`
- `looking_for_band`
- `created_at`
- `active`

## Local Setup

### Requirements

- Java 21
- PostgreSQL running locally, currently through Docker
- DBeaver or another PostgreSQL client
- Maven wrapper or a local Maven installation

### Database

Create a PostgreSQL database named:

```sql
CREATE DATABASE bandhub;
```

The current local configuration expects:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/bandhub
spring.datasource.username=postgres
spring.datasource.password=1234
```

These values are defined in:

```text
src/main/resources/application.properties
```

### Running The Backend

The application can be started from an IDE or from the Spring Boot extension in VS Code.

Expected local URL:

```text
http://localhost:8080
```

Spring Security is currently using its default generated development password. A custom authentication flow will be implemented later.

## Current API

### Get all users

```http
GET /api/users
```

Returns a list of users using `UserResponseDTO`.

The response intentionally excludes `passwordHash`.

## Development Decisions

- Code, packages, classes, methods, and variables are written in English.
- Explanations and learning notes are written in Spanish.
- The project uses layered architecture from the beginning.
- Controllers should depend on services, not repositories.
- Services expose application use cases.
- Repositories contain persistence operations and custom queries.
- DTOs are used to avoid exposing entities directly through the API.
- JPA/Hibernate currently manages schema updates in development with `ddl-auto=update`.
- Flyway may be introduced later for professional database migrations.

## Next Steps

- Refactor dependency injection from field injection with `@Autowired` to constructor injection.
- Confirm `UserController` uses `@RestController` for REST JSON responses.
- Implement `POST /api/users`.
- Create `UserCreateRequestDTO`.
- Add validation with `@Valid`, `@NotBlank`, `@Email`, and `@Size`.
- Add service methods for user creation.
- Add repository methods such as `existsByEmail`.
- Later: configure Spring Security properly.

## Development Log

This section is used as a daily project journal. Each session should include the date, what changed, what was learned, and what remains pending.

### 2026-06-25

Initial backend setup.

Changes:

- Created Spring Boot backend project.
- Added dependencies for Web, JPA, Security, Validation, PostgreSQL, Lombok, DevTools, and testing.
- Configured PostgreSQL connection for local development.
- Created first JPA entity: `UserEntity`.
- Verified Hibernate creates the `users` table in PostgreSQL.
- Created `UserRepository` extending `JpaRepository`.
- Added first layered structure with controller, service, repository, model, and DTO packages.
- Added `UserService` interface and `UserServiceImpl` implementation.
- Added `UserResponseDTO`.
- Added first endpoint: `GET /api/users`.

Learned:

- Difference between PostgreSQL server, database, schema, and table.
- Difference between `NULL` and empty strings.
- Purpose of `@Entity`, `@Table`, `@Id`, `@GeneratedValue`, and `@Column`.
- Why DTOs help avoid exposing sensitive fields such as `passwordHash`.
- Difference between repository CRUD methods and service-level application methods.
- Why constructor injection is generally preferred over field injection.
- Difference between `@Controller` and `@RestController`.

Pending:

- Apply constructor injection in controller and service implementation.
- Change `UserController` to `@RestController`.
- Implement `POST /api/users` in the next session.
