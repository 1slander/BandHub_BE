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

---

## Kanban en GitHub: Issues + Projects

**Explicación:** un kanban es un tablero con columnas (Pendiente → En curso → Hecho) donde cada tarea es una tarjeta que se mueve de izquierda a derecha. En GitHub se monta con dos piezas:
- **Issues:** cada tarea es una issue del repositorio, con título, descripción y casillas (`- [ ]`) que se pueden marcar. Viven junto al código y se pueden enlazar con ramas y PR (escribir `Closes #3` en un PR cierra la issue 3 al hacer merge).
- **Projects:** el tablero en sí. Agrupa issues (de uno o varios repos) y las muestra como tabla o como columnas (vista *Board*). La columna de cada tarjeta es su campo *Status*.

Se puede hacer todo a mano desde la web, o automatizarlo con `gh`, la herramienta de GitHub para la terminal.

**Ejemplo en el proyecto:** el tablero "BandHub Bootcamp" se creó con `scripts/create-bootcamp-issues.ps1`, que lee los tickets de `docs/bootcamp/issues.json`. Pasos para repetirlo (por ejemplo, al crear las issues de la Fase 3):

```powershell
# 1. Instalar gh (una vez)
winget install --id GitHub.cli
# 2. Iniciar sesión (GitHub.com → SSH → Login with a web browser)
gh auth login
# 3. Dar permiso para gestionar Projects (no viene por defecto)
gh auth refresh -s project

# Comandos que usa el script, por si hace falta alguno suelto:
gh label create fase-3 --repo 1slander/BandHub_BE --color "BFD4F2"
gh project create --owner 1slander --title "BandHub Bootcamp"
gh issue create --repo 1slander/BandHub_BE --title "BH-13 · Módulo Band" --body-file cuerpo.md --label "bootcamp,fase-3"
gh project item-add <número-del-proyecto> --owner 1slander --url <url-de-la-issue>
gh project list --owner 1slander   # para ver el número del proyecto
```

En la web: perfil → *Projects* → "BandHub Bootcamp" → vista *Board*. Regla de uso: solo una tarjeta en *In Progress* a la vez.

**A mano desde la web (sin `gh`):**

- *Crear una issue:* repo → pestaña **Issues** → **New issue**. Título (`BH-13 · Módulo Band`) y criterios como casillas (`- [ ] ...`). En la barra lateral: **Labels** (`bootcamp`, fase), **Projects** ("BandHub Bootcamp") para que entre en el tablero, y **Milestone** si quieres agrupar por fase.
- *Crear un tablero:* perfil → **Projects** → **New project** → plantilla **Board** (trae Todo / In Progress / Done). Para añadir tarjetas: **+ Add item** al pie de una columna, escribe `#` y elige una issue, o crea una nota rápida y luego conviértela en issue.
- *Enlazar el tablero al repo:* repo → pestaña **Projects** → **Link a project**.
- Arrastrar una tarjeta entre columnas cambia su campo *Status*. Puedes añadir columnas propias ("Bloqueado", "En revisión").

**Analogía:** es la pizarra de corcho con post-its que hay en muchas oficinas. Las issues son los post-its (cada uno con su lista de cosas por hacer) y el Project es la pizarra con sus columnas. `gh` es tener a alguien que te escribe y pega veinte post-its de golpe en vez de hacerlo tú uno a uno.

---

## Plantillas de issues y PR, enlazar PR con issues y automatizar el tablero

**Explicación:** GitHub permite que todas las issues y PR sigan la misma estructura, que cada PR quede ligado a su tarea y que el tablero se mueva solo. Son cuatro piezas:

1. **Plantillas de issues.** Ficheros dentro de `.github/ISSUE_TEMPLATE/` en la rama principal. Al pulsar *New issue*, GitHub ofrece elegir entre ellas ("Bug", "Tarea", "Ticket del bootcamp"...) y rellena la descripción. Pueden ser Markdown (`.md`) o formularios (`.yml`) con campos obligatorios.
2. **Plantilla de PR.** Un único fichero `.github/pull_request_template.md`. Su contenido aparece automáticamente en la descripción de cada PR nuevo.
3. **Enlazar un PR con su issue.** Tres formas:
   - Escribir en la descripción del PR una palabra clave seguida del número: `Closes #5`, `Fixes #5` o `Resolves #5`. Al hacer merge **a la rama por defecto**, la issue se cierra sola. Mencionar solo `#5` la enlaza pero no la cierra.
   - Desde la issue, en la barra lateral **Development** → *Create a branch*: crea la rama ya enlazada.
   - Desde el PR, en **Development** → elegir la issue.
