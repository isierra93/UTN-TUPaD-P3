# Frontend - Aplicación Web de E-commerce

Una aplicación web moderna de e-commerce construida con **TypeScript** y **Vite**, diseñada para ofrecer una experiencia de compra fluida con autenticación de usuarios y gestión de carrito.

## Descripción del Proyecto

Este proyecto es una aplicación frontend para una plataforma de e-commerce que permite a los usuarios:
- Explorar catálogo de productos (alimentos/comidas)
- Registrarse e iniciar sesión
- Agregar productos al carrito
- Ver y gestionar pedidos
- Panel administrativo para gestionar productos y categorías

## Estructura del Proyecto (Scaffolding)

```
frontend/
├── src/
│   ├── pages/                    # Páginas de la aplicación
│   │   ├── home/
│   │   │   ├── home.html
│   │   │   └── home.ts
│   │   ├── auth/
│   │   │   ├── login/
│   │   │   │   └── login.html
│   │   │   └── register/
│   │   │       └── register.html
│   │   ├── client/
│   │   │   ├── cart.html
│   │   │   └── myorders.html
│   │   └── admin/
│   │       └── admin.html
│   ├── types/                    # Tipos TypeScript
│   │   ├── Product.ts
│   │   ├── User.ts
│   │   └── UserRole.ts
│   ├── utils/                    # Funciones utilitarias
│   │   ├── getCategories.ts
│   │   ├── getProducts.ts
│   │   └── navigate.ts
│   ├── assets/                   # Recursos (imágenes)
│   │   ├── hero.png
│   │   ├── pizza.jpg
│   │   ├── hamburguesa.jpg
│   │   ├── sandwich_pollo.jpg
│   │   ├── ensalada_cesar.jpg
│   │   └── tarta_manzana.jpg
│   ├── style.css                 # Estilos globales
│   ├── main.ts                   # Punto de entrada
│   └── datos.js                  # Datos de ejemplo
├── public/                        # Archivos públicos estáticos
│   ├── favicon.svg
│   └── icons.svg
├── dist/                          # Build compilado (generado)
├── index.html                     # HTML principal
├── package.json                   # Dependencias y scripts
├── tsconfig.json                  # Configuración TypeScript
├── vite.config.js                 # Configuración Vite
├── .gitignore                     # Archivos a ignorar en Git
└── package-lock.json              # Versiones exactas de dependencias
```

## Tecnologías Utilizadas

| Herramienta | Versión | Propósito |
|-----------|---------|----------|
| **TypeScript** | ~5.9.3 | Tipado estático y mejora de código |
| **Vite** | ^8.0.1 | Build tool y dev server |
| **Node.js/ES2023** | ES2023 | Target JavaScript moderno |

## Requisitos Previos

- **Node.js** (versión 16 o superior)
- **npm** (incluido con Node.js)

## Instalación

1. Clona o descarga el proyecto:
```bash
cd frontend
```

2. Instala las dependencias:
```bash
npm install
```

## Scripts Disponibles

### Desarrollo
```bash
npm run dev
```
Inicia el servidor de desarrollo con hot reload. La aplicación estará disponible en `http://localhost:5173`

### Build
```bash
npm run build
```
Compila TypeScript y genera la build optimizada en la carpeta `dist/`

### Preview
```bash
npm run preview
```
Visualiza la build compilada localmente antes de desplegar

## Características Principales

### 🏠 Página de Inicio (Home)
- Catálogo de productos
- Filtrado por categorías
- Visualización de detalles de productos

### 🔐 Autenticación
- Página de login
- Página de registro
- Gestión de roles de usuario (cliente/admin)

### 🛒 Carrito de Compras
- Agregar/eliminar productos
- Modificar cantidades
- Ver total de compra

### 👤 Área de Cliente
- Historial de pedidos
- Gestión de perfil

### ⚙️ Panel Administrativo
- Gestión de productos
- Gestión de categorías
- Estadísticas

## Tipos de Datos

### Product
```typescript
interface Product {
  id: string
  name: string
  description: string
  price: number
  category: string
  image: string
  stock: number
}
```

### User
```typescript
interface User {
  id: string
  email: string
  name: string
  role: UserRole
}
```

### UserRole
- `admin`: Administrador del sistema
- `client`: Cliente de la tienda

## Funciones Utilitarias

- **`getProducts()`**: Obtiene el listado de productos
- **`getCategories()`**: Obtiene las categorías disponibles
- **`navigate(path)`**: Navega entre páginas de la aplicación

## Configuración de TypeScript

La configuración es estricta y moderna:
- Target: **ES2023**
- Modo módulo: **ESNext**
- Incluye validaciones estrictas (no unused variables, no any, etc.)
- Soporte para importación de extensiones TS

## Build

La configuración de Vite usa múltiples puntos de entrada (multi-page app):
- `index.html`: Página principal
- `src/pages/home.html`: Página de inicio

Puedes descomentar más puntos de entrada en `vite.config.js` según necesites.

## Próximos Pasos

- [ ] Implementar conexión a API backend
- [ ] Completar formularios de registro y login
- [ ] Crear lógica del carrito de compras
- [ ] Implementar sistema de pagos
- [ ] Agregar más páginas del admin

## Licencia

Este proyecto es privado.

---

**Desarrollado con TypeScript + Vite** 🚀
