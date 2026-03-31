import type { UserRole } from './UserRole';

export interface User {
//    id: number;
    email: string;
    password: string;
    role: UserRole;
}