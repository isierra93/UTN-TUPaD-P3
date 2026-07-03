import { navigate } from "./utils/navigate";
import { getSession } from "./utils/getSession";
import { LOGIN_URL, HOME_URL } from "./utils/relativeUrls";
import type { User } from "./types/User";

function isPublicRoute(pathname: string): boolean {
    return (
        pathname.includes('/auth/') ||
        pathname.endsWith('index.html') ||
        pathname === '/'
    );
}

function isAdminRoute(pathname: string): boolean {
    return pathname.includes('/admin/');
}

// Cierra la sesion: limpia los datos del usuario y el carrito, y vuelve al login.
export function logout(): void {
    localStorage.removeItem('userData');
    localStorage.removeItem('cart');
    navigate(LOGIN_URL);
}

// Configura el header compartido en cada pagina privada:
// - engancha el boton de cerrar sesion (#logout-btn)
// - oculta el acceso al panel admin (#admin-link) si el usuario no es ADMIN
function initHeader(user: User): void {
    const logoutBtn = document.querySelector<HTMLElement>('#logout-btn');
    logoutBtn?.addEventListener('click', (e) => {
        e.preventDefault();
        logout();
    });

    if (user.rol !== 'ADMIN') {
        document.querySelector<HTMLElement>('#admin-link')?.remove();
    }
}

export function protectRoute(): void {
    const pathname = window.location.pathname.toLowerCase();
    const user = getSession();

    // rutas publicas: si ya hay sesion, se va al home
    if (isPublicRoute(pathname)) {
        if (user) navigate(HOME_URL);
        return;
    }

    // rutas privadas: sin sesion, al login
    if (!user) {
        navigate(LOGIN_URL);
        return;
    }

    // rutas de admin: solo rol ADMIN
    if (isAdminRoute(pathname) && user.rol !== 'ADMIN') {
        navigate(HOME_URL);
        return;
    }

    // ya autenticado en una ruta privada: preparamos el header comun
    initHeader(user);
}
