# STATE.md

Snapshot rápido de "dónde lo dejé". Se actualiza al final de cada sesión,
antes de cerrar. Léelo primero al retomar — es más rápido que repasar el
Development Log o el APP_ROADMAP enteros.

---

**Última actualización:** 2026-09-30

**Fase actual:** Fase 1 (cerrada la parte de `GET /api/users/{id}`) +
arrancando la primera tarea transversal de Fase 6 (`docker-compose`) — ver
`APP_ROADMAP.md`.

**Último fichero tocado:** ninguno de código esta sesión (fue toda de
verificación + documentación). Los últimos ficheros de código tocados siguen
siendo los de la sesión anterior: `UserNotFoundException.java`,
`UserService.java`/`UserServiceImpl.java` (`findUserId`), `UserController.java`
(`GET /{userId}` con `@PathVariable`), `SecurityConfig.java` (matcher para
esa ruta).

**Siguiente paso concreto:**
1. Decidir si el `docker-compose.yml` reutiliza el volumen actual
   (`mi_db_puerto_nuevo`, conserva los datos que ya tienes) o arranca con un
   volumen nuevo con nombre propio (ej. `bandhub_pg_data`, tocaría recrear la
   BD `bandhub` a mano tras el primer `up`).
2. Escribir `docker-compose.yml` replicando el contenedor `postgres-server`
   actual: imagen `postgres:16`, puerto `5433:5432`,
   `POSTGRES_PASSWORD=1234`, volumen con nombre montado en
   `/var/lib/postgresql/data`.
3. Probar `docker compose up -d` / `docker compose down` y confirmar que la
   API sigue conectando igual.
4. Después: seguir con lo que queda de Fase 1 (`PUT`/`DELETE` de user,
   naming de DTOs, testing) o saltar a Fase 2 (auth/JWT) — a decidir.

**Decisiones pendientes / sin cerrar:**
- Volumen del `docker-compose.yml`: reusar `mi_db_puerto_nuevo` vs. crear uno
  nuevo (ver paso 1 arriba).

**Bloqueos:** ninguno.

**Notas rápidas de contexto:**
- Base de datos: PostgreSQL vía Docker Desktop, puerto 5433 (ver
  `application.properties`). Contenedor manual actual: `postgres-server`
  (imagen `postgres:16`), no gestionado aún por compose.
- El roadmap de "próximos pasos" de toda la app vive en `APP_ROADMAP.md`
  (antes `APPROADMAP.md`, renombrado esta sesión) — el `README.md` ya no
  duplica esa lista, solo referencia el fichero.
- IONOS se usará como entorno de práctica de despliegue tipo "pre" (como en
  el trabajo del usuario: local → pre → prod-AWS), antes de tocar AWS real —
  ver Fase 6 de `APP_ROADMAP.md`.
