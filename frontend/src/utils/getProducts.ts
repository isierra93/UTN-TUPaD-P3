import type { Product } from "../types/Product";

// Obtiene los productos desde el JSON local. En la iteracion con backend
// bastara con reemplazar la URL por el endpoint REST (ej: "/api/products").
export const getProducts = async (): Promise<Product[]> => {
    try {
        const response = await fetch("/data/productos.json");
        const data = (await response.json()) as Product[];
        return data.filter((p) => !p.eliminado);
    } catch (error) {
        console.error("Error fetching products:", error);
        return [];
    }
};
