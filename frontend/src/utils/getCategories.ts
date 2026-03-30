import type { Category } from "../types/Category";

export const getCategories = async (): Promise<Category[]> => {
    try {
        const result = await fetch("/categories.json");
        const data = await result.json();
        return data as Category[];
    } catch (error) {
        console.error("Error fetching categories:", error); 
        return [];
    }
}