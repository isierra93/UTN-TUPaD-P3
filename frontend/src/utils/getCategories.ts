import type { Category } from "../types/Category";
import { categorias } from "../data/data";


/* export const getCategories = async (): Promise<Category[]> => {
    try {
        const result = await fetch("/api/v1/categorias");
        const data = await result.json();
        return data as Category[];
    } catch (error) {
        console.error("Error fetching categories:", error); 
        return [];
    }
} */

export const getCategories = async (): Promise<Category[]> => {
  return categorias.filter((c) => !c.eliminado);
}