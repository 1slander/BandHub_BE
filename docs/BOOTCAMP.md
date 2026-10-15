# BOOTCAMP.md

Plan de aprendizaje construyendo BandHub. Vista visual y progreso: el roadmap en Claude ("Roadmap de Hector").

## Cómo funciona

- **Una sola línea de trabajo:** el ticket BH-xx que esté *En curso* en el tablero de GitHub. Solo uno a la vez, sin fecha límite.
- **Lecciones opcionales:** cada bloque trae cuatro lecciones de apoyo con su kata. Hazlas solo cuando el ticket te pida un concepto que no dominas. Las katas van en un repo aparte (`bootcamp-katas`).
- **Semana cumplida = al menos una sesión de ticket.**
- **Horario:** lunes a jueves 16:30–17:00; viernes 15:15–16:30 (bloque largo). Fin de semana libre.
- **El código lo escribe Hector.** Claude explica, revisa diffs y pregunta; no implementa (ver `CLAUDE.md`).
- **Ramas:** una por ticket, `feature/BH-xx-descripcion`, y se integra por PR.

## Rituales

- **Al sentarte:** leer el bloque *Aparcamiento* de `STATE.md` o preguntar "¿dónde estaba?".
- **Al terminar:** decir "paro por hoy". El cierre de sesión actualiza `STATE.md`, `APP_ROADMAP.md`, el Development Log y `CONCEPTS.md`, y las casillas de la issue.
- **Viernes:** una línea en el Development Log respondiendo: ¿qué ha generado la IA en el trabajo esta semana que yo no habría sabido escribir?
- **Primer viernes de cada mes:** repaso. Claude pregunta tres conceptos de `CONCEPTS.md` y se explican sin mirar.
- **Una vez al mes:** enseñar un ticket cerrado a un senior de la empresa (20 min).
- **Cada 6 tickets:** revisión del plan. Qué ha costado, qué recortar, qué viene.

## Fase 1 · Java

*Oct – nov 2026.* Cierras la Fase 1 de tu APP_ROADMAP: módulo User completo y con tests.

### BH-01 · PUT /api/users/{id}

- [ ] APP_ROADMAP · Fase 1. Actualizar el perfil: name, mainInstrument, location, bio, lookingForBand.
- [ ] Un UserUpdateDTO propio: el email y la contraseña no se cambian por aquí.
- [ ] 404 si el usuario no existe (ya tienes UserNotFoundException).
- [ ] Matcher explícito en SecurityConfig, como hiciste con el GET.

<details><summary>Pistas</summary>

- Decide antes de escribir: ¿un campo null en el DTO significa "bórralo" o "no lo toques"? Apunta la decisión en el Development Log.
- Con JPA no hace falta llamar a save() si el método es @Transactional: el cambio en la entidad se guarda solo (dirty checking). Pruébalo y añádelo a CONCEPTS.md.

</details>

Lecciones de apoyo:

