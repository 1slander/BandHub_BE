# CONCEPTS.md

Glosario personal de conceptos aprendidos durante el desarrollo de BandHub.
Cada entrada explica el concepto de forma sencilla, con un ejemplo del
propio proyecto y una analogía de la vida cotidiana.

---

## Checked vs Unchecked Exceptions

**Definición sencilla:**
Es una cuestión de si el *compilador* te obliga a hacer algo con una excepción
antes de dejarte compilar, no de cuándo ocurre el error.

- **Checked**: el compilador exige que cada método que pueda lanzarla la
  capture con `try/catch`, o declare `throws NombreExcepcion` para pasarle la
  responsabilidad a quien lo llame. Si no haces ninguna de las dos cosas, el
  código no compila. Son subclases de `Exception` (pero no de `RuntimeException`).
- **Unchecked**: el compilador no obliga a nada. Puedes lanzarla sin declarar
  `throws` en ningún sitio y el código compila igual; el error solo aparece si
  ocurre en tiempo de ejecución. Son subclases de `RuntimeException`.

**Ejemplo en BandHub:**
`UserNotFoundException` y `EmailAlreadyExistsException` extienden
`RuntimeException` (unchecked) a propósito. Si fueran checked, habría que
añadir `throws UserNotFoundException` a `UserService`, `UserServiceImpl` y al
método del controller que la llame, solo para que el `GlobalExceptionHandler`
la intercepte más arriba — ensuciando firmas que no tienen nada que ver con el
manejo de errores HTTP. Al ser unchecked, la excepción "burbujea" sola desde
`UserServiceImpl` hasta el `@RestControllerAdvice` sin que nadie tenga que
declararla explícitamente por el camino.

**Analogía cotidiana:**
Checked es como un formulario que el funcionario de ventanilla no acepta si no
rellenas primero una casilla obligatoria ("¿qué harás si esto falla?"): te lo
exige *antes* de tramitarlo. Unchecked es como firmar un contrato sin esa
casilla: nadie te lo impide en el momento, pero si luego algo sale mal, el
problema explota igualmente — solo que nadie te avisó de antemano de que debías
prepararte para ello.

---

## Docker Compose vs `docker run`

**Explicación:** `docker run` describe un contenedor con banderas en la
terminal, de memoria, cada vez que lo arrancas — es fácil que te salga
ligeramente distinto si te olvidas de algún flag. `docker compose` mueve esa
descripción a un fichero declarativo (`docker-compose.yaml`): defines "cómo
debe ser" tu entorno una sola vez (imagen, puertos, variables de entorno,
volúmenes), y con `docker compose up -d` Docker crea/arranca todo lo
necesario para que coincida exactamente con esa descripción.

**Ejemplo en el proyecto:** el contenedor manual `postgres-server`
(`docker run ...`) se sustituyó por `docker-compose.yaml`, con un servicio
`postgres` (imagen `postgres:16`, puerto `5433:5432`, `POSTGRES_PASSWORD`,
`POSTGRES_DB`, un volumen con nombre) — mismo resultado, pero reproducible
con un solo comando y versionado junto al código.

**Analogía:** `docker run` es montar un mueble de IKEA de memoria cada vez,
sin las instrucciones — te puede salir distinto. `docker-compose.yaml` son
las instrucciones en papel: siempre montas el mismo mueble, y si se las
pasas a otra persona (o a tu "yo" de dentro de unos meses), le sale igual.

---

## Sintaxis YAML en `docker-compose.yaml`: mapeos, listas y "short syntax"

**Explicación:** un mismo fichero YAML mezcla formas distintas de escribir
cosas, y es fácil confundirlas:
- Pares `clave: valor` normales (como `image: postgres:16`) — necesitan el
  espacio después de los dos puntos para que YAML los reconozca como par, y
  no usan `;` ni `,` al final de línea (el salto de línea ya cierra el
  campo, a diferencia de JS/CSS/SQL).
- Listas, con un guión `-` por elemento, cada uno en su propia línea — así
  es como `ports` y `volumes` esperan sus valores dentro de un servicio.
- Dentro de esas listas, el "short syntax" de `volumes` es un único string
  con su propio formato interno, `"nombre_volumen:ruta"`, **sin** espacio
  después de los dos puntos — ahí el `:` no es sintaxis de YAML, es solo
  texto dentro del valor.

**Ejemplo en el proyecto:**
```yaml
services:
  postgres:
    image: postgres:16        # clave: valor (con espacio)
    ports:
      - "5433:5432"            # lista de un elemento
    volumes:
      - bandhub_pg_database:/var/lib/postgresql/data   # string "nombre:ruta", sin espacio
volumes:
  bandhub_pg_database:         # declaración del volumen con nombre
```

**Analogía:** es como escribir en dos idiomas en la misma página: la mayoría
del texto sigue la gramática normal de YAML (`clave: valor`, con su espacio),
pero algunas frases concretas (`volumes` dentro de un servicio) están
"citadas" en otro idioma con sus propias reglas (`nombre:ruta`, sin espacio)
— hay que saber cuál toca en cada línea.

---

## `POSTGRES_DB` solo actúa en el primer arranque del volumen

**Explicación:** la imagen oficial de `postgres` ejecuta un script de
inicialización únicamente la **primera vez** que arranca contra un volumen
de datos vacío. En ese arranque inicial, si le pasas `POSTGRES_DB=nombre`,
crea esa base de datos automáticamente. En arranques posteriores (el volumen
ya tiene datos), ese script no se vuelve a ejecutar, así que cambiar
`POSTGRES_DB` en un volumen ya inicializado no tiene ningún efecto. Esto es
distinto de `ddl-auto=update` de Hibernate, que gestiona las *tablas* dentro
de una base de datos que ya existe, pero nunca crea la base de datos en sí
— si no existe, la conexión de la API falla antes de que Hibernate llegue a
actuar.

**Ejemplo en el proyecto:** al crear un volumen nuevo (`bandhub_pg_database`)
con `POSTGRES_DB=bandhub` en el `docker-compose.yaml`, la base de datos
`bandhub` se creó sola en el primer `docker compose up -d` — la API respondió
`200 OK` con lista vacía sin necesidad de crear nada a mano en DBeaver. Con
el contenedor manual anterior (sin `POSTGRES_DB`), hubo que crear `bandhub`
a mano porque esa variable nunca se pasó en su primer arranque.

**Analogía:** es como la configuración inicial de un móvil nuevo: la
primera vez que lo enciendes te pregunta el idioma y la cuenta — una sola
vez. Si luego cambias esa preferencia en los ajustes del sistema pensando
que "reconfigurará" el móvil desde cero, no pasa nada — ese asistente de
primer arranque ya no vuelve a ejecutarse salvo que restaures el móvil de
fábrica (el equivalente a borrar el volumen).
