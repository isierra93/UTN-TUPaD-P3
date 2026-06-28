# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Console-based Java/JPA backend for FoodStore (UTN TUPaD - Programación 3). It manages Categories, Products, Users, Orders, and Order Details via an interactive menu. The database is embedded H2 (file-based, auto-created). There is no REST layer yet — the frontend uses mock data and the two sides are not yet connected.

## Commands

Run from the `backend/` directory:

```bash
.\gradlew.bat run     # Start the interactive console menu (Windows)
.\gradlew.bat build   # Compile + assemble JAR
.\gradlew.bat test    # Run JUnit 5 tests
.\gradlew.bat clean   # Remove build output
```

On Linux/macOS replace `.\gradlew.bat` with `./gradlew`. The `run` task sets `standardInput = System.in` so the console menu accepts keyboard input.

## Architecture

### Layers

| Layer | Package | Role |
|-------|---------|------|
| Entry point | `org.example.Main` | Menu loop, input helpers, orchestration |
| Entities | `org.example.entities` | JPA-mapped domain objects |
| Repositories | `org.example.repository` | Data access — JPQL queries, manual transactions |
| Enums | `org.example.enums` | `Rol`, `Estado`, `FormaPago` |
| Util | `org.example.util.JPAUtil` | `EntityManagerFactory` singleton |
| DTOs | `org.example.DTOs` | Record-based view objects (currently `UsuarioDTO`) |

### Entity model

All entities extend `Base` (`@MappedSuperclass`), which provides `id` (auto-increment), `eliminado` (soft-delete flag), and `createdAt`.

Key relationships:
- `Categoria` 1:N `Producto` — cascade PERSIST/MERGE, lazy
- `Usuario` 1:N `Pedido` — cascade ALL
- `Pedido` 1:N `DetallePedido` — cascade ALL + orphan removal
- `Pedido` implements `Calculable` to derive `total` from line items

`eliminado = true` is the soft-delete convention. All `listarActivos()` calls filter `WHERE e.eliminado = false`.

### Repository pattern

`BaseRepository<T extends Base>` provides generic CRUD (`guardar`, `buscarPorId`, `listarActivos`, `eliminarLogico`). Each method creates a fresh `EntityManager`, manages its own transaction, and closes in a `finally` block.

Overrides to know:
- **`ProductoRepository.listarActivos()`** uses `LEFT JOIN FETCH` to avoid `LazyInitializationException` when accessing `categorias` outside a session.
- **`CategoriaRepository.eliminarLogico()`** cascades the soft-delete to all child `Producto` records.

### Database

H2 file-based database at `./data/mydb` (created automatically). Configuration in `src/main/resources/META-INF/persistence.xml`, persistence unit name `miUnidad`. Schema mode is `update` — data persists between runs. SQL logging is off by default; set `hibernate.show_sql = true` in persistence.xml to enable.

### Lombok usage

Entities use `@SuperBuilder`, `@Getter`, `@Setter`, `@AllArgsConstructor`. The build must have the Lombok annotation processor on the compile classpath (already configured in `build.gradle`).

## Enums reference

| Enum | Values |
|------|--------|
| `Rol` | `ADMIN`, `USUARIO` |
| `Estado` | `PENDIENTE`, `CONFIRMADO`, `TERMINADO`, `CANCELADO` |
| `FormaPago` | `TARJETA`, `TRANSFERENCIA`, `EFECTIVO` |
