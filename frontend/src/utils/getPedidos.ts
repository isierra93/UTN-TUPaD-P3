import type { Pedido } from "../types/Pedido";

// Obtiene los pedidos desde el JSON local mas los generados en localStorage
// (checkout de esta iteracion). Reemplazable por "/api/orders".
export const getPedidos = async (): Promise<Pedido[]> => {
    try {
        const response = await fetch("/data/pedidos.json");
        const base = (await response.json()) as Pedido[];
        const locales: Pedido[] = JSON.parse(localStorage.getItem("pedidos") || "[]");
        return [...base, ...locales];
    } catch (error) {
        console.error("Error fetching orders:", error);
        return [];
    }
};
