import type { Product } from "../types/Product";
import { productos } from "../data/data";

/* export const getProducts = async (): Promise<Product[]> => {
    try {
        const result = await fetch("/api/v1/productos");
        const data = await result.json();
        return data as Product[];
    } catch (error) {
        console.error("Error fetching products:", error); 
        return [];
    }
} */

export const getProducts = async (): Promise<Product[]> => {
  return productos.filter((p) => !p.eliminado);
}