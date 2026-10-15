# APP_ROADMAP.md

Guía viva de construcción de BandHub, organizada por fases y por
módulo/controller → endpoints. A diferencia del "Development Log" del
`README.md` (que registra lo que ya pasó, sesión a sesión), este documento
mira hacia delante: qué falta por construir.

Este documento se actualiza a medida que avanza la app: se marcan los puntos
completados, se ajustan fases si el diseño cambia, y se añaden fases o ideas
nuevas según van surgiendo (no hay que planificarlo todo de antemano).

Los identificadores **BH-xx** enlazan cada punto con su ticket en
`docs/BOOTCAMP.md` y con su issue en el tablero "BandHub Bootcamp" de GitHub.

---

## Fase 1 — Cerrar el módulo User

`UserController`

- [x] `GET /api/users` — listado
- [x] `POST /api/users` — creación
- [x] `GET /api/users/{id}` — con `UserNotFoundException` + 404
- [ ] `PUT /api/users/{id}` — actualizar perfil propio (nombre, instrumento, ubicación, bio, lookingForBand) · **BH-01**
- [ ] `DELETE /api/users/{id}` — baja lógica (`active = false`, no borrado físico) · **BH-02**
- [ ] Revisar naming de DTOs y limpiar comentarios temporales · **BH-03**
- [ ] Introducir testing (unit tests de services con repositorios mockeados, integration tests de controllers) — empezar ya, de forma incremental, no esperar a que crezca más el código · **BH-04**

## Fase 2 — Autenticación (bloqueante para el resto)

`AuthController`

- [ ] `POST /api/auth/register` — puede reusar o sustituir el actual `POST /api/users` · **BH-05**
- [ ] `POST /api/auth/login` — valida credenciales, devuelve JWT · **BH-05**
- [ ] `POST /api/auth/refresh` — renovar token (más adelante)

Piezas de soporte:

- [ ] `JwtService` / `JwtUtil` (generar y validar tokens) · **BH-05**
- [ ] Filtro de autenticación (`OncePerRequestFilter` o similar) que lea el JWT de cada request · **BH-06**
- [ ] Actualizar `SecurityConfig`: sustituir los `permitAll` sueltos por reglas basadas en autenticación real · **BH-06**
- [ ] `UserDetailsService` propio que cargue `UserEntity` para Spring Security · **BH-05**

## Fase 3 — Band (primer módulo de dominio nuevo) · **BH-13**

`BandController`

- [ ] `POST /api/bands` — crear banda (el creador pasa a ser el primer miembro con rol "owner")
- [ ] `GET /api/bands` — listado público de bandas
- [ ] `GET /api/bands/{id}` — detalle de una banda
- [ ] `PUT /api/bands/{id}` — editar datos de banda (solo owner/admin de esa banda)
- [ ] `DELETE /api/bands/{id}` — baja lógica de banda

Piezas nuevas: `BandEntity`, `BandRepository`, `BandService`/`Impl`, DTOs
(`BandCreateDTO`, `BandResponseDTO`).

## Fase 4 — BandMember (roles dentro de banda) · **BH-14**

`BandMemberController` (anidado bajo `/api/bands/{bandId}/members`)

- [ ] `POST /api/bands/{bandId}/members` — invitar/añadir miembro
- [ ] `GET /api/bands/{bandId}/members` — listar miembros y su rol
- [ ] `PUT /api/bands/{bandId}/members/{userId}` — cambiar rol del miembro (owner/admin/member)
- [ ] `DELETE /api/bands/{bandId}/members/{userId}` — sacar miembro de la banda

Nota de diseño: distinguir `UserRole` (rol global de la plataforma:
USER/ADMIN) de un rol propio por banda — probablemente un enum
`BandMemberRole` (OWNER/ADMIN/MEMBER) en una entidad puente con atributo
extra.

**A partir de aquí, según el roadmap del README, el frontend Angular puede
empezar a avanzar en paralelo, feature a feature.**

## Fase 5 — Backstage: resto del dominio privado

`RehearsalController` · **BH-22**

- [ ] `POST /api/bands/{bandId}/rehearsals`
- [ ] `GET /api/bands/{bandId}/rehearsals`
- [ ] `PUT /api/bands/{bandId}/rehearsals/{id}`
- [ ] `DELETE /api/bands/{bandId}/rehearsals/{id}`

`SongController` (repertorio) · **BH-15**

- [ ] `POST /api/bands/{bandId}/songs`
- [ ] `GET /api/bands/{bandId}/songs`
- [ ] `PUT /api/bands/{bandId}/songs/{id}`
- [ ] `DELETE /api/bands/{bandId}/songs/{id}`

`SetlistController` · **BH-15**

- [ ] `POST /api/bands/{bandId}/setlists`
- [ ] `GET /api/bands/{bandId}/setlists/{id}` — incluye canciones ordenadas
- [ ] `PUT /api/bands/{bandId}/setlists/{id}` — reordenar/editar canciones

`ConcertController`

- [ ] `POST /api/bands/{bandId}/concerts`
- [ ] `GET /api/bands/{bandId}/concerts`
- [ ] `PUT /api/bands/{bandId}/concerts/{id}` — puede vincular un setlist

`FileController` (partituras/acordes; antes de S3, almacenamiento local o en BBDD) · **BH-21**

- [ ] `POST /api/bands/{bandId}/songs/{songId}/files` — subir archivo
- [ ] `GET /api/bands/{bandId}/songs/{songId}/files` — listar
- [ ] `DELETE /api/bands/{bandId}/songs/{songId}/files/{id}`

## Fase 6 — Infraestructura transversal

Simula el flujo de trabajo real: local (Docker) → "pre" de práctica (IONOS,
sin coste) → "prod" (AWS). El objetivo de IONOS no es sustituir AWS, sino
practicar el despliegue en un entorno de bajo riesgo antes de hacerlo en la
nube de verdad, una vez exista el baseline de JWT + `Band` (Fases 2-3).

- [x] Levantar la base de datos local con `docker-compose` (`postgres:16`, volumen con nombre, `POSTGRES_DB` auto-crea `bandhub`)
- [ ] Introducir Flyway (sustituir `ddl-auto=update`) · **BH-06**
- [ ] Integración continua con GitHub Actions (build + tests en cada push y PR) · **BH-CI**
- [ ] Tests de integración con Testcontainers (Postgres real en los tests) · **BH-06**
- [ ] Dockerizar la app y añadirla al `docker-compose.yaml` · **BH-08**
- [ ] Desplegar el backend en el servidor IONOS personal — práctica de despliegue ("pre") · **BH-M2**
- [ ] Migrar almacenamiento de ficheros a AWS S3 · **BH-10** (avatar), **BH-21** (partituras)
- [ ] Desplegar backend en AWS (EC2 o similar) — ("prod") · **BH-11** (EC2), **BH-23** (Fargate)
- [ ] Base de datos en AWS RDS · **BH-20**

## Fase 7 — Angular

- [ ] Setup del proyecto Angular + estructura de carpetas por feature · **BH-16**
- [ ] Módulo de autenticación (login/registro, guardas de ruta, interceptor HTTP para el JWT)
- [ ] Perfil de usuario (consume `UserController`)
- [ ] Bandas: listado público + detalle + gestión (consume `BandController`/`BandMemberController`)
- [ ] Backstage: pantallas de ensayos, repertorio, setlists, conciertos, ficheros — cada una en su propia vertical slice junto con el endpoint correspondiente de la Fase 5

---

## Ideas futuras / sin fase asignada aún

Espacio para anotar ideas que surjan durante el desarrollo, antes de
encajarlas en una fase concreta.

-
