# ⚡ Pokémon REST API - Spring Boot 3 & Java 21

API RESTful moderna, robusta y completamente documentada para la gestión de Pokémon y Pokédex, desarrollada con **Spring Boot 3** y **Java 21**.

---

## 🚀 Características Principales

- **Arquitectura en capas limpia**: Controller -> Service -> Repository -> Model / DTO.
- **Entidad `PokemonType` (Tabla `types`)**: Los tipos están mapeados a una tabla entidad JPA independiente (`types`). La clase `Pokemon` se relaciona mediante `primaryType` (tipo primario) y `secondaryType` (tipo secundario, opcional).
- **Consultas y Conteo Completo de Tipos**: Tanto las consultas por tipo (`GET /api/v1/types/{id}/pokemon`, `GET /api/v1/types/name/{name}/pokemon`, `GET /api/v1/pokemon?type=...`) como el conteo de Pokémon (`GET /api/v1/types`) tienen en cuenta tanto el **tipo primario** como el **tipo secundario**. El conteo desglosa:
  - `pokemonCount`: Total de Pokémon con ese tipo.
  - `primaryCount`: Cantidad con ese tipo como primario.
  - `secondaryCount`: Cantidad con ese tipo como secundario.
- **Borrado controlado con error estructurado**: Al intentar eliminar un tipo asignado a uno o más Pokémon (`DELETE /api/v1/types/{id}`), la API no lanza errores de base de datos no controlados; devuelve una respuesta controlada con código HTTP **409 Conflict** y un mensaje explicativo en JSON.
- **Endpoints completos para Tipos (`/api/v1/types`)**:
  - CRUD completo para tipos (crear, listar, consultar por ID o nombre, modificar y eliminar).
  - Consulta de lista de Pokémon por tipo específico (`/api/v1/types/{id}/pokemon` y `/api/v1/types/name/{name}/pokemon`).
- **Corrección de método PUT**: Actualización completa de datos y sincronización segura de tipos asociados sin pérdida de consistencia ni errores de detached entities.
- **Paginación inteligente**: Al enviar parámetros `page` y `size` fuera de rango (cuando no existen suficientes registros para llenar páginas posteriores), la API devuelve automáticamente la **última página con los últimos Pokémon disponibles** en lugar de una lista vacía `[]`.
- **Validación de datos**: Implementada con Jakarta Bean Validation (`@Valid`, `@NotNull`, `@NotBlank`, etc.).
- **Persistencia con Spring Data JPA**: Base de datos relacional H2 en memoria preconfigurada.
- **Carga de datos completa desde CSV (`pokemon.csv`)**: Inicialización automática de los 18 tipos elementales y **más de 1200 Pokémon** a partir del archivo CSV con estadísticas completas, formas alternativas (Mega evoluciones, formas regionales, etc.), generación y arte oficial.
- **Documentación Interactiva Swagger / OpenAPI 3**: Interfaz gráfica lista para explorar y probar endpoints en el navegador.
- **Manejo global de excepciones**: Respuestas de error estructuradas en formato JSON (`GlobalExceptionHandler`).
- **Soporte CORS**: Habilitado para consumir la API fácilmente desde clientes frontend (React, Angular, Vue, Flutter, etc.).

---

## 🛠️ Tecnologías

- **Java**: 21 (LTS)
- **Framework**: Spring Boot 3.3.5
- **ORM / Persistencia**: Spring Data JPA & Hibernate
- **Base de datos**: H2 Database (In-Memory)
- **Documentación**: Springdoc OpenAPI / Swagger UI 2.6.0
- **Herramienta de construcción**: Apache Maven

---

## 📂 Estructura del Proyecto

```text
src/
├── main/
│   ├── java/com/pokemon/api/
│   │   ├── PokemonApiApplication.java
│   │   ├── config/
│   │   │   ├── CorsConfig.java
│   │   │   ├── DataInitializer.java
│   │   │   └── OpenApiConfig.java
│   │   ├── controller/
│   │   │   ├── PokemonController.java
│   │   │   └── PokemonTypeController.java
│   │   ├── dto/
│   │   │   ├── ErrorResponse.java
│   │   │   ├── PokemonRequestDTO.java
│   │   │   ├── PokemonResponseDTO.java
│   │   │   ├── PokemonTypeRequestDTO.java
│   │   │   └── PokemonTypeResponseDTO.java
│   │   ├── exception/
│   │   │   ├── DuplicateResourceException.java
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── ResourceNotFoundException.java
│   │   ├── model/
│   │   │   ├── Pokemon.java
│   │   │   └── PokemonType.java
│   │   ├── repository/
│   │   │   ├── PokemonRepository.java
│   │   │   └── PokemonTypeRepository.java
│   │   └── service/
│   │       ├── PokemonService.java
│   │       ├── PokemonServiceImpl.java
│   │       ├── PokemonTypeService.java
│   │       └── PokemonTypeServiceImpl.java
│   └── resources/
│       └── application.yml
└── test/
    └── java/com/pokemon/api/
        ├── PokemonApiApplicationTests.java
        └── controller/
            ├── PokemonControllerTest.java
            └── PokemonTypeControllerTest.java
```

