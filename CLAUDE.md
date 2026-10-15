# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

BandHub is a Spring Boot 3.5.16 (Java 21) + PostgreSQL backend for a musician/band platform (public discovery layer + private band-management layer). An Angular frontend is planned but not started. This is an early-stage learning project built to practice clean architecture, Spring Boot, JPA, REST, and security — only the `User` module exists so far.

## Commands

- Run: `./mvnw spring-boot:run` (requires PostgreSQL reachable per `src/main/resources/application.properties`; on Windows cmd use `mvnw.cmd`)
- Build: `./mvnw compile`
- Package: `./mvnw package`
- Test all: `./mvnw test`
- Test single class: `./mvnw test -Dtest=ClassName`

There is no meaningful test suite yet — only the default Spring context-load test.

## Database

Local PostgreSQL, database name `bandhub`, connection in `application.properties`:
`jdbc:postgresql://localhost:5433/bandhub` — note the non-default port `5433`.

`spring.jpa.hibernate.ddl-auto=update` lets Hibernate evolve the schema from entities automatically in dev. This does **not** reliably handle column renames — dropping/recreating tables locally is acceptable while there's no real data to preserve. Flyway migrations are a planned future step, not yet introduced.

## Architecture

Strict layered architecture: controller → service (interface + Impl) → repository → model, with DTOs as the API boundary.

- `controller`: `@RestController`s under `/api/...`; depend only on service interfaces, never on repositories directly.
- `service`: interface + implementation pair (e.g. `UserService` / `UserServiceImpl`) — add both when introducing a new use case, not just the Impl.
- `repository`: Spring Data JPA interfaces; custom queries are derived from method names (e.g. `existsByEmail`).
- `model`: JPA entities using Lombok (`@Getter`/`@Setter`/`@NoArgsConstructor`); never returned directly from controllers.
- `dto`: request and response types are deliberately separate records (e.g. `UserCreateDTO` vs `UserResponseDTO`) even when fields overlap heavily — this is what keeps fields like `passwordHash` out of API responses.
- `exception` + `GlobalExceptionHandler` (`@RestControllerAdvice`): domain exceptions (e.g. `EmailAlreadyExistsException`) are mapped to HTTP statuses centrally rather than handled ad hoc in controllers/services. Every error response uses the single `ApiErrorResponse` shape (`status, error, message, path, timestamp, errors`), with `errors` populated only for field-level validation failures (`MethodArgumentNotValidException`).

## Security

`SecurityConfig` defines the `SecurityFilterChain` using explicit matchers per HTTP method and exact path (not wildcards). New endpoints are **not** public by default — anything not explicitly permitted falls under `anyRequest().authenticated()` and returns 403, even before login/JWT exists. There is no authentication flow yet; `PasswordEncoder` (BCrypt) is only used to hash passwords on user creation. CSRF is disabled, which is acceptable for a stateless REST API in development but should be revisited once auth is designed.

## Conventions

- Code (packages, classes, methods, variables) is written in English; inline comments and explanatory notes are written in Spanish — preserve this split when adding code.
- Email values are normalized (trim + lowercase) before duplicate checks and persistence, in the service layer.
- Constructor injection only — no field injection or `@Autowired` on fields.

## Project journal

