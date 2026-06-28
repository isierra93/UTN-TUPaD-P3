# TP JPA — Programación III (UTN)

Aplicación de consola desarrollada en Java con JPA (Hibernate) y base de datos H2 embebida.
Implementa un ABM completo de Categorias y Productos con persistencia relacional.

## Tecnologías

- Java 17+
- Hibernate 6.4 (proveedor JPA)
- Jakarta Persistence 3.1
- H2 Database (archivo local)
- Lombok
- Gradle

## Estructura del proyecto

```
src/main/java/org/example/
├── entities/          # Entidades JPA: Base, Categoria, Producto, Pedido, DetallePedido, Usuario
├── enums/             # Enums: Estado, FormaPago, Rol
├── repository/        # Repositorios: BaseRepository, CategoriaRepository, ProductoRepository
├── util/              # JPAUtil: singleton del EntityManagerFactory
└── Main.java          # Punto de entrada — menú de consola
src/main/resources/
└── META-INF/
    └── persistence.xml
```

## Cómo ejecutar

### Requisitos previos

- JDK 17 o superior instalado
- No requiere ninguna instalación adicional (Gradle Wrapper incluido)

### Pasos

1. Clonar o descomprimir el proyecto.

2. Abrir una terminal en la carpeta raíz del proyecto.

3. Ejecutar el siguiente comando:

**Windows:**
```bash
.\gradlew.bat run
```

**Linux / macOS:**
```bash
./gradlew run
```

4. La aplicación iniciará el menú principal en la consola:

```
==============================
       MENU PRINCIPAL
==============================
1. Gestionar Categorias
2. Gestionar Productos
3. Reportes
0. Salir
```

> La base de datos H2 se crea automáticamente en la carpeta `data/` al iniciar la aplicación.
> El esquema se recrea en cada ejecución (`drop-and-create`).

## Funcionalidades

### Categorias
- **Alta**: ingreso de nombre (obligatorio) y descripción. Muestra el ID generado.
- **Baja lógica**: marca `eliminado = true`. El registro permanece en la BD.
- **Modificación**: muestra valores actuales; dejar un campo vacío conserva el valor anterior.
- **Listado**: muestra todas las categorías activas.

### Productos
- **Alta**: selección de categoría activa, ingreso de nombre, descripción, precio (> 0) y stock (≥ 0).
- **Baja lógica**: marca `eliminado = true`. Muestra el nombre del producto afectado.
- **Modificación**: muestra valores actuales; valida precio > 0 y stock ≥ 0.
- **Listado**: muestra todos los productos activos con su categoría.

### Reportes
- **Productos por categoría**: consulta JPQL que filtra productos activos por categoría seleccionada.
