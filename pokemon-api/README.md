# Pokémon Catalog Microservices

The repository is a small Spring Boot 3 / Java 21 learning example with two independently runnable catalog services and the existing public API retained as a facade.

## Services and ownership

| App | Default port | Responsibility |
| --- | ---: | --- |
| Type Catalog | 8081 | Owns type definitions and their stable internal codes; seeds the 18 built-in types. |
| Pokémon Catalog | 8082 | Owns Pokémon records, type-code assignments, search, pagination, and CSV seeding. Validates type identifiers through Type Catalog over HTTP. |
| `pokemon-api` facade | 8080 | Preserves the public `/api/v1` routes, composes type counts and type-scoped queries, and maps downstream errors to the existing public error format. |

Each catalog has its own in-memory H2 database and uses `ddl-auto: create-drop`. User-created changes are lost on restart. The Pokémon service stores type codes rather than a cross-service JPA relationship. Type Catalog's internal code remains stable if its display name is changed.

## Run locally

Run each command in a separate terminal from the repository root, and start the services in this order:

```powershell
mvn -pl type-catalog-service spring-boot:run
mvn -pl pokemon-catalog-service spring-boot:run
mvn -pl pokemon-api spring-boot:run
```

Wait for Type Catalog to start and seed its types before starting Pokémon Catalog; it resolves the CSV's type names during startup and fails with a clear error if Type Catalog is unavailable. The facade can start after both catalog services.

The facade serves Swagger UI at [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) and OpenAPI JSON at [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs). Catalog service URLs are configurable with `TYPE_CATALOG_URL` and `POKEMON_CATALOG_URL`; service ports and bind addresses can be changed with `SERVER_PORT` and `SERVER_ADDRESS`. The internal services bind to loopback by default and have no authentication; this setup is intended for local demos.

## Public API

The facade preserves the existing routes:

### Pokémon (`/api/v1/pokemon`)

| Method | Route | Description |
| --- | --- | --- |
| `GET` | `/api/v1/pokemon` | Paged list with optional `name` and `type` filters, `page`, `size`, `sortBy`, and `direction`. Out-of-range pages return the last page. |
| `GET` | `/api/v1/pokemon/{id}` | Get a Pokémon by internal ID. |
| `GET` | `/api/v1/pokemon/pokedex/{number}` | Get forms by Pokédex number. |
| `GET` | `/api/v1/pokemon/random` | Get a random Pokémon. |
| `POST` | `/api/v1/pokemon` | Create a Pokémon; accepts one or two type names or IDs. |
| `PUT` | `/api/v1/pokemon/{id}` | Replace a Pokémon and its type assignments. |
| `DELETE` | `/api/v1/pokemon/{id}` | Delete a Pokémon. |

### Types (`/api/v1/types`)

| Method | Route | Description |
| --- | --- | --- |
| `GET` | `/api/v1/types` | List types with total, primary, and secondary Pokémon counts. |
| `GET` | `/api/v1/types/{id}` | Get a type by ID, including counts. |
| `GET` | `/api/v1/types/name/{name}` | Get a type by name, including counts. |
| `POST` | `/api/v1/types` | Create a type. |
| `PUT` | `/api/v1/types/{id}` | Update a type's display name or description. |
| `DELETE` | `/api/v1/types/{id}` | Delete an unassigned type; returns `409 Conflict` if Pokémon still use it. |
| `GET` | `/api/v1/types/{id}/pokemon` | Page Pokémon assigned to a type ID. |
| `GET` | `/api/v1/types/name/{name}/pokemon` | Page Pokémon assigned to a type name. |

Type deletion is checked by the facade using the Pokémon service's current assignment count before deleting the type. The check and delete span separate databases and are not atomic under concurrent writes; that consistency limitation is intentional for this small synchronous demo.

## Build and tests

From the repository root:

```powershell
mvn test
mvn package
```

The internal service APIs are under `/internal/v1`; the facade is the supported public API.
