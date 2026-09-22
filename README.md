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
+-- config
|   +-- SecurityConfig.java
+-- dto
|   +-- UserResponseDTO.java
|   +-- UserCreateDTO.java
|   +-- ApiErrorResponse.java
+-- model
|   +-- UserEntity.java
|   +-- UserRole.java
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
- `exception`: custom exceptions and global REST API error handling.

## Current Database Model

The first implemented table is `users`, mapped by `UserEntity`.

Current fields:

- `id`
- `name`
- `surname`
- `email`
- `password_hash`
- `role`
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
spring.datasource.url=jdbc:postgresql://localhost:5433/bandhub
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

Spring Security is currently configured to allow the first public user endpoints. A custom authentication flow will be implemented later.

## Current API

### Get all users

```http
GET /api/users
```

Returns a list of users using `UserResponseDTO`.

The response intentionally excludes `passwordHash`.

### Create user

```http
POST /api/users
```

Current status: endpoint, service implementation, password hashing, security access, and manual testing completed.

Expected request DTO: `UserCreateDTO`.

Expected response DTO: `UserResponseDTO`.

Current behavior:

- Creates a user in PostgreSQL.
- Hashes the password with BCrypt before saving.
- Normalizes email values before checking duplicates and saving.
- Returns public user data through `UserResponseDTO`.
- Does not expose `passwordHash` in the API response.

### Error responses

The API uses `ApiErrorResponse` for controlled errors.

Example:

```json
{
  "status": 409,
  "error": "Conflict",
  "message": "Email already exists: admin@mail.com",
  "path": "/api/users",
  "timestamp": "2026-07-16T18:00:00",
  "errors": null
}
```

Validation errors return field-level messages in `errors`.

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

- Add `findUserById` to `UserService` and implement it in `UserServiceImpl`.
- Add `GET /api/users/{id}` to `UserController`.
- Update `SecurityConfig` to permit `GET /api/users/{id}` (current matchers are exact paths, not wildcards, so this endpoint would 403 as-is).
- Manually test both cases: existing id and non-existing id (404).
- Review and standardize DTO file/class naming.
- Introduce Flyway later for professional database migrations.

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

- Implement `POST /api/users` in the next session.

### 2026-06-29

Prepared user creation endpoint contract.

Changes:

- Confirmed `UserController` uses `@RestController`.
- Confirmed controller and service implementation use constructor injection.
- Created `UserCreateDTO` as a request DTO for user creation.
- Added validation annotations to `UserCreateDTO`: `@NotBlank`, `@Email`, and `@Size`.
- Added `POST /api/users` endpoint signature in `UserController`.
- Added `@Valid` to trigger request DTO validation.
- Changed the create-user flow to receive `UserCreateDTO` instead of `UserEntity`.
- Changed the create-user response type to `UserResponseDTO`.
- Added `createUser(UserCreateDTO user)` to `UserService`.
- Added `existsByEmail(String email)` to `UserRepository`.

Learned:

- Why request DTOs and response DTOs should be different.
- Why controllers should not receive JPA entities directly from the API.
- How `@Valid` activates Bean Validation annotations on request bodies.
- Why `ResponseEntity.status(HttpStatus.CREATED).body(...)` is simpler than `ResponseEntity.created(...)` when no resource URI is being returned yet.
- How Spring Data JPA derives queries from method names such as `existsByEmail`.

Pending:

- Implement `UserServiceImpl.createUser`.
- Decide how to handle duplicated emails.
- Decide where password hashing will happen before integrating Spring Security properly.
- Remove unused imports if they remain after the implementation.

### 2026-06-30

Implemented the user creation service and password hashing.

Changes:

- Implemented `UserServiceImpl.createUser`.
- Added duplicate email checking with `existsByEmail`.
- Added the mapping from `UserCreateDTO` to `UserEntity`.
- Saved the user through `UserRepository` and returned `UserResponseDTO`.
- Renamed `globalRole` to `role`.
- Created the `UserRole` enum with `USER` and `ADMIN` values.
- Configured enum persistence with `@Enumerated(EnumType.STRING)`.
- Added `SecurityConfig` with a `PasswordEncoder` bean.
- Injected `PasswordEncoder` into `UserServiceImpl` using constructor injection.
- Added BCrypt password hashing before persistence.

Learned:

- Difference between a global application role and a role within a band.
- Why enums are safer than unrestricted strings for predefined roles.
- How `@Configuration` and `@Bean` register objects managed by Spring.
- Why passwords must never be stored as plain text.
- How BCrypt uses a salt and verifies passwords without decrypting their hashes.
- Why constructor injection does not require `@Autowired` when there is only one constructor.

