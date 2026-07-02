# FoodStore Backend

Backend de consola para el sistema FoodStore, desarrollado con Java y JPA/Hibernate como parte del Trabajo Práctico Integrador de la materia Programación 3 (UTN TUPaD).

## Descripción

Aplicación de consola interactiva que gestiona las entidades principales de una tienda de alimentos: Categorías, Productos, Usuarios y Pedidos. Utiliza JPA con Hibernate como proveedor de persistencia y una base de datos embebida H2 (archivo local). No requiere instalación de base de datos externa.

El sistema permite:
- ABM de Categorías (con baja lógica en cascada a Productos)
- ABM de Productos (con validaciones de precio y stock)
- ABM de Usuarios (con búsqueda por mail y control de roles)
- Gestión de Pedidos (alta, cambio de estado, descuento y restauración de stock)

## Tecnologías

- Java 17+
- Hibernate 6.4 (proveedor JPA)
- Jakarta Persistence 3.1
- H2 Database (archivo local)
- Lombok
- Gradle

## Requisitos previos

- JDK 17 o superior instalado
- No requiere ninguna instalación adicional (Gradle Wrapper incluido)

## Instalación y ejecución

1. Clonar o descomprimir el proyecto.
2. Abrir una terminal en la carpeta `backend/`.
3. Ejecutar el menú interactivo:

**Windows:**
```bash
.\gradlew.bat run
```

**Linux / macOS:**
```bash
./gradlew run
```

La base de datos H2 se crea automáticamente en `backend/data/mydb` en el primer arranque. Los datos persisten entre ejecuciones.

## Otros comandos

```bash
.\gradlew.bat build   # Compilar y ensamblar el JAR
.\gradlew.bat test    # Ejecutar los tests
.\gradlew.bat clean   # Limpiar los artefactos de compilación
```

## Estructura del proyecto

```
src/main/java/org/example/
├── Main.java                  # Menú de consola y lógica de interacción
├── model/                     # Entidades JPA y enums
│   ├── Base.java
│   ├── Categoria.java
│   ├── Producto.java
│   ├── Usuario.java
│   ├── Pedido.java
│   ├── DetallePedido.java
│   ├── Calculable.java
│   └── enums/
│       ├── Rol.java
│       ├── Estado.java
│       └── FormaPago.java
├── repository/                # Capa de acceso a datos (JPA/JPQL)
│   ├── BaseRepository.java
│   ├── CategoriaRepository.java
│   ├── ProductoRepository.java
│   ├── UsuarioRepository.java
│   └── PedidoRepository.java
└── util/
    └── JPAUtil.java           # Singleton de EntityManagerFactory
```