4. **Automatizaciones del Project.** En el tablero → menú **⋯** → **Workflows**. Vienen de serie, solo hay que activarlas: *Item closed* → mover a Done, *Pull request merged* → Done, *Auto-add to project* (añade solas las issues del repo que cumplan un filtro, por ejemplo `label:bootcamp`). Además se pueden crear campos propios (prioridad, tamaño, sprint con el tipo *Iteration*) y vistas distintas del mismo tablero (Board, Table, Roadmap).

**Ejemplo en el proyecto:**

`.github/ISSUE_TEMPLATE/ticket.md`:
```markdown
---
name: Ticket del bootcamp
about: Nuevo ticket BH-xx
title: "BH-xx · "
labels: bootcamp
---

## Objetivo
<!-- Qué aprende o construye este ticket, en una frase -->

## Criterios de aceptación
- [ ] 

## Conceptos nuevos
<!-- Lo que irá a CONCEPTS.md -->
```

`.github/pull_request_template.md`:
```markdown
## Qué cambia
<!-- Resumen en 2-3 líneas -->

Closes #

## Cómo probarlo
- [ ] Tests en verde (`./mvnw test`)
- [ ] Probado a mano: <!-- endpoint y casos -->

## Checklist
- [ ] STATE.md, APP_ROADMAP.md y Development Log actualizados
- [ ] Conceptos nuevos añadidos a CONCEPTS.md
```

Flujo completo de un ticket: issue BH-01 → *Development* → *Create a branch* (`feature/BH-01-update-user`) → commits → PR con `Closes #1` → CI en verde → merge → la issue se cierra y la tarjeta pasa sola a Done.

**Para un equipo de trabajo:** lo que más ordena es combinar plantillas (todo el mundo describe igual las tareas y los PR), la regla "ningún PR sin issue enlazada", y las automatizaciones para que nadie tenga que mover tarjetas a mano. Requisito: que el código esté en GitHub. Si en la empresa usan Azure DevOps o Jira, ambos tienen equivalentes (work items y boards, plantillas de PR, enlace con `AB#123` en Azure DevOps o con la clave del ticket en Jira).

**Analogía:** es como los albaranes con formato fijo de un almacén. La plantilla es el impreso en blanco que obliga a rellenar siempre los mismos campos; el `Closes #5` es grapar el albarán de entrega al pedido original; y las automatizaciones son el empleado que, al ver el albarán grapado, archiva el pedido como "entregado" sin que nadie se lo pida.

---

## PATH en Windows (por qué un programa recién instalado "no existe")

**Explicación:** cuando escribes un comando como `gh` o `java`, la terminal no busca en todo el disco: solo mira en una lista de carpetas guardada en una variable de entorno llamada `PATH`. Al instalar un programa, el instalador añade su carpeta a esa lista, pero **cada terminal lee el `PATH` una sola vez, al abrirse**. Una terminal abierta antes de la instalación sigue con la lista antigua y no encuentra el programa. La terminal integrada de VS Code hereda el `PATH` del propio VS Code, así que a veces no basta con abrir otra pestaña: hay que cerrar VS Code entero.

**Ejemplo en el proyecto:** tras `winget install --id GitHub.cli`, la terminal respondía `gh : The term 'gh' is not recognized`. Se arregló recargando el `PATH` en la misma terminal, sin cerrar nada:

```powershell
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")
gh --version   # ahora sí responde
```

La lista se compone de dos partes: la del sistema (`Machine`, para todos los usuarios) y la tuya (`User`). El comando las vuelve a leer y las aplica a la sesión actual.

**Analogía:** es como la agenda de contactos de un móvil que solo se sincroniza al encenderlo. Si alguien te da su número mientras el móvil está encendido, lo apuntas en la nube, pero tu móvil no lo tendrá hasta que lo reinicies o fuerces la sincronización. Recargar el `PATH` es pulsar "sincronizar ahora".
