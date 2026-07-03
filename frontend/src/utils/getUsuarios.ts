import type { User } from "../types/User";

// Obtiene los usuarios desde el JSON local. Reemplazable por "/api/users".
export const getUsuarios = async (): Promise<User[]> => {
    try {
        const response = await fetch("/data/usuarios.json");
        const data = (await response.json()) as User[];
        return data.filter((u) => !u.eliminado);
    } catch (error) {
        console.error("Error fetching users:", error);
        return [];
    }
};
