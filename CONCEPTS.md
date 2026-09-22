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