---

## ⚙️ Cómo Ejecutar el Proyecto

```bash
mvn spring-boot:run
```

La aplicación arrancará por defecto en el puerto `8080`.

---

## 📖 Documentación Interactiva y Consola H2

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
- **Consola de Base de Datos H2**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  - **JDBC URL**: `jdbc:h2:mem:pokemondb`
  - **User**: `sa`
  - **Password**: *(dejar en blanco)*

---

## 📡 Endpoints de la API

### 🐾 Pokémon (`/api/v1/pokemon`)

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/api/v1/pokemon` | Listar Pokémon con paginación (`page`, `size`, `sortBy`, `direction`) y filtros (`name`, `type`). Si `page` supera el total, retorna la última página. |
| `GET` | `/api/v1/pokemon/{id}` | Obtener Pokémon por ID interno |
| `GET` | `/api/v1/pokemon/pokedex/{number}` | Obtener Pokémon por número nacional de Pokédex |
| `GET` | `/api/v1/pokemon/random` | Obtener un Pokémon aleatorio |
| `POST` | `/api/v1/pokemon` | Crear nuevo Pokémon |
| `PUT` | `/api/v1/pokemon/{id}` | Actualizar Pokémon existente (incluyendo sus tipos) |
| `DELETE` | `/api/v1/pokemon/{id}` | Eliminar Pokémon por ID |

### 🔥 Tipos de Pokémon (`/api/v1/types`)

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/api/v1/types` | Listar todos los tipos registrados |
| `GET` | `/api/v1/types/{id}` | Obtener tipo por ID |
| `GET` | `/api/v1/types/name/{name}` | Obtener tipo por nombre (ej: `FIRE`, `WATER`) |
| `POST` | `/api/v1/types` | Registrar nuevo tipo |
| `PUT` | `/api/v1/types/{id}` | Modificar datos de un tipo |
| `DELETE` | `/api/v1/types/{id}` | Eliminar un tipo (devuelve HTTP 409 controlado si está asignado como primario o secundario) |
| `GET` | `/api/v1/types/{id}/pokemon` | Listar Pokémon de un tipo por su ID (paginado con fallback inteligente) |
| `GET` | `/api/v1/types/name/{name}/pokemon` | Listar Pokémon de un tipo por su nombre (paginado con fallback inteligente) |

---

## 🧪 Ejemplos cURL

### 1. Paginación con retorno de la última página (si supera el límite)
```bash
# Solicitando página 99 con tamaño 4:
curl -X GET "http://localhost:8080/api/v1/pokemon?page=99&size=4"
```

### 2. Obtener la lista de Pokémon de un tipo concreto
```bash
# Por nombre del tipo:
curl -X GET "http://localhost:8080/api/v1/types/name/FIRE/pokemon"

# Por ID del tipo:
curl -X GET "http://localhost:8080/api/v1/types/2/pokemon"
```

### 3. Actualizar un Pokémon existente (PUT)
```bash
curl -X PUT "http://localhost:8080/api/v1/pokemon/1" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Bulbasaur Entrenado",
    "pokedexNumber": 1,
    "types": ["GRASS", "POISON"],
    "hp": 55,
    "attack": 58,
    "defense": 58,
    "specialAttack": 70,
    "specialDefense": 70,
    "speed": 50,
    "height": 0.7,
    "weight": 7.0,
    "description": "Bulbasaur tras un entrenamiento intensivo.",
    "spriteUrl": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png"
  }'
```

### 4. Crear un nuevo Tipo de Pokémon (POST)
```bash
curl -X POST "http://localhost:8080/api/v1/types" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "COSMIC",
    "description": "Tipo cósmico de Pokémon extraterrestres o espaciales."
  }'
```