Pending:

- Configure the Spring Security filter chain for the REST API.
- Test `POST /api/users` and inspect the stored BCrypt hash.
- Return an appropriate HTTP error when an email is already registered.

### 2026-07-08

Configured the first public security rules and manually tested user creation.

Changes:

- Added a `SecurityFilterChain` in `SecurityConfig`.
- Allowed public access to `POST /api/users` for user registration.
- Allowed public access to `GET /api/users` for development testing.
- Disabled CSRF temporarily for the REST API development phase.
- Tested `POST /api/users` successfully.
- Verified that the API returns a `UserResponseDTO` without the password hash.
- Detected and fixed the local database mismatch caused by the old `global_role` column after renaming it to `role`.
- Confirmed duplicated email detection in `UserServiceImpl`.
- Confirmed Bean Validation works for invalid request fields.

Learned:

- A `403 Forbidden` can come from Spring Security, but logs must be checked before assuming the cause.
- If Hibernate reaches `select` and `insert`, the request passed the security filter chain.
- `ddl-auto=update` does not reliably handle column renames.
- During early development, dropping and recreating a table can be acceptable when there is no valuable data.
- `SecurityFilterChain` defines which requests are public and which require authentication.
- `csrf().disable()` is acceptable temporarily for a stateless REST API under development, but must be revisited when authentication is designed.

Pending:

- Replace the temporary `IllegalArgumentException` with a custom exception.
- Add `GlobalExceptionHandler` with `@RestControllerAdvice`.
- Return `409 Conflict` when an email is already registered.
- Improve validation error responses for invalid request bodies.
- Normalize email values before checking duplicates and saving.

### 2026-07-16

Improved API error handling and email consistency.

Changes:

- Created `EmailAlreadyExistsException` as a domain-specific exception.
- Added `GlobalExceptionHandler` with `@RestControllerAdvice`.
- Replaced the temporary `IllegalArgumentException` in `UserServiceImpl`.
- Created `ApiErrorResponse` as the common error response DTO.
- Changed duplicated email errors to return `409 Conflict`.
- Added handling for `MethodArgumentNotValidException`.
- Validation errors now return field-level messages using `Map<String, String>`.
- Added email normalization before checking duplicates and saving users.
- Kept `normalizeEmail` as a private method in `UserServiceImpl` because it is only used there for now.

Learned:

- How `@ExceptionHandler` maps exceptions to HTTP responses.
- Why `@RestControllerAdvice` is useful for global API error handling.
- What `WebRequest` represents and how it provides request context such as the URI path.
- Why API errors should return structured JSON instead of plain strings.
- Why `Map<String, String>` fits field validation errors.
- Difference between the `Map` abstraction and the `HashMap` implementation.
- Why emails should be normalized before duplicate checks.

Pending:

- Add `GET /api/users/{id}`.
- Add `UserNotFoundException`.
- Return `404 Not Found` using `ApiErrorResponse`.
- Clean temporary comments in `UserServiceImpl`.
- Consider `Locale.ROOT` in email normalization later.

### 2026-09-22

Started the `GET /api/users/{id}` feature: created the domain exception and its
error handling. Also added project documentation for AI-assisted sessions.

Changes:

- Created `UserNotFoundException` in the `exception` package, following the same pattern as `EmailAlreadyExistsException`.
- Added `handleUserNotFound` to `GlobalExceptionHandler`, mapping `UserNotFoundException` to `404 Not Found` via `ApiErrorResponse`.
- Added `CLAUDE.md` with project context, commands, architecture notes, and working-style guidance for AI-assisted sessions.
- Added `CONCEPTS.md` as a personal glossary for concepts learned during development, with its first entry on checked vs unchecked exceptions.

Learned:

- The real distinction between checked and unchecked exceptions is whether the compiler forces a `try/catch` or `throws` declaration before it will compile, not when the error happens at runtime.
- Why domain exceptions in this project extend `RuntimeException` (unchecked): a checked exception would force `throws UserNotFoundException` onto `UserService`, `UserServiceImpl`, and the controller method, just so `GlobalExceptionHandler` can intercept it higher up. Unchecked exceptions propagate to the `@RestControllerAdvice` without polluting every signature in between.

Pending:

- Add `findUserById` to `UserService` and implement it in `UserServiceImpl`.
- Add `GET /api/users/{id}` to `UserController`.
- Update `SecurityConfig` to permit `GET /api/users/{id}`.
- Manually test both cases: existing id and non-existing id (404).
