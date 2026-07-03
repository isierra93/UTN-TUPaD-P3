import { defineConfig } from "vite";
import { dirname, resolve } from "node:path";
import { fileURLToPath } from "node:url";

const root = dirname(fileURLToPath(import.meta.url));

// App multi-pagina: cada .html es un punto de entrada del build.
export default defineConfig({
    build: {
        rollupOptions: {
            input: {
                main: resolve(root, "index.html"),
                login: resolve(root, "src/pages/auth/login/login.html"),
                register: resolve(root, "src/pages/auth/register/register.html"),
                home: resolve(root, "src/pages/store/home/home.html"),
                productDetail: resolve(root, "src/pages/store/productDetail/productDetail.html"),
                cart: resolve(root, "src/pages/store/cart/cart.html"),
                orders: resolve(root, "src/pages/client/orders/orders.html"),
                adminHome: resolve(root, "src/pages/admin/adminHome/adminHome.html"),
                adminCategories: resolve(root, "src/pages/admin/categories/categories.html"),
                adminProducts: resolve(root, "src/pages/admin/products/products.html"),
                adminOrders: resolve(root, "src/pages/admin/orders/orders.html"),
            },
        },
    },
});