- **List y duplicados** — Crea una List<String> con 8 títulos de canciones, dos repetidos. Elimina los duplicados manteniendo el orden original (pista: LinkedHashSet) y luego ordénala alfabéticamente sin distinguir mayúsculas. ([dev.java · Collections Framework (oficial)](https://dev.java/learn/api/collections-framework/))
- **Set** — Tienes dos setlists como Set<String>. Calcula las canciones comunes (retainAll), las que solo están en el primero (removeAll) y la unión ordenada (TreeSet). Trabaja sobre copias para no romper los originales. ([dev.java · Collections Framework (oficial)](https://dev.java/learn/api/collections-framework/))
- **Map** — Dados 5 setlists (List<List<String>>), cuenta cuántas veces se ha tocado cada canción en un Map<String,Integer>. Hazlo primero con getOrDefault y luego con merge. ([dev.java · Collections Framework (oficial)](https://dev.java/learn/api/collections-framework/))
- **equals, hashCode y Comparator** — Crea una clase Cancion(titulo, artista, duracionSeg). Mete dos iguales en un HashSet y comprueba que se duplican. Arréglalo con equals/hashCode. Después ordena una lista por duración descendente y, a igual duración, por título. ([Exercism · Java track (ejercicios con mentor)](https://exercism.org/tracks/java))

### BH-02 · Baja lógica y listados con streams

- [ ] DELETE /api/users/{id} pone active = false y devuelve 204.
- [ ] GET /api/users devuelve solo usuarios activos.
- [ ] Filtros opcionales ?instrument= y ?lookingForBand=true, primero resueltos con streams en el servicio.
- [ ] Después, mueve el filtro a una consulta del repositorio y anota en el Development Log por qué es mejor filtrar en base de datos.

<details><summary>Pistas</summary>

- El mapeo entidad → DTO queda en una línea: users.stream().map(...).toList().
- Spring Data deriva findByActiveTrue y findByActiveTrueAndMainInstrumentIgnoreCase del nombre del método.

</details>

Lecciones de apoyo:

- **filter, map, collect** — Del repertorio de la semana 1, saca los títulos en mayúsculas de las canciones de más de 4 minutos, en una List<String>. ([dev.java · Streams (oficial)](https://dev.java/learn/api/streams/))
- **mapToInt, sum, average** — Calcula la duración total del repertorio con mapToInt().sum() y devuélvela formateada como "mm:ss". Calcula también la duración media. ([dev.java · Streams (oficial)](https://dev.java/learn/api/streams/))
- **groupingBy y counting** — Construye un Map<String, Long> con el número de canciones por artista, y otro Map<String, List<String>> con los títulos por tonalidad. ([dev.java · Streams (oficial)](https://dev.java/learn/api/streams/))
- **sorted, limit, anyMatch, flatMap** — ¿Hay alguna canción en "Dm"? (anyMatch). Saca los 5 artistas distintos ordenados. Dada una List<Setlist> donde cada setlist tiene su List<Cancion>, obtén todas las canciones únicas con flatMap. ([Exercism · Java track (ejercicios con mentor)](https://exercism.org/tracks/java))

### BH-03 · DTOs y errores pulidos

- [ ] APP_ROADMAP · Fase 1: revisar el naming de los DTOs (UserCreateDTO, UserUpdateDTO, UserResponseDTO) y limpiar comentarios temporales.
- [ ] El mapeo entidad → DTO en un único sitio: un método estático UserResponseDTO.from(UserEntity).
- [ ] Locale.ROOT al normalizar emails, y entrada en CONCEPTS.md explicando por qué (el problema de la i turca).
- [ ] Un handler para cuerpos JSON mal formados (HttpMessageNotReadableException) que devuelva 400 con ApiErrorResponse.

<details><summary>Pistas</summary>

- Un record puede tener métodos estáticos y constructores compactos: es el sitio natural para from().
- Prueba "EMAIL@X.COM".toLowerCase(new Locale("tr")) para ver el bug con tus propios ojos.

</details>

Lecciones de apoyo:

- **Records** — Convierte Cancion en record. Añade validación en el constructor compacto: título no vacío y duración mayor que 0 (lanza IllegalArgumentException). ([dev.java · Records](https://dev.java/learn/records/))
- **Optional** — Crea buscarPorTitulo(String): Optional<Cancion>. Úsalo de tres formas: orElse con una canción por defecto, map para sacar solo el artista, y orElseThrow. ([Baeldung · Guía de Optional](https://www.baeldung.com/java-optional))
- **Excepciones** — Crea CancionNoEncontradaException (unchecked) con un mensaje que incluya el título buscado. Úsala en un método obtenerPorTitulo. Explica en dos frases en tu nota por qué la hiciste unchecked. ([Exercism · Java track (ejercicios con mentor)](https://exercism.org/tracks/java))
- **switch, sealed y text blocks** — Crea sealed interface Evento con records Ensayo(int minutos) y Concierto(String sala, int aforo). Con un switch con pattern matching, genera una descripción de cada evento. Exporta un setlist a texto usando un text block. ([roadmap.sh · Java](https://roadmap.sh/java))

### BH-04 · Testing del módulo User

- [ ] APP_ROADMAP · Fase 1: unit tests de UserServiceImpl con el repositorio mockeado (Mockito).
- [ ] @WebMvcTest de UserController: 201, 400 por validación, 404 y 409.
- [ ] Cubre los casos de PUT y DELETE de las semanas anteriores.
- [ ] Mínimo 15 tests. Comprueba la cobertura en VS Code.

<details><summary>Pistas</summary>

- Con Spring Security activo, @WebMvcTest puede devolverte 401 o 403. Importa tu SecurityConfig con @Import o usa @WithMockUser.
- @MockitoBean sustituye a @MockBean desde Spring Boot 3.4.
- Para el PasswordEncoder en el test del servicio, mockéalo también: el test no debe depender de BCrypt.

</details>

Lecciones de apoyo:

- **Primer test con JUnit 5** — Añade JUnit 5 a tu proyecto (Maven o Gradle). Escribe 3 tests de Repertorio: añadir una canción, rechazar un duplicado y duración total con repertorio vacío. ([JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/))
- **Tests parametrizados y excepciones** — Crea Formato.mmss(int seg) y pruébalo con 5 casos en un solo test parametrizado (0, 59, 60, 258, 3600). Comprueba con assertThrows que el record Cancion rechaza una duración negativa. ([JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/))
- **Mockito** — Crea una interfaz CancionRepository y un SetlistService que la use. Prueba el servicio con el repositorio mockeado: caso feliz y caso de canción inexistente. ([Baeldung · Serie Mockito](https://www.baeldung.com/mockito-series))
- **Tests a código de IA** — Pide a la IA una función que reparta canciones en dos sets de duración lo más parecida posible. No la leas a fondo: escribe 5 tests que definan lo que debe hacer. Luego introduce tú un bug y comprueba que algún test lo detecta. ([Exercism · Java track (ejercicios con mentor)](https://exercism.org/tracks/java))

## Fase 2 · Auth + Cloud

*Feb – mar 2027 (y lo que haga falta).* CI, login con JWT, Flyway y Testcontainers (tu Fase 2 del APP_ROADMAP), después Linux, Docker y AWS. Sin examen de Cloud Practitioner: curso gratuito como calentamiento y directo a la Associate.

### BH-CI · CI con GitHub Actions

- [ ] Workflow que compila y pasa los tests en cada push y en cada PR.
- [ ] Badge de estado en el README.
- [ ] Desde aquí, ningún merge a main con CI en rojo.

<details><summary>Pistas</summary>

- Usa la caché de Maven de setup-java.
- Una sola sesión: es el ticket ideal para volver después del permiso.

</details>

### BH-05 · Autenticación I: login con JWT

- [ ] APP_ROADMAP · Fase 2: UserDetailsService propio que cargue UserEntity por email.
- [ ] POST /api/auth/register (decide si sustituye a POST /api/users) y POST /api/auth/login, que devuelve un JWT.
- [ ] JwtService para generar y validar tokens. El secreto sale de configuración, nunca del código.
- [ ] Si el login falla, el mismo mensaje para email inexistente y contraseña incorrecta.

<details><summary>Pistas</summary>

- Esta semana las lecciones de beans e inyección te explican por qué basta con declarar un @Bean UserDetailsService para que Spring Security lo use.
- Dos caminos: la librería jjwt con tu propio filtro (lo que dice tu APP_ROADMAP) o spring-boot-starter-oauth2-resource-server. Para aprender, el primero enseña más.
- Ticket grande: si no cabe en el viernes, el sábado opcional existe para esto.

</details>

Lecciones de apoyo:

- **Arranque e inyección de dependencias** — Crea setlist-api con Spring Initializr (Web, Validation). Crea una interfaz Saludo con dos implementaciones @Component y resuelve el conflicto primero con @Primary y luego con @Qualifier. Anota en tu nota qué error salía sin ellos. ([Spring Academy (cursos gratuitos oficiales)](https://spring.academy/courses))
- **Perfiles y configuración** — Crea un record SetlistProperties con duracionMaximaMin, vinculado con @ConfigurationProperties. Dale 45 en el perfil dev y 90 en prod, y comprueba que cambia al arrancar con cada perfil. ([Spring Boot Reference](https://docs.spring.io/spring-boot/reference/))
- **Controladores, DTOs y validación** — Endpoints GET /api/canciones, GET /api/canciones/{id} y POST /api/canciones (guardando en memoria). Usa un record CancionRequest con @NotBlank y @Positive. Prueba con Postman o curl. ([Spring Guides (tutoriales cortos)](https://spring.io/guides))
- **Errores coherentes** — Haz que una canción inexistente devuelva 404 con ProblemDetail y que una validación fallida devuelva 400 con la lista de campos erróneos. ([Baeldung · Gestión de errores en REST](https://www.baeldung.com/exception-handling-for-rest-with-spring))

### BH-06 · Autenticación II + Flyway

- [ ] Filtro OncePerRequestFilter que lee el JWT de cada petición.
- [ ] SecurityConfig con reglas reales: register y login públicos, el resto autenticado. Un usuario solo puede editar o dar de baja su propio perfil.
- [ ] APP_ROADMAP · Fase 6: Flyway con V1 creando la tabla users tal como está hoy, y ddl-auto=validate.
- [ ] Tests: 401 sin token, 200 con token, 403 editando a otro usuario.
- [ ] Tests de integración con Testcontainers: un Postgres real en Docker que comprueba que tus migraciones y tus repositorios funcionan.

<details><summary>Pistas</summary>

- Tu base de datos local ya tiene datos: mira spring.flyway.baseline-on-migrate, o borra el volumen (es local y sin datos reales).
- Para la V1, puedes sacar el SQL que ya genera Hibernate activando show-sql y repasándolo a mano.

</details>

Lecciones de apoyo:

- **SQL básico** — Haz las lecciones 1 a 7 de SQLBolt. Si te sobra tiempo, escribe en papel la consulta: títulos del setlist 3 ordenados por posición, usando las tablas cancion, setlist y setlist_cancion. ([SQLBolt (ejercicios SQL interactivos)](https://sqlbolt.com/))
- **Entidades y relaciones** — Añade Spring Data JPA y H2. Convierte Cancion y Setlist en entidades con una relación @ManyToMany. Activa spring.jpa.show-sql=true y mira el SQL que se genera. ([Spring Guides (tutoriales cortos)](https://spring.io/guides))
- **El problema N+1** — Lista todos los setlists con sus canciones y cuenta las consultas en el log. Arréglalo con JOIN FETCH en una @Query y luego con @EntityGraph. Compara. ([Vlad Mihalcea · El problema N+1](https://vladmihalcea.com/n-plus-1-query-problem/))
- **@Transactional** — Crea un método de servicio que guarde un setlist y lance una excepción a mitad. Comprueba que sin @Transactional queda basura en la base de datos y con @Transactional no. ([Spring Boot Reference](https://docs.spring.io/spring-boot/reference/))

### BH-07 · Logs y script de diagnóstico

- [ ] Configura BandHub para escribir logs en fichero (logging.file.name).
- [ ] Script scripts/check-logs.sh: cuenta ERROR y WARN y muestra las 5 excepciones más repetidas.
- [ ] Sección en el README: cómo arrancar el proyecto desde cero.

<details><summary>Pistas</summary>

- grep -o, sort, uniq -c y sort -nr resuelven el top 5.
- Añade set -euo pipefail al principio del script y averigua qué hace.

</details>

Lecciones de apoyo:

- **Navegar sin miedo** — Haz los niveles 0 a 5 de Bandit. Apunta cada comando nuevo en un cheatsheet propio. ([OverTheWire Bandit (retos de terminal)](https://overthewire.org/wargames/bandit/))
- **Permisos y usuarios** — Crea un usuario, un grupo y un fichero que solo pueda leer ese grupo. Explica qué significa 750. ([Linux Journey](https://linuxjourney.com/))
- **Procesos y puertos** — Arranca BandHub y encuentra su proceso (ps), el puerto que escucha (ss o lsof) y páralo con una señal (kill). ([Linux Journey](https://linuxjourney.com/))
- **Pipes, grep y logs** — Niveles 6 a 10 de Bandit. Luego cuenta las líneas ERROR de un log con grep | wc. ([OverTheWire Bandit (retos de terminal)](https://overthewire.org/wargames/bandit/))

### BH-08 · BandHub dockerizado

- [ ] Dockerfile multi-stage: compila con el Maven wrapper y ejecuta en una imagen JRE 21 ligera.
- [ ] Añade el servicio de la app a tu docker-compose.yaml actual; perfil docker con la configuración por variables de entorno.
- [ ] Saca la contraseña 1234 del compose a un fichero .env que no se sube a Git.
- [ ] Un .dockerignore y una sección en el README.
- [ ] Un solo comando deja BandHub funcionando: docker compose up.

<details><summary>Pistas</summary>

- healthcheck en Postgres + depends_on con condition: service_healthy.
- SPRING_DATASOURCE_URL apunta al nombre del servicio, no a localhost.

</details>

Lecciones de apoyo:

- **Imagen vs contenedor** — Ejecuta postgres:16 en un contenedor, conéctate con psql y destrúyelo. ¿Qué se pierde? ([Docker · Get started](https://docs.docker.com/get-started/))
- **Dockerfile** — Escribe un Dockerfile para una app Java mínima de las katas. Mira el tamaño de la imagen. ([Docker · Get started](https://docs.docker.com/get-started/))
- **Volúmenes y redes** — Repite el Postgres del lunes con un volumen y comprueba que los datos sobreviven. ([Docker · Get started](https://docs.docker.com/get-started/))
- **Docker Compose** — Levanta Postgres + pgAdmin con un docker-compose.yml. ([Docker Compose docs](https://docs.docker.com/compose/))

### BH-09 · Cuenta AWS segura

- [ ] Cuenta creada con MFA en root y root sin usar nunca más.
- [ ] Usuario IAM administrador con MFA y AWS CLI configurada.
- [ ] Alarma de presupuesto (AWS Budgets) de 5 € con aviso al email.
- [ ] Notas de infraestructura en docs/infra.md del repo.

<details><summary>Pistas</summary>

- El paso de la alarma de coste no es opcional: es lo que te deja experimentar tranquilo.

</details>

Lecciones de apoyo:

- **Qué es la nube** — Módulo 1 de Cloud Practitioner Essentials. Explica con tus palabras el modelo de responsabilidad compartida. ([AWS Skill Builder · Cloud Practitioner Essentials (gratis)](https://skillbuilder.aws/))
- **Infraestructura global** — Regiones, zonas de disponibilidad y edge. ¿Qué región elegirías para BandHub y por qué? ([AWS Skill Builder · Cloud Practitioner Essentials (gratis)](https://skillbuilder.aws/))
- **IAM** — Usuarios, grupos, roles y políticas. Lee una política JSON y explica qué permite. ([Documentación de AWS](https://docs.aws.amazon.com/))
- **Precios y facturación** — Bajo demanda, reservas, spot, Savings Plans. Calcula a mano cuánto costaría una t3.micro un mes. ([AWS Skill Builder · Cloud Practitioner Essentials (gratis)](https://skillbuilder.aws/))

### BH-10 · Foto de perfil en S3

- [ ] POST /api/users/{id}/avatar sube una imagen a S3 y guarda la clave en UserEntity.
- [ ] GET devuelve la imagen. Solo el propio usuario puede cambiar la suya.
- [ ] Valida tipo (jpg, png) y tamaño.
- [ ] Ninguna credencial en el código ni en el repo.

<details><summary>Pistas</summary>

- Uso el módulo User porque ya existe; las partituras llegan cuando haya canciones (Fase 3).
- AWS SDK for Java 2.x, S3Client con DefaultCredentialsProvider: lee las credenciales de la CLI.
- Una clave tipo users/{id}/avatar-{uuid}.png.

</details>

Lecciones de apoyo:

- **EC2** — Tipos de instancia y modelos de precio. Lanza una t3.micro, conéctate por SSH y termínala. ([AWS Skill Builder · Cloud Practitioner Essentials (gratis)](https://skillbuilder.aws/))
- **S3** — Clases de almacenamiento y políticas de bucket. Crea un bucket privado y sube un fichero por CLI. ([AWS Skill Builder · Cloud Practitioner Essentials (gratis)](https://skillbuilder.aws/))
- **Bases de datos** — RDS vs DynamoDB vs Aurora. ¿Cuál usaría BandHub y por qué? ([AWS Skill Builder · Cloud Practitioner Essentials (gratis)](https://skillbuilder.aws/))
- **Serverless y contenedores** — Lambda, ECS y Fargate. Haz una tabla comparativa con 3 criterios. ([AWS Skill Builder · Cloud Practitioner Essentials (gratis)](https://skillbuilder.aws/))

### BH-11 · Primer despliegue

- [ ] BandHub corriendo con Docker Compose en una EC2 t3.micro.
- [ ] Security group: SSH solo desde tu IP y 8080 abierto.
- [ ] Prueba los endpoints desde fuera y termina la instancia.
- [ ] Apunta en docs/infra.md lo que has creado y lo que costaría tenerlo encendido.

<details><summary>Pistas</summary>

- Un script de user data puede instalar Docker al arrancar la instancia.

</details>

Lecciones de apoyo:

- **VPC** — Subredes públicas y privadas, internet gateway. Dibuja la red por defecto de tu cuenta. ([AWS Skill Builder · Cloud Practitioner Essentials (gratis)](https://skillbuilder.aws/))
- **Security groups** — Security groups vs NACL. ¿Qué abrirías para una API en el puerto 8080? ([Documentación de AWS](https://docs.aws.amazon.com/))
- **CloudWatch y CloudTrail** — Métricas, logs y auditoría. Busca en CloudTrail quién creó tu bucket. ([AWS Skill Builder · Cloud Practitioner Essentials (gratis)](https://skillbuilder.aws/))
- **Well-Architected** — Los 6 pilares. Puntúa a BandHub de 1 a 5 en cada uno. ([AWS Well-Architected Framework](https://aws.amazon.com/architecture/well-architected/))

### SAA-0 · Medir desde dónde partes

- [ ] Nota del primer examen de práctica de la Associate apuntada en docs/infra.md.
- [ ] Tus 3 dominios más flojos, que guiarán la Fase 3.

Lecciones de apoyo:

- **Mapa general** — Termina los módulos que te queden del curso gratuito de Cloud Practitioner. Sin examen. ([AWS Skill Builder · Cloud Practitioner Essentials (gratis)](https://skillbuilder.aws/))
- **Examen de práctica SAA** — Haz uno sin preparar. La nota no importa: es tu punto de partida. ([Tutorials Dojo · exámenes de práctica](https://tutorialsdojo.com/))
- **Análisis de fallos** — Clasifica cada fallo por dominio del examen. ([Tutorials Dojo · exámenes de práctica](https://tutorialsdojo.com/))
- **Plan de estudio** — Elige el curso para la Fase 3 y apunta tus dominios flojos. ([Adrian Cantrill · SAA-C03 (muy práctico)](https://learn.cantrill.io/))

## Abr–may · Mantenimiento

*Segundo permiso.* Tickets sueltos de una sesión. Solo si hay energía.

### BH-M2 · Entorno "pre" en IONOS

- [ ] APP_ROADMAP · Fase 6: CI construye la imagen y la publica en GitHub Container Registry.
- [ ] Despliegas esa imagen con Docker Compose en tu servidor IONOS. Es tu ensayo de bajo riesgo antes de AWS.

<details><summary>Pistas</summary>

- GITHUB_TOKEN con permiso packages: write.
- En el servidor, solo hace falta docker compose pull && docker compose up -d.

</details>

### BH-M3 · Ramas y pull requests

- [ ] Rama main protegida: solo entra código por PR con CI en verde.
- [ ] Plantilla de PR y commits convencionales (feat:, fix:).

### Lectura · DNS, HTTPS y TLS

- [ ] Artículos del Cloudflare Learning Center sobre DNS, HTTP/HTTPS y TLS.
- [ ] Sin código. Si te enteras de cómo llega una petición del navegador a BandHub, objetivo cumplido.

## Fase 3 · Associate + IA

*Jun 2027 →.* Bandas, miembros, repertorio y setlists (Fases 3–5 de tu APP_ROADMAP), AWS a fondo, Terraform e IA. Sin prisa: el examen SAA cuando estés listo.

### BH-13 · Módulo Band

- [ ] APP_ROADMAP · Fase 3: POST, GET listado, GET detalle, PUT y DELETE lógico de /api/bands.
- [ ] El creador queda como primer miembro con rol OWNER.
- [ ] Migración Flyway V2.

<details><summary>Pistas</summary>

- La creación de banda y de su primer miembro van en la misma transacción.

</details>

Lecciones de apoyo:

- **Modelado relacional** — Diseña en papel Band, BandMember y sus claves. ([SQLBolt (ejercicios SQL interactivos)](https://sqlbolt.com/))
- **Relaciones JPA** — @ManyToOne y @OneToMany: quién es el dueño de la relación. ([Spring Guides (tutoriales cortos)](https://spring.io/guides))
- **Entidad puente con atributos** — Por qué BandMember con rol es mejor que un @ManyToMany. ([Vlad Mihalcea · El problema N+1](https://vladmihalcea.com/n-plus-1-query-problem/))
- **Migraciones** — Escribe a mano la V2 de Flyway para bandas. ([Spring Boot Reference](https://docs.spring.io/spring-boot/reference/))

### BH-14 · Miembros de banda

- [ ] APP_ROADMAP · Fase 4: endpoints de /api/bands/{bandId}/members.
- [ ] Enum BandMemberRole (OWNER, ADMIN, MEMBER) separado de UserRole.
- [ ] Solo OWNER y ADMIN añaden o sacan miembros; nadie puede dejar la banda sin OWNER.
- [ ] Tests de cada regla.

Lecciones de apoyo:

- **Roles vs permisos** — Diferencia entre tu UserRole global y un rol dentro de una banda. ([Spring Security Reference](https://docs.spring.io/spring-security/reference/))
- **@PreAuthorize** — Kata: protege un método para que solo lo llame su dueño. ([Spring Security Reference](https://docs.spring.io/spring-security/reference/))
- **Beans en expresiones** — @PreAuthorize("@bandSecurity.isOwner(#bandId)"). ([Spring Security Reference](https://docs.spring.io/spring-security/reference/))
- **Tests de autorización** — @WithMockUser con distintos roles. ([Spring Security Reference](https://docs.spring.io/spring-security/reference/))

### BH-15 · Repertorio y setlists

- [ ] APP_ROADMAP · Fase 5: SongController y SetlistController bajo /api/bands/{bandId}.
- [ ] Setlists con canciones ordenadas (entidad puente con position) y sin problema N+1.
- [ ] Paginación en el listado de canciones.
- [ ] Solo los miembros de la banda acceden.

<details><summary>Pistas</summary>

- JOIN FETCH o @EntityGraph para traer un setlist con sus canciones en una consulta.

</details>

Lecciones de apoyo:

- **Diseño REST anidado** — Cuándo anidar (/bands/{id}/songs) y cuándo no. ([MDN · HTTP](https://developer.mozilla.org/en-US/docs/Web/HTTP))
- **Paginación** — Pageable y Page en Spring Data. ([Spring Boot Reference](https://docs.spring.io/spring-boot/reference/))
- **Orden en relaciones** — Listas ordenadas con una columna position. ([Vlad Mihalcea · El problema N+1](https://vladmihalcea.com/n-plus-1-query-problem/))
- **OpenAPI** — springdoc y Swagger UI. ([Spring Guides (tutoriales cortos)](https://spring.io/guides))

### BH-16 · Arranque del frontend

- [ ] APP_ROADMAP · Fase 7: proyecto Angular con login, interceptor JWT y listado de bandas.
- [ ] Tu punto fuerte: hazlo bonito, pero en una semana.

Lecciones de apoyo:

- **Standalone components** — Repasa la estructura de un proyecto Angular actual. ([Angular docs (oficial)](https://angular.dev/))
- **Signals** — Kata: contador y lista filtrada con signals. ([Angular docs (oficial)](https://angular.dev/))
- **HttpClient e interceptores** — Un interceptor que añade el JWT. ([Angular docs (oficial)](https://angular.dev/))
- **Guards** — Protege una ruta si no hay sesión. ([Angular docs (oficial)](https://angular.dev/))

### BH-17 · BandHub sin claves

- [ ] En EC2, la aplicación accede a S3 con un rol de instancia, sin claves.
- [ ] Política de mínimo privilegio: solo el bucket de BandHub.

Lecciones de apoyo:

- **Políticas** — Escribe una política que solo permita leer un bucket concreto. ([Documentación de AWS](https://docs.aws.amazon.com/))
- **Roles** — Por qué una aplicación usa roles y no claves. ([Documentación de AWS](https://docs.aws.amazon.com/))
- **Mínimo privilegio** — Sección de IAM del curso SAA. ([Adrian Cantrill · SAA-C03 (muy práctico)](https://learn.cantrill.io/))
- **Organizations y SCP** — Qué son y cuándo se usan. ([Adrian Cantrill · SAA-C03 (muy práctico)](https://learn.cantrill.io/))

### BH-18 · Diseño de red de BandHub

- [ ] Diagrama (draw.io o Excalidraw) con VPC, subredes públicas y privadas, ALB, aplicación y RDS.
- [ ] docs/adr/001-red.md explicando cada decisión.

<details><summary>Pistas</summary>

- Sin código esta semana: el diseño es el entregable.

</details>

Lecciones de apoyo:

- **Subredes y rutas** — Pública vs privada: qué las diferencia de verdad. ([Adrian Cantrill · SAA-C03 (muy práctico)](https://learn.cantrill.io/))
- **NAT Gateway** — Para qué sirve y cuánto cuesta. ([Adrian Cantrill · SAA-C03 (muy práctico)](https://learn.cantrill.io/))
- **Endpoints de VPC** — Acceder a S3 sin salir a internet. ([Adrian Cantrill · SAA-C03 (muy práctico)](https://learn.cantrill.io/))
- **Peering y transit** — Solo conceptos. ([Adrian Cantrill · SAA-C03 (muy práctico)](https://learn.cantrill.io/))

### BH-20 · BandHub con RDS

- [ ] Postgres en RDS en subred privada.
- [ ] La contraseña sale de Secrets Manager o de variables de entorno, nunca del repo.
- [ ] Flyway migra al arrancar.

Lecciones de apoyo:

- **RDS** — Backups, snapshots, Multi-AZ y réplicas de lectura. ([Adrian Cantrill · SAA-C03 (muy práctico)](https://learn.cantrill.io/))
- **Seguridad de BBDD** — Subred privada y security group que solo deja entrar a la app. ([Adrian Cantrill · SAA-C03 (muy práctico)](https://learn.cantrill.io/))
- **Secrets Manager** — Guardar credenciales fuera del código. ([Documentación de AWS](https://docs.aws.amazon.com/))
- **Migraciones en despliegue** — Flyway al arrancar: riesgos. ([Spring Boot Reference](https://docs.spring.io/spring-boot/reference/))

### BH-21 · Partituras en S3

- [ ] APP_ROADMAP · Fase 5: FileController bajo /api/bands/{bandId}/songs/{songId}/files.
- [ ] Los PDF van a S3 y la descarga devuelve una URL prefirmada en lugar de pasar el fichero por la API.

<details><summary>Pistas</summary>

- Reutiliza lo que aprendiste con el avatar en BH-10.

</details>

Lecciones de apoyo:

- **Seguridad de S3** — Block public access, cifrado, políticas. ([Adrian Cantrill · SAA-C03 (muy práctico)](https://learn.cantrill.io/))
- **URLs prefirmadas** — Kata: genera una URL que caduque en 5 minutos. ([AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/))
- **CloudFront** — CDN y origin access control. ([Adrian Cantrill · SAA-C03 (muy práctico)](https://learn.cantrill.io/))
- **Ciclo de vida** — Mover ficheros viejos a clases baratas. ([Adrian Cantrill · SAA-C03 (muy práctico)](https://learn.cantrill.io/))

### BH-22 · Ensayos con aviso

- [ ] APP_ROADMAP · Fase 5: RehearsalController bajo /api/bands/{bandId}/rehearsals.
- [ ] Al crear un ensayo se publica un evento en SQS.
- [ ] Un consumidor lo lee y registra el aviso en log (más adelante, email).

<details><summary>Pistas</summary>

- Con Spring Cloud AWS hay un @SqsListener.

</details>

Lecciones de apoyo:

- **SQS** — Colas estándar vs FIFO, dead-letter queue. ([Adrian Cantrill · SAA-C03 (muy práctico)](https://learn.cantrill.io/))
- **SNS** — Pub/sub y fan-out. ([Adrian Cantrill · SAA-C03 (muy práctico)](https://learn.cantrill.io/))
- **EventBridge** — Eventos entre servicios. ([Adrian Cantrill · SAA-C03 (muy práctico)](https://learn.cantrill.io/))
- **Desacoplar** — Por qué una cola hace un sistema más resistente. ([System Design Primer](https://github.com/donnemartin/system-design-primer))

### BH-23 · BandHub en Fargate

- [ ] Imagen en ECR y servicio en ECS Fargate detrás de un ALB con health check, contra RDS.
- [ ] Destruye al acabar y apunta el coste.

Lecciones de apoyo:

- **ECR** — Sube una imagen. ([Adrian Cantrill · SAA-C03 (muy práctico)](https://learn.cantrill.io/))
- **ECS y Fargate** — Task definition, service y cluster. ([Adrian Cantrill · SAA-C03 (muy práctico)](https://learn.cantrill.io/))
- **ELB y health checks** — ALB vs NLB; /actuator/health. ([Spring Boot Actuator](https://docs.spring.io/spring-boot/reference/actuator/))
- **Coste** — Fargate vs EC2 para BandHub. ([Adrian Cantrill · SAA-C03 (muy práctico)](https://learn.cantrill.io/))

### Viernes · Medir dónde estás

- [ ] Segundo examen de práctica. Apunta tu nota: la referencia para diciembre.

Lecciones de apoyo:

- **Examen de práctica 1** — Cronometrado. ([Tutorials Dojo · exámenes de práctica](https://tutorialsdojo.com/))
- **Repaso** — Dominios flojos. ([Adrian Cantrill · SAA-C03 (muy práctico)](https://learn.cantrill.io/))
- **Repaso** — Dominios flojos. ([Adrian Cantrill · SAA-C03 (muy práctico)](https://learn.cantrill.io/))
- **Flashcards** — Servicios que confundes. ([Tutorials Dojo · exámenes de práctica](https://tutorialsdojo.com/))

### BH-25 · Infra en código: S3 + IAM

- [ ] Carpeta infra/ en BandHub con el bucket y el rol IAM en Terraform.

Lecciones de apoyo:

- **Por qué IaC** — Lee la introducción y explica qué problema resuelve. ([HashiCorp · Tutoriales de Terraform](https://developer.hashicorp.com/terraform/tutorials))
- **Providers y resources** — Kata: un bucket S3 con Terraform. ([HashiCorp · Tutoriales de Terraform](https://developer.hashicorp.com/terraform/tutorials))
- **Variables, outputs y módulos** — Parametriza el entorno (dev/prod). ([HashiCorp · Tutoriales de Terraform](https://developer.hashicorp.com/terraform/tutorials))
- **State** — Qué es el state, por qué no va a Git y cómo guardarlo en S3. ([HashiCorp · Tutoriales de Terraform](https://developer.hashicorp.com/terraform/tutorials))

### BH-27 · terraform apply y BandHub vivo

- [ ] Un apply levanta red, RDS, ECS y ALB; un destroy lo borra todo.

Lecciones de apoyo:

- **RDS en Terraform** — Subnet group y security group. ([HashiCorp · Tutoriales de Terraform](https://developer.hashicorp.com/terraform/tutorials))
- **ECS en Terraform** — Cluster, service y task definition. ([HashiCorp · Tutoriales de Terraform](https://developer.hashicorp.com/terraform/tutorials))
- **Secretos** — No meter contraseñas en el state en claro. ([HashiCorp · Tutoriales de Terraform](https://developer.hashicorp.com/terraform/tutorials))
- **Destroy** — Comprueba que no queda nada. ([HashiCorp · Tutoriales de Terraform](https://developer.hashicorp.com/terraform/tutorials))

### BH-28 · Merge a main = despliegue

- [ ] Pipeline que despliega en ECS al hacer merge, autenticado por OIDC.

Lecciones de apoyo:

- **OIDC** — GitHub Actions entra en AWS sin claves. ([GitHub Actions docs](https://docs.github.com/en/actions))
- **Pipeline** — Build, test, imagen, despliegue. ([GitHub Actions docs](https://docs.github.com/en/actions))
- **Entornos** — Aprobación manual para prod. ([GitHub Actions docs](https://docs.github.com/en/actions))
- **Rollback** — Cómo volver a la versión anterior en ECS. ([Adrian Cantrill · SAA-C03 (muy práctico)](https://learn.cantrill.io/))

### BH-29 · BandHub observable

- [ ] Métricas de latencia y de negocio (setlists creados) en CloudWatch.
- [ ] Logs JSON con id de petición y dos consultas guardadas en Logs Insights para diagnosticar errores.

Lecciones de apoyo:

- **Actuator** — Endpoints útiles y cuáles no exponer. ([Spring Boot Actuator](https://docs.spring.io/spring-boot/reference/actuator/))
- **Micrometer** — Contadores y timers. ([Spring Boot Actuator](https://docs.spring.io/spring-boot/reference/actuator/))
- **Logs estructurados** — Formato JSON e id de petición. ([Spring Boot Reference](https://docs.spring.io/spring-boot/reference/))
- **Logs Insights** — Consultas sobre tus logs. ([Documentación de AWS](https://docs.aws.amazon.com/))

### BH-32 · BandHub vigilado

- [ ] Alarmas de 5xx y latencia con aviso por email.
- [ ] docs/runbook.md con los pasos ante cada alarma.

Lecciones de apoyo:

- **Alarmas** — Sobre errores 5xx y latencia. ([Documentación de AWS](https://docs.aws.amazon.com/))
- **SLO** — Define un objetivo para BandHub. ([AWS Well-Architected Framework](https://aws.amazon.com/architecture/well-architected/))
- **Costes** — Cost Explorer y etiquetas. ([Documentación de AWS](https://docs.aws.amazon.com/))
- **Runbook** — Qué hacer si salta la alarma. ([AWS Well-Architected Framework](https://aws.amazon.com/architecture/well-architected/))

### BH-33 · Sugeridor de setlist

- [ ] Endpoint que, a partir del repertorio de la banda y una duración, pide al modelo un setlist y lo devuelve como record validado.
- [ ] Si el modelo propone canciones que no existen, se descartan.

<details><summary>Pistas</summary>

- No confíes en la salida del modelo: valídala igual que una entrada de usuario.

</details>

Lecciones de apoyo:

- **Cómo funciona un LLM** — Tokens, contexto, temperatura. ([Spring AI Reference](https://docs.spring.io/spring-ai/reference/))
- **ChatClient** — Kata: una llamada simple desde Spring. ([Spring AI Reference](https://docs.spring.io/spring-ai/reference/))
- **Salida estructurada** — Que el modelo devuelva un record de Java. ([Spring AI Reference](https://docs.spring.io/spring-ai/reference/))
- **Prompts** — System prompt vs user prompt. ([Spring AI Reference](https://docs.spring.io/spring-ai/reference/))

### BH-34 · Pregunta a tu banda

- [ ] Las notas de ensayo de cada banda se guardan como embeddings en pgvector; una banda nunca ve las de otra.
- [ ] Endpoint de preguntas sobre esas notas que responde citando de qué nota sale.

<details><summary>Pistas</summary>

- Ticket de varios viernes: es normal.

</details>

Lecciones de apoyo:

- **Qué es un embedding** — Similitud entre textos. ([Spring AI Reference](https://docs.spring.io/spring-ai/reference/))
- **pgvector** — Instálalo en tu Postgres de Docker. ([pgvector](https://github.com/pgvector/pgvector))
- **Patrón RAG** — Recuperar y luego generar. ([Spring AI Reference](https://docs.spring.io/spring-ai/reference/))
- **Citar fuentes** — Devolver de qué nota sale la respuesta. ([Spring AI Reference](https://docs.spring.io/spring-ai/reference/))

### BH-36 · IA blindada

- [ ] Rate limit por usuario en los endpoints de IA.
- [ ] Métrica de tokens consumidos.
- [ ] Tres tests con intentos de prompt injection.

Lecciones de apoyo:

- **Prompt injection** — Lee el top 10 de LLM. ([OWASP Top 10 para aplicaciones LLM](https://owasp.org/www-project-top-10-for-large-language-model-applications/))
- **Datos sensibles** — Qué no mandar al modelo. ([OWASP Top 10 para aplicaciones LLM](https://owasp.org/www-project-top-10-for-large-language-model-applications/))
- **Límites** — Tokens máximos y rate limit por usuario. ([Spring AI Reference](https://docs.spring.io/spring-ai/reference/))
- **Tests** — Tests de los casos maliciosos. ([Spring AI Reference](https://docs.spring.io/spring-ai/reference/))

### Viernes · Último simulacro

- [ ] Si superas el 80% dos veces seguidas, estás listo.

Lecciones de apoyo:

- **Examen de práctica 3** — Cronometrado. ([Tutorials Dojo · exámenes de práctica](https://tutorialsdojo.com/))
- **Repaso** — Fallos. ([Adrian Cantrill · SAA-C03 (muy práctico)](https://learn.cantrill.io/))
- **Examen de práctica 4** — Objetivo: más del 80%. ([Tutorials Dojo · exámenes de práctica](https://tutorialsdojo.com/))
- **Repaso final** — Cheatsheet. ([Adrian Cantrill · SAA-C03 (muy práctico)](https://learn.cantrill.io/))

### SAA-C03 · AWS Solutions Architect Associate

- [ ] Examen. Después, actualiza el CV y LinkedIn con BandHub como proyecto principal: API, AWS, Terraform, CI/CD, observabilidad e IA.

## Fase 4 · Senior

*2028.* Módulos largos, sin calendario semanal.

### BH-40 · Documento de diseño

- [ ] Diseña cómo escalaría BandHub a 10.000 bandas: caché, colas, lecturas y escrituras.
- [ ] Escríbelo como documento de diseño con alternativas descartadas.

### BH-41 · Refactor del módulo de setlists

- [ ] Separa dominio, aplicación y adaptadores en el módulo de setlists sin romper un solo test.

### BH-42 · BandHub como portfolio

- [ ] README de portfolio con arquitectura, decisiones (ADRs) y demo.
- [ ] Una charla interna o un post sobre algo que aprendiste.
- [ ] Ofrécete a hacer onboarding a los próximos juniors.

### BH-44 · Saber contar lo que sabes

- [ ] Explicar la arquitectura de BandHub en 5 minutos, con el diagrama de BH-18.
- [ ] Repasar preguntas típicas de Java, Spring, JPA y AWS.
- [ ] Un simulacro de entrevista técnica con Claude.
- [ ] CV y LinkedIn con BandHub como proyecto principal.

### BH-43 · BandHub en Kubernetes

- [ ] Despliegue local con kind o minikube: Deployment, Service, ConfigMap y Secret.

### TAE · Temario en lugar de esta fase

- [ ] Sustituye los módulos de 2028 por el temario de la convocatoria. Te preparo un plan propio si eliges este camino.
