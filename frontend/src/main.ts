import { navigate } from "./utils/navigate";
import { getSession } from "./utils/getSession";
import { LOGIN_URL, HOME_URL } from "./utils/relativeUrls";

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