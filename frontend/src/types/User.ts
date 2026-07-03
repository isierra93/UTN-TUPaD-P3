import type { Rol } from "./Rol";

export interface User {
    id: number;
    nombre: string;
    apellido: string;
    mail: string;
    celular: string;
    // Solo se usa para verificar el login; nunca se guarda en la sesion.
    password?: string;
    rol: Rol;
    eliminado?: boolean;
}
