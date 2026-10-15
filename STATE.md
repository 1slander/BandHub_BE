# STATE.md

Snapshot rápido de "dónde lo dejé". Se actualiza al final de cada sesión,
antes de cerrar. Léelo primero al retomar — es más rápido que repasar el
Development Log o el APP_ROADMAP enteros.

---

## Aparcamiento (léelo primero)

- **Ticket:** BH-01 · `PUT /api/users/{id}`
- **Último paso hecho:** plan de aprendizaje montado (`docs/BOOTCAMP.md`). Falta ejecutar `scripts/create-bootcamp-issues.ps1` para crear las issues y el tablero.
- **Siguiente paso (se empieza en 5 min):** crear la rama `feature/BH-01-update-user` y escribir el record `UserUpdateDTO` con sus validaciones.
- **Rama:** ninguna todavía (estás en `main`).

---

**Última actualización:** 2026-10-01

**Fase actual:** Fase 1 — retomar el resto del módulo `User` (ver
`APP_ROADMAP.md`). La tarea transversal de `docker-compose` (Fase 6) quedó
cerrada esta sesión.

**Último fichero tocado:** `docker-compose.yaml` (nuevo, en la raíz del
proyecto) — servicio `postgres` con imagen `postgres:16`, puerto
`5433:5432`, `POSTGRES_PASSWORD=1234`, `POSTGRES_DB=bandhub`, volumen nuevo
con nombre `bandhub_pg_database`.

**Siguiente paso concreto (retomar Fase 1):**
1. Decidir por cuál seguir: `PUT /api/users/{id}` (actualizar perfil propio),
   `DELETE /api/users/{id}` (baja lógica, `active = false`), revisar naming
   de DTOs, o empezar ya con testing (unit tests de `UserServiceImpl` con
   repositorio mockeado).
2. Seguir el mismo patrón ya usado en `findUserId`: `UserService`
   (interfaz) → `UserServiceImpl` → `UserController` → revisar si hace
   falta tocar `SecurityConfig`.

**Decisiones pendientes / sin cerrar:**
- ~~Añadir `restart: unless-stopped` al servicio de `docker-compose.yaml`.~~
  Ya aplicado en el compose.
- El contenedor manual antiguo `postgres-server` (`docker run`, sin
  compose) se deja **parado pero no borrado**, como backup, por si acaso.
  No se usa activamente — el que gestiona la BD ahora es
  `bandhub-postgres-1` vía compose.

**Bloqueos:** ninguno.

**Notas rápidas de contexto:**
- Para trabajar en el proyecto: `docker compose up -d` desde la raíz
  arranca Postgres (contenedor `bandhub-postgres-1`, puerto `5433`); no
  arranca solo si Docker Desktop se reinició entre medias (ver decisión
  pendiente de `restart: unless-stopped` arriba).
- `POSTGRES_DB=bandhub` en el compose hace que la base de datos se cree
  sola en el primer arranque del volumen (verificado: la API respondió
  `200 OK` con lista vacía sin crear nada a mano en DBeaver).
- DBeaver no necesita reconfiguración: se conecta a `localhost:5433` igual
  que antes, sin saber ni importarle qué contenedor hay detrás.
- El roadmap de "próximos pasos" de toda la app vive en `APP_ROADMAP.md`;
  el `README.md` solo referencia ese fichero, no duplica la lista.
