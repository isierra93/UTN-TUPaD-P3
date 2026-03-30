import type { Product } from "../types/Product";

export const getProducts = async (): Promise<Product[]> => {
    try {
        const result = await fetch("/products.json");
        const data = await result.json();
        return data as Product[];
    } catch (error) {
        console.error("Error fetching products:", error); 
        return [];
    }
}