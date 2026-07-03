# FoodStore Frontend

Interfaz web del sistema **FoodStore**, desarrollada con **TypeScript + Vite** (HTML5 y CSS3, sin framework) como parte del Trabajo Práctico Integrador de Programación 3 (UTN TUPaD).

## Descripción

Aplicación web multi-página (MPA) para una tienda de comida. En esta iteración el frontend consume todos sus datos desde archivos `.json` locales mediante `fetch()`, de modo que los flujos de usuario funcionan de forma independiente al backend. La capa de fetch está aislada: para conectar la API REST de la Parte 2 basta con reemplazar la URL de cada `fetch` (ej. `/data/productos.json` → `/api/products`).

Funcionalidades:
- **Autenticación** con roles (ADMIN / USUARIO), sesión persistida en `localStorage`.
- **Catálogo**: filtro por categoría, búsqueda en tiempo real y ordenamiento (nombre / precio).
- **Detalle de producto** con selector de cantidad y validación de stock.
- **Carrito** persistente en `localStorage` con subtotal, envío y total.
- **Checkout** que genera el pedido en `localStorage` e **Historial de pedidos** del cliente.
- **Panel de administración**: dashboard con estadísticas, y ABM de categorías, productos y pedidos.

## Tecnologías

- TypeScript ~5.9
- Vite ^8 (build multi-página)
- HTML5, CSS3
- Persistencia y sesión: `localStorage`

## Requisitos previos

- Node.js 18 o superior
- npm

## Instalación y ejecución

```bash
cd frontend
npm install
npm run dev      # servidor de desarrollo en http://localhost:5173
```

Otros scripts:

```bash
npm run build    # type-check (tsc) + build de producción en dist/
npm run preview  # sirve la build de producción localmente
```

## Credenciales de prueba

| Rol | Email | Contraseña |
|-----|-------|-----------|
| **ADMIN** | `admin@admin.com` | `123456` |
| **USUARIO** (cliente) | `cliente@food.com` | `cliente123` |

Al iniciar sesión, un ADMIN es redirigido al panel de administración y un USUARIO al catálogo. También podés registrarte como cliente (rol USUARIO) desde la página de registro.

## Fuente de datos

Los datos de prueba están en `public/data/` y se consumen por `fetch()`:

| Archivo | Contenido |
|---------|-----------|
| `categorias.json` | `id, nombre, descripcion, imagen, eliminado` |
| `productos.json` | `id, nombre, precio, descripcion, stock, imagen, disponible, eliminado, categoriaId` |
| `usuarios.json` | `id, nombre, apellido, mail, celular, password, rol` |
| `pedidos.json` | `id, fecha, estado, total, formaPago, idUsuario, detalles[]` |

Las relaciones se modelan con **identificadores planos** (`categoriaId`, `idUsuario`, `idProducto`).

### Nota sobre la persistencia

- El **carrito**, la **sesión** (`userData`) y los **pedidos generados en el checkout** se guardan en `localStorage`.
- Las operaciones de escritura del **panel de administración** (crear / editar / eliminar categorías, productos y cambiar estado de pedidos) se aplican **solo en memoria**: al recargar la página se pierde el estado modificado. Esto es intencional para esta iteración; en la siguiente se conectará al backend.

### Costo de envío

El envío es una **constante fija** definida en `src/utils/config.ts`:

```ts
export const ENVIO = 5500;
```

El total de un pedido se calcula como `subtotal + ENVIO`.

## Estructura del proyecto

```
frontend/
├── public/
│   └── data/                     # JSON de prueba (fetch)
│       ├── categorias.json
│       ├── productos.json
│       ├── usuarios.json
│       └── pedidos.json
├── src/
│   ├── main.ts                   # protectRoute() — guardas de ruta por rol
│   ├── style.css                 # estilos globales
│   ├── types/                    # Category, Product, User, Rol, Pedido, DetallePedido, CartItem
│   ├── utils/                    # capa de fetch, sesión, carrito, navegación, config (ENVIO)
│   └── pages/
│       ├── auth/{login,register}/
│       ├── store/{home,productDetail,cart}/
│       ├── client/orders/        # historial de pedidos del cliente
│       └── admin/{adminHome,categories,products,orders}/
├── index.html                    # redirección inicial
├── vite.config.ts                # entradas del build multi-página
├── tsconfig.json
└── package.json
```

## Notas de seguridad (solo fines educativos)

Este proyecto **no** implementa seguridad real: las contraseñas se comparan en texto plano contra el JSON, no hay tokens y la validación de rol es únicamente en el frontend (`localStorage` es fácilmente modificable).
