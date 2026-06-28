# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

FoodStore is a university project (UTN TUPaD - Programación 3) — a food e-commerce frontend built with Vite + TypeScript, no framework. The companion Java/JPA backend is referenced in PDFs but not present in this repo; the frontend currently runs fully offline using in-memory mock data.

## Commands

All commands run from the `frontend/` directory:

```bash
cd frontend
npm run dev      # start dev server (Vite, http://localhost:5173)
npm run build    # tsc type-check + Vite production build
npm run preview  # serve the production build locally
```

There are no tests or linting scripts configured.

## Architecture

### Multi-page app (MPA) pattern

Each route is a standalone `.html` + `.ts` pair under `src/pages/`. Vite treats each HTML file as an entry point. Navigation is done via `window.location.href` (see `utils/navigate.ts`).

Pages:
- `pages/auth/login/` — login form, reads users from `localStorage`
- `pages/auth/register/` — registration, writes new users to `localStorage` as `role: "client"`
- `pages/home/` — product catalog with category filter and search
- `pages/client/cart` — shopping cart
- `pages/admin/admin` — admin product table (accessible only to `role: "admin"`)

### Route protection

`src/main.ts` exports `protectRoute()`. Every page calls this at the top of its `.ts` file. The logic:
- Public routes (`/auth/**`, `index.html`, `/`) redirect authenticated users away.
- Private routes redirect unauthenticated users to login.
- Admin routes (`/admin/**`) redirect `client`-role users to home.

### Data layer (currently mocked)

`src/utils/getProducts.ts` and `src/utils/getCategories.ts` import directly from `src/data/data.ts` (hardcoded arrays). Commented-out `fetch("/api/v1/...")` blocks show the intended REST API shape for when a backend is connected.

### State / persistence

All state is stored in `localStorage`:
- `users` — array of `User` objects (registered accounts)
- `userData` — single `User` object representing the active session
- `cart` — array of `CartItem` objects

`getSession()` and `getCart()` wrap `localStorage` reads with try/catch that self-heals corrupted JSON by removing the bad key.

### Types

| Type | Key fields |
|------|-----------|
| `User` | `email`, `password`, `role: UserRole` |
| `UserRole` | `"client" \| "admin"` |
| `Product` | `id`, `nombre`, `precio`, `stock`, `imagen`, `disponible`, `eliminado`, `categorias[]` |
| `Category` | `id`, `nombre`, `eliminado` |
| `CartItem` | `id`, `name`, `price`, `quantity`, `imageUrl`, `category` |

`eliminado` acts as a soft-delete flag; `getProducts()` already filters these out.

### TypeScript config

Strict mode is enabled with `noUnusedLocals`, `noUnusedParameters`, and `noUncheckedSideEffectImports`. `noEmit: true` — Vite handles transpilation, `tsc` is type-check only.
