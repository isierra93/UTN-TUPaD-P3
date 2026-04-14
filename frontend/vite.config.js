import { dirname, resolve } from 'node:path';
import { defineConfig } from 'vite';

export default defineConfig({
    build: {
        rolldownOptions: {
            input: {
                index: resolve(__dirname, "index.html"),
                home: resolve(__dirname, "src/pages/home/home.html") ,
                login: resolve(__dirname, "src/pages/auth/login/login.html"),
                register: resolve(__dirname, "src/pages/auth/register/register.html"),
                admin: resolve(__dirname, "src/pages/admin/admin.html"),
            }
        }
    }
})