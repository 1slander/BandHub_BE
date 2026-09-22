# APPROADMAP.md

Guía viva de construcción de BandHub, organizada por fases y por
módulo/controller → endpoints. A diferencia del "Development Log" del
`README.md` (que registra lo que ya pasó, sesión a sesión), este documento
mira hacia delante: qué falta por construir.

Este documento se actualiza a medida que avanza la app: se marcan los puntos
completados, se ajustan fases si el diseño cambia, y se añaden fases o ideas
nuevas según van surgiendo (no hay que planificarlo todo de antemano).

---

## Fase 1 — Cerrar el módulo User

`UserController`

- [x] `GET /api/users` — listado
- [x] `POST /api/users` — creación
- [ ] `GET /api/users/{id}` — con `UserNotFoundException` + 404 (en curso)
- [ ] `PUT /api/users/{id}` — actualizar perfil propio (nombre, instrumento, ubicación, bio, lookingForBand)
- [ ] `DELETE /api/users/{id}` — baja lógica (`active = false`, no borrado físico)
- [ ] Revisar naming de DTOs y limpiar comentarios temporales

## Fase 2 — Autenticación (bloqueante para el resto)

`AuthController`

- [ ] `POST /api/auth/register` — puede reusar o sustituir el actual `POST /api/users`
- [ ] `POST /api/auth/login` — valida credenciales, devuelve JWT
- [ ] `POST /api/auth/refresh` — renovar token (más adelante)

Piezas de soporte:

- [ ] `JwtService` / `JwtUtil` (generar y validar tokens)
- [ ] Filtro de autenticación (`OncePerRequestFilter` o similar) que lea el JWT de cada request
- [ ] Actualizar `SecurityConfig`: sustituir los `permitAll` sueltos por reglas basadas en autenticación real
- [ ] `UserDetailsService` propio que cargue `UserEntity` para Spring Security

## Fase 3 — Band (primer módulo de dominio nuevo)

`BandController`

- [ ] `POST /api/bands` — crear banda (el creador pasa a ser el primer miembro con rol "owner")
- [ ] `GET /api/bands` — listado público de bandas
- [ ] `GET /api/bands/{id}` — detalle de una banda
- [ ] `PUT /api/bands/{id}` — editar datos de banda (solo owner/admin de esa banda)
- [ ] `DELETE /api/bands/{id}` — baja lógica de banda

Piezas nuevas: `BandEntity`, `BandRepository`, `BandService`/`Impl`, DTOs
(`BandCreateDTO`, `BandResponseDTO`).

## Fase 4 — BandMember (roles dentro de banda)

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

`RehearsalController`

- [ ] `POST /api/bands/{bandId}/rehearsals`
- [ ] `GET /api/bands/{bandId}/rehearsals`
- [ ] `PUT /api/bands/{bandId}/rehearsals/{id}`
- [ ] `DELETE /api/bands/{bandId}/rehearsals/{id}`

`SongController` (repertorio)

- [ ] `POST /api/bands/{bandId}/songs`
- [ ] `GET /api/bands/{bandId}/songs`
- [ ] `PUT /api/bands/{bandId}/songs/{id}`
- [ ] `DELETE /api/bands/{bandId}/songs/{id}`

`SetlistController`

- [ ] `POST /api/bands/{bandId}/setlists`
- [ ] `GET /api/bands/{bandId}/setlists/{id}` — incluye canciones ordenadas
- [ ] `PUT /api/bands/{bandId}/setlists/{id}` — reordenar/editar canciones

`ConcertController`

- [ ] `POST /api/bands/{bandId}/concerts`
- [ ] `GET /api/bands/{bandId}/concerts`
- [ ] `PUT /api/bands/{bandId}/concerts/{id}` — puede vincular un setlist

`FileController` (partituras/acordes; antes de S3, almacenamiento local o en BBDD)

- [ ] `POST /api/bands/{bandId}/songs/{songId}/files` — subir archivo
- [ ] `GET /api/bands/{bandId}/songs/{songId}/files` — listar
- [ ] `DELETE /api/bands/{bandId}/songs/{songId}/files/{id}`

## Fase 6 — Infraestructura transversal

- [ ] Introducir Flyway (sustituir `ddl-auto=update`)
- [ ] Migrar almacenamiento de ficheros a AWS S3
- [ ] Desplegar backend en AWS (EC2 o similar)
- [ ] Base de datos en AWS RDS

## Fase 7 — Angular

- [ ] Setup del proyecto Angular + estructura de carpetas por feature
- [ ] Módulo de autenticación (login/registro, guardas de ruta, interceptor HTTP para el JWT)
- [ ] Perfil de usuario (consume `UserController`)
- [ ] Bandas: listado público + detalle + gestión (consume `BandController`/`BandMemberController`)
- [ ] Backstage: pantallas de ensayos, repertorio, setlists, conciertos, ficheros — cada una en su propia vertical slice junto con el endpoint correspondiente de la Fase 5

---

## Ideas futuras / sin fase asignada aún

Espacio para anotar ideas que surjan durante el desarrollo, antes de
encajarlas en una fase concreta.

-
