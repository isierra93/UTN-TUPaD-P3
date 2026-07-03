import type { Category } from "../types/Category";

// Obtiene las categorias desde el JSON local. Reemplazable por "/api/categories".
export const getCategories = async (): Promise<Category[]> => {
    try {
        const response = await fetch("/data/categorias.json");
        const data = (await response.json()) as Category[];
        return data.filter((c) => !c.eliminado);
    } catch (error) {
        console.error("Error fetching categories:", error);
        return [];
    }
};
