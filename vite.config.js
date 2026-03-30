import { dirname, resolve } from 'node:path';
import { defineConfig } from 'vite';

export default defineConfig({
    build: {
        rolldownOptions: {
            input: {
                index: resolve(dirname, "/index.html"),
                login: resolve(dirname, "src/pages/auth/login/login.html"),
                registro: resolve(dirname, "src/pages/auth/registro/registro.html"),
                adminHome: resolve(dirname, "src/pages/admin/home/home.html"),
                clientHome: resolve(dirname, "src/pages/client/home/home.html"),
            }
        }
    }
})