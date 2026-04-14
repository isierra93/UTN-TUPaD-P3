import type { User } from "../types/User";

export function getSession(): User | null {
    try {
        const raw = localStorage.getItem('userData');
        return raw ? (JSON.parse(raw) as User) : null;
    } catch {
        localStorage.removeItem('userData'); // sesión corrupta → la limpiamos
        return null;
    }
}