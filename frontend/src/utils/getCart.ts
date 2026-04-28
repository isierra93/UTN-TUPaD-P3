import type { CartItem } from '../types/CartItem';

export const getCart = async (): Promise<CartItem[]> => {
    try {
        const raw = localStorage.getItem('cart');
        return raw ? (JSON.parse(raw) as CartItem[]) : [];
    } catch {
        localStorage.removeItem('cart'); // carrito corrupto → lo limpiamos
        return [];
    }
}