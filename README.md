# Recocido Simulado para TSP mediante Aceptación por Umbrales

Implementación en **Java 21** de la heurística **Threshold Accepting
(Aceptación por Umbrales)** aplicada al **Problema del Agente Viajero
(Traveling Salesman Problem, TSP)**.

El proyecto permite:

- construir instancias TSP a partir de identificadores de ciudades;
- obtener ciudades y conexiones desde SQLite o PostgreSQL;
- ejecutar Threshold Accepting con parámetros definidos manualmente;
- calcular y utilizar una temperatura inicial;
- realizar experimentos con múltiples configuraciones y semillas;
- comparar estadísticamente configuraciones;
- reproducir ejecuciones experimentales;
- generar reportes de las corridas y soluciones encontradas.

El proyecto fue desarrollado para el estudio de **Heurísticas de
Optimización Combinatoria**.

---

# Contenido

1. [Requisitos](#requisitos)
2. [Tecnologías](#tecnologías)
3. [Estructura del proyecto](#estructura-del-proyecto)
4. [Clonar el repositorio](#clonar-el-repositorio)
5. [Compilar el proyecto](#compilar-el-proyecto)
6. [Ejecutar pruebas](#ejecutar-pruebas)
7. [Generar el JAR](#generar-el-jar)
8. [Configuración de la base de datos](#configuración-de-la-base-de-datos)
9. [Configuración de pruebas](#configuración-de-pruebas)
10. [Formato de archivos TSP](#formato-de-archivos-tsp)
11. [Uso general del programa](#uso-general-del-programa)
12. [Modo normal `-n`](#modo-normal--n)
13. [`heuristic.properties`](#heuristicproperties)
14. [Modo experimental `-e`](#modo-experimental--e)
15. [`experiment.properties`](#experimentproperties)
16. [Cálculo de temperatura inicial](#cálculo-de-temperatura-inicial)
17. [Semillas y reproducibilidad](#semillas-y-reproducibilidad)
18. [Reportes](#reportes)
19. [Selección del campeón](#selección-del-campeón)
20. [Ejecuciones largas](#ejecuciones-largas)
21. [Guardar el log](#ejecutar-y-guardar-un-log)
22. [Flujo recomendado](#flujo-recomendado)

---

# Requisitos

El proyecto requiere:

- **Java 21**
- **Maven 3.x**
- **Git**
- SQLite o PostgreSQL
- una base de datos con las ciudades y conexiones utilizadas por la instancia

Para verificar Java:

```bash
java -version
```

Para verificar Maven:

```bash
mvn -version
```

El proyecto está configurado para compilar utilizando Java 21:

```xml
<maven.compiler.release>21</maven.compiler.release>
```

---

# Tecnologías

| Tecnología | Uso |
|---|---|
| Java 21 | Implementación principal |
| Maven | Compilación y administración de dependencias |
| JUnit 5 | Pruebas unitarias y de integración |
| JDBC | Acceso a datos |
| SQLite | Base de datos soportada |
| PostgreSQL | Base de datos soportada |
| Maven Shade Plugin | Creación del JAR ejecutable |
| SLF4J | Infraestructura de logging de dependencias |

Las principales dependencias JDBC son:

```text
SQLite JDBC      3.46.1.0
PostgreSQL JDBC  42.7.4
```

El artefacto Maven del proyecto es:

```text
groupId:    mx.unam.heuristicas
artifactId: recocido-simulado-tsp
version:    1.0
```

Por lo tanto, el JAR generado es:

```text
target/recocido-simulado-tsp-1.0.jar
```

---

# Estructura del proyecto

La estructura general es similar a:

```text
recocido-simulado-tsp/
│
├── parametros/
│   ├── experiment.properties
│   └── heuristic.properties
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── mx/unam/heuristicas/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── ...
│   │
│   └── test/
│       ├── java/
│       └── resources/
│           └── application-test.properties
│
├── pom.xml
└── README.md
```

La clase principal del programa es:

```text
mx.unam.heuristicas.App
```

---

# Clonar el repositorio

```bash
git clone <URL_DEL_REPOSITORIO>
```

Entrar al proyecto:

```bash
cd recocido-simulado-tsp
```

Para descargar posteriormente los últimos cambios:

```bash
git pull
```

---

# Compilar el proyecto

Para compilar:

```bash
mvn compile
```

Para limpiar compilaciones anteriores y volver a compilar:

```bash
mvn clean compile
```

`clean` elimina el directorio:

```text
target/
```

antes de volver a construir el proyecto.

---

# Ejecutar pruebas

El proyecto utiliza **JUnit 5**.

## Todas las pruebas

```bash
mvn test
```

Se recomienda:

```bash
mvn clean test
```

## Una clase de pruebas

Por ejemplo:

```bash
mvn -Dtest=ThresholdAcceptingTest test
```

## Un método específico

```bash
mvn -Dtest=ThresholdAcceptingTest#nombreDelMetodo test
```

---

# Generar el JAR

La forma recomendada de validar y construir completamente el proyecto es:

```bash
mvn clean install
```

El ciclo de Maven realizará, entre otras tareas:

```text
limpieza
   ↓
compilación
   ↓
pruebas
   ↓
empaquetado
   ↓
generación del JAR ejecutable
   ↓
instalación en el repositorio local de Maven
```

Una construcción correcta termina con:

```text
BUILD SUCCESS
```

El JAR resultante se encuentra en:

```text
target/recocido-simulado-tsp-1.0.jar
```

Puede comprobarse ejecutando:

```bash
java -jar target/recocido-simulado-tsp-1.0.jar -h
```

---

# Ayuda del programa

El programa admite la opción:

```bash
-h
```

Ejemplo:

```bash
java -jar target/recocido-simulado-tsp-1.0.jar -h
```

La aplicación muestra:

```text
Uso:
    java -jar programa.jar -n archivo.tsp parametros.properties ruta-reportes/
    java -jar programa.jar -e archivo.tsp parametros.properties ruta-reportes/
```

Exceptuando `-h`, la aplicación requiere exactamente **cuatro argumentos**:

```text
1. modo
2. archivo TSP
3. archivo de parámetros
4. directorio de reportes
```

La forma general es:

```bash
java -jar target/recocido-simulado-tsp-1.0.jar \
<MODO> \
<ARCHIVO_TSP> \
<ARCHIVO_PROPERTIES> \
<DIRECTORIO_REPORTES>
```

Los modos disponibles son:

```text
-n    ejecución normal
-e    ejecución experimental
```

---

# Configuración de la base de datos

La configuración utilizada durante la ejecución normal del programa se encuentra en:

```text
src/main/resources/application.properties
```

Las propiedades reconocidas son:

```properties
db.type=
db.url=
db.user=
db.password=
```

---

## SQLite

Ejemplo:

```properties
# Database configuration sqlite

db.type=sqlite
db.url=jdbc:sqlite:/home/ricardo/Documentos/data/tsp.db
db.user=
db.password=
```

En SQLite normalmente no se requieren usuario ni contraseña.

La propiedad:

```properties
db.type=sqlite
```

indica que debe utilizarse SQLite.

La URL:

```properties
db.url=jdbc:sqlite:/home/ricardo/Documentos/data/tsp.db
```

debe apuntar al archivo `.db` correspondiente.

La ruta debe modificarse de acuerdo con la computadora en la que se ejecute
el programa.

Por ejemplo, en otra máquina podría utilizarse:

```properties
db.url=jdbc:sqlite:/home/usuario/data/tsp.db
```

---

## PostgreSQL

También puede utilizarse PostgreSQL.

Ejemplo:

```properties
db.type=postgres
db.url=jdbc:postgresql://localhost:5432/tsp?currentSchema=tsp
db.user=tsp_user
db.password=tu_password
```

En este caso:

```properties
db.type=postgres
```

indica que se utilizará PostgreSQL.

La URL:

```text
jdbc:postgresql://localhost:5432/tsp?currentSchema=tsp
```

indica:

```text
servidor: localhost
puerto:   5432
base:     tsp
esquema:  tsp
```

El parámetro:

```text
currentSchema=tsp
```

permite que las consultas se realicen sobre el esquema `tsp`.

> No se recomienda almacenar contraseñas reales en un repositorio público.

---

# Configuración de pruebas

Las pruebas que requieren una conexión real utilizan:

```text
src/test/resources/application-test.properties
```

La estructura es la misma que la configuración principal.

Por ejemplo, con SQLite:

```properties
# Database configuration sqlite

db.type=sqlite
db.url=jdbc:sqlite:/home/ricardo/Documentos/data/tsp.db
db.user=
db.password=
```

O con PostgreSQL:

```properties
db.type=postgres
db.url=jdbc:postgresql://localhost:5432/tsp?currentSchema=tsp
db.user=tsp_user
db.password=tu_password
```

La base de datos debe estar disponible antes de ejecutar las pruebas de
integración que dependan de ella.

---

# Base de datos

El sistema trabaja principalmente con las tablas:

```text
cities
connections
```

## `cities`

Contiene las ciudades.

Una estructura equivalente es:

```sql
CREATE TABLE cities (
    id INTEGER PRIMARY KEY,
    name TEXT,
    country TEXT,
    population INTEGER,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION
);
```

## `connections`

Contiene las conexiones disponibles entre ciudades:

```sql
CREATE TABLE connections (
    id_city_1 INTEGER,
    id_city_2 INTEGER,
    distance DOUBLE PRECISION,

    PRIMARY KEY (id_city_1, id_city_2),

    FOREIGN KEY (id_city_1)
        REFERENCES cities(id),

    FOREIGN KEY (id_city_2)
        REFERENCES cities(id)
);
```

Los identificadores incluidos en un archivo TSP deben corresponder con
ciudades existentes en la base.

---

# Formato de archivos TSP

Una instancia se especifica mediante un archivo `.tsp`.

El formato utilizado es una lista de identificadores de ciudades separados
por comas.

Ejemplo:

```text
1,2,3,4,5
```

El orden de los identificadores se conserva durante la creación de la
instancia.

También se permiten espacios:

```text
1, 2, 3, 4, 5
```

y una coma final:

```text
1,2,3,4,5,
```

No se permiten ciudades repetidas.

Por ejemplo:

```text
1,2,3,2,5
```

es inválido porque la ciudad `2` aparece dos veces.

También se rechazan:

```text
archivos vacíos
identificadores inválidos
rutas inexistentes
```

---

# Representación interna

Aunque las ciudades tengan identificadores arbitrarios en la base de datos,
la implementación utiliza internamente índices consecutivos.

Por ejemplo:

```text
IDs reales:
7, 25, 81, 103
```

pueden representarse internamente como:

```text
0 -> 7
1 -> 25
2 -> 81
3 -> 103
```

Esto permite utilizar eficientemente arreglos y matrices.

Al presentar la solución al usuario se pueden recuperar nuevamente los
identificadores reales.

---

# Función objetivo

La función objetivo determina el costo de una solución TSP.

El problema es de minimización:

```text
menor costo = mejor solución
```

Una solución está representada por una permutación de las ciudades.

Por ejemplo:

```text
[0, 4, 2, 1, 3]
```

El costo suma las conexiones entre posiciones consecutivas.

La implementación actual **no agrega una conexión de regreso desde la última
ciudad hacia la primera**.

Por ejemplo, para:

```text
[A, B, C, D]
```

se consideran:

```text
A -> B
B -> C
C -> D
```

pero no:

```text
D -> A
```

---

# Conexiones inexistentes

Si una conexión entre dos ciudades no existe en la tabla `connections`, el
sistema utiliza una penalización.

Esta penalización utiliza la distancia geográfica natural entre ambas
ciudades y un factor basado en las conexiones de la instancia.

El objetivo es que una solución que utiliza conexiones inexistentes resulte
menos conveniente que una solución construida únicamente con conexiones
válidas.

---

# Normalización

El costo utilizado por la heurística se encuentra normalizado.

La intención es que una solución formada únicamente por conexiones válidas
tenga un costo aproximadamente dentro de:

```text
[0, 1]
```

mientras que una solución con conexiones inexistentes pueda producir:

```text
costo > 1
```

Por esta razón, al analizar los resultados experimentales generalmente se
prefieren valores más pequeños.

---

# Vecindario

Para obtener una solución vecina se intercambian exactamente dos posiciones
de la permutación.

Ejemplo:

```text
Solución actual:

[0, 1, 2, 3, 4]

Vecino:

[0, 3, 2, 1, 4]
```

Los índices se seleccionan mediante un generador pseudoaleatorio.

---

# Uso general del programa

Existen dos modos:

```text
-n
-e
```

---

# Modo normal `-n`

El modo normal ejecuta una corrida de Threshold Accepting utilizando
parámetros definidos explícitamente.

La sintaxis es:

```bash
java -jar target/recocido-simulado-tsp-1.0.jar \
-n \
archivo.tsp \
parametros/heuristic.properties \
ruta-reportes/
```

Ejemplo:

```bash
java -jar target/recocido-simulado-tsp-1.0.jar \
-n \
src/main/resources/input-40.tsp \
parametros/heuristic.properties \
../reportes/
```

Los parámetros de la heurística se leen desde:

```text
parametros/heuristic.properties
```

---

# `heuristic.properties`

La estructura exacta es:

```properties
heuristic.initial-temperature=
heuristic.temperature-epsilon=
heuristic.cooling-factor=
heuristic.batch-size=
heuristic.max-attempts-per-batch=
heuristic.seed=
```

Ejemplo:

```properties
heuristic.initial-temperature=393216.0
heuristic.temperature-epsilon=0.00000016723012460800819
heuristic.cooling-factor=0.9932903145783581
heuristic.batch-size=4491
heuristic.max-attempts-per-batch=291915
heuristic.seed=262831
```

---

## `heuristic.initial-temperature`

Temperatura inicial de Threshold Accepting.

```properties
heuristic.initial-temperature=393216.0
```

La temperatura controla cuánto puede empeorar una solución y todavía ser
aceptada.

Una solución vecina \(s'\) se acepta cuando:

```text
f(s') <= f(s) + T
```

donde:

```text
s     solución actual
s'    solución vecina
f     función objetivo
T     temperatura actual
```

Por lo tanto, al inicio pueden aceptarse soluciones peores si su diferencia
de costo se encuentra dentro del umbral permitido por `T`.

---

## `heuristic.temperature-epsilon`

Temperatura mínima de la ejecución.

```properties
heuristic.temperature-epsilon=0.00000016723012460800819
```

La temperatura va disminuyendo hasta alcanzar este valor.

---

## `heuristic.cooling-factor`

Factor de enfriamiento.

```properties
heuristic.cooling-factor=0.9932903145783581
```

La temperatura se actualiza como:

```text
T_nueva = coolingFactor × T_actual
```

Valores más cercanos a `1` provocan un enfriamiento más lento.

Esto normalmente implica:

```text
más niveles de temperatura
más vecinos generados
mayor tiempo de ejecución
```

---

## `heuristic.batch-size`

Número de soluciones **aceptadas** requeridas para completar un lote.

```properties
heuristic.batch-size=4491
```

Un lote de tamaño:

```text
4491
```

significa que se intenta obtener:

```text
4491 vecinos aceptados
```

y no solamente generar 4491 vecinos.

---

## `heuristic.max-attempts-per-batch`

Máximo número de vecinos que pueden generarse intentando completar un lote.

```properties
heuristic.max-attempts-per-batch=291915
```

Este parámetro evita ciclos excesivamente largos cuando la probabilidad de
aceptación se vuelve muy pequeña.

Si no se consigue completar el lote antes de alcanzar este número de
intentos, el lote queda incompleto.

---

## `heuristic.seed`

Semilla utilizada durante la ejecución de Threshold Accepting.

```properties
heuristic.seed=262831
```

Utilizar exactamente la misma:

```text
instancia
+
configuración
+
semilla
```

permite reproducir la secuencia pseudoaleatoria utilizada por la heurística.

---

# Salida del modo normal

Además de mostrar los resultados de la optimización, el modo normal genera un
reporte de las evaluaciones aceptadas.

El archivo se llama:

```text
evaluaciones-aceptadas.txt
```

Cada línea tiene el formato:

```text
E:<costo>
```

Ejemplo:

```text
E:0.8231942381
E:0.7912834712
E:0.8012938172
E:0.7521938471
```

Cada valor corresponde al costo de un vecino que fue aceptado por Threshold
Accepting.

Este archivo puede utilizarse para graficar el comportamiento de la
heurística.

Este reporte se genera en modo:

```text
-n
```

y no durante las corridas masivas del modo experimental.

---

# Modo experimental `-e`

El modo experimental busca configuraciones adecuadas para Threshold
Accepting.

Sintaxis:

```bash
java -jar target/recocido-simulado-tsp-1.0.jar \
-e \
archivo.tsp \
parametros/experiment.properties \
ruta-reportes/
```

Ejemplo:

```bash
java -jar target/recocido-simulado-tsp-1.0.jar \
-e \
src/main/resources/input-150.tsp \
parametros/experiment.properties \
../reportes/
```

El modo experimental evalúa diferentes combinaciones de parámetros durante
varias generaciones.

Cada configuración se ejecuta utilizando múltiples semillas.

---

# `experiment.properties`

La estructura exacta del archivo es:

```properties
# ==========================================================
# Control general del experimento
# ==========================================================

experiment.generations=
experiment.candidates-per-generation=
experiment.seeds=


# ==========================================================
# Espacio de búsqueda: Threshold Accepting
# ==========================================================

experiment.cooling-factor.min=
experiment.cooling-factor.max=

experiment.temperature-epsilon.min=
experiment.temperature-epsilon.max=

experiment.batch-size.min=
experiment.batch-size.max=

experiment.max-attempt-factor.min=
experiment.max-attempt-factor.max=


# ==========================================================
# Cálculo de temperatura inicial
# ==========================================================

experiment.initial-temperature.guess=

experiment.target-acceptance.min=
experiment.target-acceptance.max=

experiment.acceptance-epsilon=
experiment.temperature-samples=
experiment.temperature-max-iterations=


# ==========================================================
# Evolución de parámetros
# ==========================================================

experiment.exploration-probability=
experiment.variation-factor=

experiment.parameter-seed=
```

---

# Control general del experimento

## `experiment.generations`

Número de generaciones.

Ejemplo:

```properties
experiment.generations=20
```

Cada generación contiene varias configuraciones candidatas.

Al terminar la generación se selecciona una configuración campeona que se
utiliza como referencia para producir la siguiente generación.

---

## `experiment.candidates-per-generation`

Número de configuraciones candidatas por generación.

```properties
experiment.candidates-per-generation=30
```

Si se utilizan:

```text
30 candidatos
10 semillas
```

se ejecutan:

```text
30 × 10 = 300 corridas
```

por generación.

Si además existen:

```text
20 generaciones
```

el experimento completo contiene:

```text
20 × 30 × 10 = 6000 corridas
```

---

## `experiment.seeds`

Lista de semillas base.

Ejemplo:

```properties
experiment.seeds=131321,131347,131389,131415,131442,131468,131503,131527,131561,131599
```

Cada configuración candidata se evalúa usando todas las semillas.

Esto permite medir si una configuración produce buenos resultados de manera
consistente y no solamente con una secuencia pseudoaleatoria favorable.

---

# Espacio de búsqueda de Threshold Accepting

## Factor de enfriamiento

```properties
experiment.cooling-factor.min=
experiment.cooling-factor.max=
```

Ejemplo:

```properties
experiment.cooling-factor.min=0.980
experiment.cooling-factor.max=0.9955
```

El experimento genera valores dentro de ese intervalo.

---

## Epsilon de temperatura

```properties
experiment.temperature-epsilon.min=
experiment.temperature-epsilon.max=
```

Ejemplo:

```properties
experiment.temperature-epsilon.min=0.0000003
experiment.temperature-epsilon.max=0.000002
```

Estos parámetros definen el intervalo de posibles temperaturas mínimas.

---

## Tamaño de lote

```properties
experiment.batch-size.min=
experiment.batch-size.max=
```

Ejemplo:

```properties
experiment.batch-size.min=2500
experiment.batch-size.max=5500
```

El tamaño de lote de cada candidato se genera dentro de este intervalo.

---

# Factor máximo de intentos

El modo experimental no configura directamente:

```text
maxAttemptsPerBatch
```

En su lugar utiliza:

```properties
experiment.max-attempt-factor.min=
experiment.max-attempt-factor.max=
```

Por ejemplo:

```properties
experiment.max-attempt-factor.min=50
experiment.max-attempt-factor.max=90
```

Para cada candidato se obtiene:

```text
maxAttemptsPerBatch =
    batchSize × maxAttemptFactor
```

Ejemplo:

```text
batchSize        = 4491
maxAttemptFactor = 65
```

entonces:

```text
maxAttemptsPerBatch =
4491 × 65
=
291915
```

---

# Cálculo de temperatura inicial

Durante el modo experimental la temperatura inicial no se especifica
directamente.

Se calcula antes de ejecutar Threshold Accepting.

Los parámetros utilizados son:

```properties
experiment.initial-temperature.guess=

experiment.target-acceptance.min=
experiment.target-acceptance.max=

experiment.acceptance-epsilon=
experiment.temperature-samples=
experiment.temperature-max-iterations=
```

---

## `experiment.initial-temperature.guess`

Valor inicial desde el que comienza la búsqueda de temperatura.

Ejemplo:

```properties
experiment.initial-temperature.guess=1.0
```

No significa necesariamente que Threshold Accepting vaya a comenzar con:

```text
T0 = 1.0
```

Es únicamente el punto inicial utilizado por el algoritmo encargado de
calcular una temperatura adecuada.

---

## `experiment.target-acceptance.min`

## `experiment.target-acceptance.max`

Definen el intervalo para el porcentaje de aceptación objetivo.

Ejemplo:

```properties
experiment.target-acceptance.min=0.84
experiment.target-acceptance.max=0.90
```

Un candidato podría utilizar:

```text
targetAcceptance = 0.87
```

lo que representa aproximadamente:

```text
87 %
```

de aceptación durante la calibración de la temperatura inicial.

Este valor se utiliza únicamente para determinar `T0`.

No significa que el 87 % de todos los vecinos generados durante la ejecución
completa de Threshold Accepting tengan que ser aceptados.

---

## `experiment.acceptance-epsilon`

Tolerancia utilizada al comparar el porcentaje de aceptación obtenido con el
objetivo.

Ejemplo:

```properties
experiment.acceptance-epsilon=0.0025
```

---

## `experiment.temperature-samples`

Número de vecinos utilizados para estimar la tasa de aceptación durante el
cálculo de temperatura.

Ejemplo:

```properties
experiment.temperature-samples=5000
```

Este valor:

```text
NO es el batchSize
```

y solamente pertenece al procedimiento encargado de calcular la temperatura
inicial.

---

## `experiment.temperature-max-iterations`

Número máximo de iteraciones permitidas durante la búsqueda de temperatura.

Ejemplo:

```properties
experiment.temperature-max-iterations=200
```

Funciona como límite de seguridad para la búsqueda de `T0`.

---

# Evolución de parámetros

## `experiment.exploration-probability`

Probabilidad de generar parámetros exploratorios.

Ejemplo:

```properties
experiment.exploration-probability=0.30
```

equivale a:

```text
30 %
```

La exploración ayuda a evitar que todas las nuevas configuraciones queden
concentradas demasiado pronto alrededor del campeón actual.

---

## `experiment.variation-factor`

Controla cuánto pueden variar los nuevos parámetros alrededor del campeón.

Ejemplo:

```properties
experiment.variation-factor=0.12
```

Valores mayores producen una búsqueda más amplia.

Valores menores concentran más la búsqueda alrededor de la configuración
actual.

---

## `experiment.parameter-seed`

Semilla utilizada para generar las configuraciones experimentales.

Ejemplo:

```properties
experiment.parameter-seed=20260912
```

Esta semilla es independiente de las semillas con las que se ejecuta
Threshold Accepting.

Su propósito es hacer reproducible la generación de configuraciones.

---

# Semillas y reproducibilidad

Durante un experimento existen diferentes tipos de semillas.

Para cada:

```text
baseSeed
```

se calculan:

```text
temperatureSeed = baseSeed × 2
executionSeed   = baseSeed × 2 + 1
```

Por ejemplo:

```text
baseSeed = 131415
```

produce:

```text
temperatureSeed = 262830
executionSeed   = 262831
```

---

## `baseSeed`

Es la semilla definida en:

```properties
experiment.seeds=
```

---

## `temperatureSeed`

Se utiliza durante el cálculo de la temperatura inicial:

```text
temperatureSeed = baseSeed × 2
```

---

## `executionSeed`

Se utiliza durante la ejecución real de Threshold Accepting:

```text
executionSeed = baseSeed × 2 + 1
```

---

# Reproducir una corrida experimental

Si se desea reproducir en modo normal una corrida encontrada en el modo
experimental, debe utilizarse:

```text
executionSeed
```

y no `baseSeed`.

Por ejemplo:

```text
baseSeed       = 131415
temperatureSeed = 262830
executionSeed   = 262831
```

Entonces:

```properties
heuristic.seed=262831
```

---

# Ejemplo de reproducción

Supóngase que una corrida experimental produjo:

```text
initialTemperature   = 393216.0
temperatureEpsilon   = 1.6723012460800819E-7
coolingFactor        = 0.9932903145783581
batchSize            = 4491
maxAttemptFactor     = 65
maxAttemptsPerBatch  = 291915
executionSeed        = 262831
```

Entonces puede configurarse:

```properties
heuristic.initial-temperature=393216.0
heuristic.temperature-epsilon=1.6723012460800819E-7
heuristic.cooling-factor=0.9932903145783581
heuristic.batch-size=4491
heuristic.max-attempts-per-batch=291915
heuristic.seed=262831
```

y ejecutar:

```bash
java -jar target/recocido-simulado-tsp-1.0.jar \
-n \
src/main/resources/input-150.tsp \
parametros/heuristic.properties \
../reportes/
```

---

# Reportes

El directorio de reportes se especifica mediante el cuarto argumento de la
aplicación.

Por ejemplo:

```bash
../reportes/
```

En modo experimental los principales reportes son:

```text
experiment-runs.csv
best-solutions.csv
generation-summary.csv
```

---

### Mapa de la mejor ruta

En el modo normal (`-n`), además del reporte de evaluaciones aceptadas, se genera automáticamente el archivo:

`mapa-ruta.html`

Este reporte muestra de forma interactiva la mejor ruta encontrada por la heurística sobre un mapa, utilizando las coordenadas geográficas (latitud y longitud) de las ciudades almacenadas en la base de datos.

Cada ciudad aparece marcada siguiendo el orden de visita de la solución encontrada. Al seleccionar un marcador se muestra información de la ciudad, como su nombre, país, identificador y coordenadas.

El mapa puede abrirse directamente desde cualquier navegador web y requiere conexión a Internet para cargar el mapa base de OpenStreetMap.

# `experiment-runs.csv`

Contiene información de cada corrida.

Entre sus columnas se encuentran:

```text
generation
baseSeed
temperatureSeed
executionSeed
initialTemperature
initialCost
bestCost
finalCost
coolingFactor
temperatureEpsilon
batchSize
maxAttemptFactor
maxAttemptsPerBatch
targetAcceptance
acceptanceEpsilon
temperatureSamples
temperatureMaxIterations
generatedNeighbors
acceptedNeighbors
temperatureLevels
elapsedNanos
```

---

## `initialCost`

Costo de la solución inicial.

---

## `bestCost`

Mejor costo encontrado durante toda la corrida.

Para un problema de minimización:

```text
menor bestCost = mejor resultado
```

---

## `finalCost`

Costo de la solución en la que finalizó la cadena.

No necesariamente coincide con `bestCost`.

Puede ocurrir:

```text
bestCost < finalCost
```

porque Threshold Accepting permite aceptar soluciones peores mientras se
encuentren dentro del umbral.

---

## `generatedNeighbors`

Número total de vecinos generados.

---

## `acceptedNeighbors`

Número total de vecinos aceptados.

La tasa global de aceptación puede calcularse como:

```text
acceptedNeighbors / generatedNeighbors
```

---

## `temperatureLevels`

Número de niveles de temperatura procesados.

---

## `elapsedNanos`

Tiempo empleado por la corrida en nanosegundos.

Para obtener segundos:

```text
segundos = elapsedNanos / 1,000,000,000
```

Ejemplo:

```text
40957230691 ns
```

equivale a aproximadamente:

```text
40.96 segundos
```

---

# `best-solutions.csv`

Contiene las mejores soluciones descubiertas durante el experimento junto con
la configuración que permitió encontrarlas.

Es especialmente útil para seleccionar una corrida y reproducirla
posteriormente utilizando `-n`.

---

# `generation-summary.csv`

Contiene un resumen de la configuración campeona de cada generación.

Permite estudiar la evolución de:

```text
costo medio
mejor costo
dispersión
parámetros
tiempo de ejecución
```

a medida que avanza el experimento.

---

# Selección del campeón

Cada configuración se ejecuta con todas las semillas especificadas.

Posteriormente se comparan sus resultados.

La selección utiliza, en orden:

```text
1. menor media de bestCost
2. menor desviación estándar de bestCost
3. menor tiempo medio de ejecución
```

Esto permite favorecer configuraciones que produzcan buenos resultados de
manera consistente y no solamente configuraciones que hayan tenido una sola
corrida excepcional.

---

# Resumen mostrado por generación

Durante el modo experimental la aplicación muestra información como:

```text
Campeón de la generación
Media del mejor costo
Mediana
Desviación estándar
Mejor corrida
Peor corrida
Parámetros
```

También se informa cuando aparece un nuevo mejor resultado global.

---

# Algoritmo Threshold Accepting

Dada una solución actual:

```text
s
```

se genera un vecino:

```text
s'
```

El vecino es aceptado cuando:

```text
f(s') <= f(s) + T
```

donde:

```text
f(s)   costo actual
f(s')  costo del vecino
T      temperatura
```

A diferencia de una búsqueda que solamente acepta mejoras, Threshold
Accepting puede aceptar temporalmente soluciones peores.

Esto ayuda a escapar de mínimos locales.

---

# Enfriamiento

Después de alcanzar las condiciones correspondientes a una temperatura, ésta
se reduce mediante:

```text
T <- coolingFactor × T
```

Conforme `T` disminuye, también disminuye la cantidad de deterioro permitido.

Cuando la temperatura se vuelve suficientemente pequeña, el algoritmo
termina.

---

# Mejor solución global

La solución actual y la mejor solución encontrada son conceptos diferentes.

Durante la búsqueda puede ocurrir:

```text
currentCost > bestCost
```

La implementación conserva independientemente la mejor solución descubierta
durante toda la ejecución.

Por esta razón se reportan tanto:

```text
bestCost
```

como:

```text
finalCost
```

---

# Ejecutar y guardar un log

Para ejecutar un experimento y guardar toda la salida:

```bash
java -jar target/recocido-simulado-tsp-1.0.jar \
-e \
src/main/resources/input-150.tsp \
parametros/experiment.properties \
../reportes/ \
2>&1 | tee ejecucion-150.log
```

Esto:

```text
muestra la salida en pantalla
+
guarda stdout
+
guarda stderr
```

en:

```text
ejecucion-150.log
```

---

# Ver el log en tiempo real

```bash
tail -f ejecucion-150.log
```

Para salir de `tail`:

```text
Ctrl + C
```

Esto no detiene el proceso Java.

---

# Comprobar si la ejecución sigue activa

Puede utilizarse:

```bash
pgrep -af recocido-simulado-tsp
```

o:

```bash
ps aux | grep '[j]ava.*recocido-simulado-tsp'
```

Si aparece una línea similar a:

```text
java -jar target/recocido-simulado-tsp-1.0.jar -e ...
```

el experimento continúa ejecutándose.

---

# Ejecuciones largas

Para experimentos grandes en servidores o máquinas virtuales se recomienda
utilizar `tmux`.

Crear una nueva sesión:

```bash
tmux new -s tsp150
```

Entrar al proyecto:

```bash
cd ~/recocido-simulado-tsp
```

Ejecutar:

```bash
java -jar target/recocido-simulado-tsp-1.0.jar \
-e \
src/main/resources/input-150.tsp \
parametros/experiment.properties \
../reportes/ \
2>&1 | tee ejecucion-150.log
```

---

# Salir de tmux sin detener el proceso

Presionar:

```text
Ctrl + B
```

y después:

```text
D
```

La sesión quedará desacoplada y el experimento continuará ejecutándose.

---

# Listar sesiones de tmux

```bash
tmux ls
```

---

# Volver a entrar

```bash
tmux attach -t tsp150
```

También puede utilizarse:

```bash
tmux a -t tsp150
```

---

# Flujo recomendado

Un flujo típico de trabajo es:

```text
1. Actualizar repositorio
2. Configurar base de datos
3. Ejecutar pruebas
4. Generar JAR
5. Ejecutar experimento
6. Analizar resultados
7. Reproducir la mejor corrida en modo normal
```

Comandos:

```bash
git pull
```

Después:

```bash
mvn clean install
```

Comprobar ayuda:

```bash
java -jar target/recocido-simulado-tsp-1.0.jar -h
```

Buscar parámetros:

```bash
java -jar target/recocido-simulado-tsp-1.0.jar \
-e \
src/main/resources/input-150.tsp \
parametros/experiment.properties \
../reportes/
```

Analizar:

```text
experiment-runs.csv
best-solutions.csv
generation-summary.csv
```

Copiar después una configuración seleccionada a:

```text
parametros/heuristic.properties
```

y reproducirla:

```bash
java -jar target/recocido-simulado-tsp-1.0.jar \
-n \
src/main/resources/input-150.tsp \
parametros/heuristic.properties \
../reportes/
```

Finalmente puede analizarse:

```text
evaluaciones-aceptadas.txt
```

---

# Errores comunes

## Número incorrecto de argumentos

Excepto para `-h`, deben enviarse exactamente cuatro argumentos.

Incorrecto:

```bash
java -jar target/recocido-simulado-tsp-1.0.jar -e input.tsp
```

Correcto:

```bash
java -jar target/recocido-simulado-tsp-1.0.jar \
-e \
input.tsp \
parametros/experiment.properties \
../reportes/
```

---

## Modo desconocido

Los únicos modos disponibles son:

```text
-n
-e
```

Para consultar la ayuda:

```bash
java -jar target/recocido-simulado-tsp-1.0.jar -h
```

---

## Error de conexión

Revisar:

```properties
db.type
db.url
db.user
db.password
```

También comprobar que:

```text
la base exista
el archivo SQLite exista
PostgreSQL esté ejecutándose
el esquema sea correcto
las credenciales sean válidas
```

---

## Error `No suitable driver`

Ejecutar nuevamente:

```bash
mvn clean install
```

y utilizar el JAR generado por Maven Shade:

```text
target/recocido-simulado-tsp-1.0.jar
```

El proyecto contiene las dependencias JDBC necesarias dentro del JAR
ejecutable.

---

# Desarrollo

Antes de subir cambios al repositorio se recomienda ejecutar:

```bash
mvn clean install
```

y comprobar que termine con:

```text
BUILD SUCCESS
```

Para revisar los archivos modificados:

```bash
git status
```

Después:

```bash
git add .
git commit -m "Descripción del cambio"
git push
```

---

# Autor

Proyecto desarrollado para el estudio de **Heurísticas de Optimización
Combinatoria** y su aplicación al **Problema del Agente Viajero**.

**Facultad de Ciencias**  
**Universidad Nacional Autónoma de México**

---

# Licencia

Este proyecto se distribuye bajo la licencia **GNU General Public License
(GPL)**.

Consulte:

```text
LICENSE
```

para conocer los términos completos de la licencia.
