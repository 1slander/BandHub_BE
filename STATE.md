# STATE.md

Snapshot rápido de "dónde lo dejé". Se actualiza al final de cada sesión,
antes de cerrar. Léelo primero al retomar — es más rápido que repasar el
Development Log o el APPROADMAP enteros.

---

**Última actualización:** 2026-09-22

**Fase actual:** Fase 1 — Cerrar el módulo User (ver `APPROADMAP.md`)

**Último fichero tocado:** `GlobalExceptionHandler.java` (añadido
`handleUserNotFound`) y `UserNotFoundException.java` (nueva).

**Siguiente paso concreto:**
1. Añadir `findUserById` a `UserService` (interfaz) y `UserServiceImpl`.
2. Añadir `GET /api/users/{id}` en `UserController`.
3. Actualizar `SecurityConfig` para permitir esa ruta (los matchers actuales
   son exactos, no wildcards, así que esta ruta daría 403 tal y como está).
4. Probar manualmente: id existente y id inexistente (debe devolver 404).

**Decisiones pendientes / sin cerrar:** ninguna ahora mismo.

**Bloqueos:** ninguno.

**Notas rápidas de contexto:**
- Base de datos: PostgreSQL vía Docker Desktop, puerto 5433 (ver
  `application.properties`).
- Trabajando en un `docker-compose.yml` para la base de datos, como
  ejercicio de aprendizaje guiado — en curso.
