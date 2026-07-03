# FoodStore — Sistema de Gestión de Pedidos de Comida

Trabajo Práctico Integrador de **Programación 3** (UTN — Tecnicatura Universitaria en Programación a Distancia).

Link Youtube: https://youtu.be/slGGeZWoBpU

FoodStore es un sistema de gestión de pedidos de comida implementado en **dos partes independientes y complementarias**:

- **`frontend/`** — Aplicación web (TypeScript + Vite) que resuelve todos los flujos de usuario consumiendo datos desde archivos `.json` locales.
- **`backend/`** — Aplicación de consola (Java + JPA/Hibernate + H2) que gestiona la persistencia y la lógica de negocio mediante un menú interactivo.

Ambas partes modelan el mismo dominio (categorías, productos, usuarios y pedidos con sus detalles), pero se desarrollan y ejecutan por separado. La integración vía API REST queda planteada para una iteración futura.

## Estructura del repositorio

```
UTN-TUPaD-P3/
├── frontend/     # Web app Vite + TypeScript (ver frontend/README.md)
├── backend/      # Consola Java + JPA + H2  (ver backend/README.md)
├── README.md     # este archivo
└── LICENSE
```

Cada carpeta tiene su propio `README.md` con instrucciones detalladas.

## Cómo ejecutar

### Frontend
```bash
cd frontend
npm install
npm run dev        # http://localhost:5173
```
Credenciales de prueba: `admin@admin.com` / `123456` (ADMIN) — `cliente@food.com` / `cliente123` (USUARIO).

### Backend
```bash
cd backend
./gradlew run      # Linux/macOS
.\gradlew.bat run  # Windows
```
La base H2 en archivo se crea automáticamente en `backend/data/`.

## Modelo de dominio (común a ambas partes)

| Entidad | Campos principales |
|---------|--------------------|
| Categoria | nombre, descripcion |
| Producto | nombre, precio, descripcion, stock, imagen, disponible |
| Usuario | nombre, apellido, mail (único), celular, rol |
| Pedido | fecha, estado, total, formaPago |
| DetallePedido | cantidad, subtotal, producto |

Enums: `Rol` (ADMIN, USUARIO) · `Estado` (PENDIENTE, CONFIRMADO, TERMINADO, CANCELADO) · `FormaPago` (TARJETA, TRANSFERENCIA, EFECTIVO).

## Decisiones de diseño

### Backend (Java / JPA)

- **Herencia con `Base`** (`@MappedSuperclass`): todas las entidades heredan `id`, `eliminado` y `createdAt`, evitando repetir el mapeo común.
- **Baja lógica** en lugar de borrado físico: `eliminado = true`. Ningún listado activo muestra registros dados de baja.
- **Relaciones unidireccionales**: `Categoria → Producto`, `Usuario → Pedido` y `Pedido → DetallePedido` se modelan con `@OneToMany @JoinColumn` desde el lado "dueño"; el lado "muchos" no declara la referencia inversa. La única `@ManyToOne` es `DetallePedido → Producto`. Para navegar de un producto a su categoría o de un pedido a su usuario se usan consultas JPQL, no referencias en el objeto. Esto mantiene el grafo de objetos simple y coherente con la consigna.
- **`BaseRepository<T>` genérico**: CRUD reutilizable (`guardar`, `buscarPorId`, `listarActivos`, `eliminarLogico`). `guardar` usa `persist()` si el id es `null` (alta) o `merge()` si ya existe (actualización). Cada método abre su propio `EntityManager` y lo cierra en `finally`.
- **Alta de pedido atómica**: toda la operación (validar stock/disponibilidad, calcular subtotales y total, reducir stock y persistir) ocurre en **una única transacción**. Si cualquier validación falla, se hace `rollback` completo y no se modifica nada. Los productos se recuperan con `em.find()` dentro de esa transacción para que queden gestionados y el descuento de stock se sincronice al commit.
- **Reportes con JPQL**: productos por categoría, pedidos por usuario, pedidos por estado y total facturado (suma de pedidos `TERMINADO`).

### Frontend (TypeScript / Vite)

- **App multi-página (MPA)**: cada ruta es un par `.html` + `.ts` bajo `src/pages/`. Vite trata cada HTML como un punto de entrada del build (`vite.config.ts`).
- **Capa de datos desacoplada**: los datos se consumen por `fetch()` desde `public/data/*.json`. La capa está aislada en `utils/` para que la migración a la API REST sea solo cambiar la URL del fetch.
- **Identificadores planos en los JSON** (`categoriaId`, `idUsuario`, `idProducto`), aproximando el formato que devolvería la API. El mapeo a los objetos anidados del backend se resolvería en la capa de serialización.
- **Persistencia selectiva en `localStorage`**: carrito, sesión activa y pedidos generados en el checkout se persisten. Las escrituras del panel de administración (ABM de categorías/productos, cambio de estado de pedidos) son **solo en memoria** y se pierden al recargar — intencional para esta iteración.
- **Protección de rutas por rol** (`protectRoute()`): las rutas públicas redirigen a usuarios ya logueados, las privadas exigen sesión, y las de `/admin/` solo admiten rol `ADMIN`.
- **Envío como constante** de configuración (`ENVIO = 5500` en `utils/config.ts`); el total del pedido es `subtotal + ENVIO`.

## Seguridad

El sistema es **solo con fines educativos**: las contraseñas se comparan en texto plano, no hay tokens y la validación de rol del frontend es fácilmente evitable. No debe usarse en producción.