`README.md` contains a "Development Log" (date, changes, what was learned, what's pending per session) — the historical record. `APP_ROADMAP.md` is the single source of truth for what's next, organized by phase/module → endpoints, with checkboxes. `STATE.md` is the fastest "where I left off" snapshot. Check all three before starting work — they reflect the actual current state faster than this file will.


## Learning context and working style

This project is a personal learning exercise to reinforce and apply knowledge
from DAW (Desarrollo de Aplicaciones Web) studies — not just a deliverable to
ship. Prioritize teaching and explaining over speed.

- The user writes all the code themselves. Claude acts as a guide/mentor:
  explain what to do and why, review code the user writes, and suggest
  improvements — do not write or edit code files unless explicitly asked to
  do so for a specific instance.
- Prefer explaining concepts (Spring/JPA/Security mechanics, design
  reasoning, trade-offs) over just stating the answer.
- Explain concepts simply, in plain language, as if teaching someone who is
  still learning — avoid unnecessary jargon, and when a technical term is
  needed, explain it in the same breath. Favor short, concrete examples or
  analogies over abstract descriptions.
- When reviewing code, point out not just bugs but what a DAW student should
  understand from the mistake (why it's wrong, what concept it touches).
- When a future project need touches a technology or concept the user hasn't
  covered yet (e.g. AWS S3/RDS when file/audio storage comes up, JWT when
  authentication is implemented, Flyway when migrations are introduced),
  flag it proactively: name the concept, briefly say why it's needed at that
  point, and suggest what to learn/read before or while implementing it —
  don't assume it's already known just because it appears in the roadmap.

## Bootcamp (docs/BOOTCAMP.md)

The user follows a learning plan built around BandHub, described in
`docs/BOOTCAMP.md`. Each `BH-xx` ticket there is also a GitHub issue on the
"BandHub Bootcamp" project board, and is referenced next to its item in
`APP_ROADMAP.md`.

- One ticket in progress at a time, no deadlines. Consistency over speed.
- When the user asks "¿dónde estaba?" (or similar) at the start of a session,
  read the Aparcamiento block in `STATE.md` and answer in three lines: ticket,
  where they left off, what to do now.
- Support lessons in `BOOTCAMP.md` are optional; suggest one only when the
  current ticket needs a concept the user hasn't covered.
- Monthly review (first Friday of the month, or when asked): ask the user to
  explain three concepts from `CONCEPTS.md` without looking, then give
  feedback. Concepts they can't explain yet go back on the list.

## Concept glossary (CONCEPTS.md)

Whenever a new or unclear concept comes up during the session (a Spring/JPA
mechanism, a security pattern, an HTTP/REST concept, etc.), add an entry for
it to `CONCEPTS.md` at the repo root. Create the file if it doesn't exist
yet.

Each entry must include:

- **Concept name** (as a heading).
- A simple explanation, in plain language, avoiding unnecessary jargon.
- A concrete example, ideally taken from this project's own code.
- A everyday-life analogy that makes the concept easier to remember.

Entry format:

```markdown
## <Concept name>

**Explicación:** <plain-language explanation>

**Ejemplo en el proyecto:** <short example from BandHub's code>

**Analogía:** <everyday-life analogy>
```

Only add an entry the first time a concept comes up, or when it's revisited
in a way that changes/deepens the earlier explanation — don't duplicate
entries for the same concept.

## Session close

When the user signals they're ending the session for now (phrases like
"hasta mañana", "lo dejamos aquí", "nos vemos", "paro por hoy", or similar —
in Spanish or English), before responding, update the project's living docs
to reflect what happened in this session:

- `STATE.md`: always rewrite the **Aparcamiento** block at the top first,
  with exactly four lines: current ticket (BH-xx and title), last step done,
  next step, and current branch. The next step must be a concrete action
  that can be started in 5 minutes, naming the file or class (e.g. "write the
  404 test in `UserServiceImplTest`"), never something vague like "continue
  BH-02". Then update the rest of the file as before — current phase, last
  file(s) touched, open decisions or blockers.
- GitHub issue of the current ticket: if `gh` is available and authenticated,
  tick the acceptance criteria completed this session and, when the ticket is
  done, close it (the board moves it to Done). If `gh` is not available,
  remind the user in one line which boxes to tick.
- `APP_ROADMAP.md`: check off any completed items from this session, and add newly agreed next steps in the right phase.
- `README.md` Development Log: add a new dated entry (changes, what was
  learned, what's pending), following the existing entries' format.

Summarize briefly in the reply what was updated — don't ask for permission
first, since this is just documentation reflecting work already done in the
session.