import { navigate } from "./utils/navigate";
import { getSession } from "./utils/getSession";

export const LOGIN_URL = '/src/pages/auth/login/login.html';
export const HOME_URL  = '/src/pages/home/home.html';


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

export function protectRoute(): void {
    const pathname = window.location.pathname.toLowerCase();
    const user = getSession();

    // public routes
    if (isPublicRoute(pathname)) {
        if (user) navigate(HOME_URL);
        return;
    }

    // private routes
    if (!user) {
        navigate(LOGIN_URL);
        return;
    }

    //admin routes
    if (isAdminRoute(pathname) && user.role === 'client') {
        navigate(HOME_URL);
    }
}