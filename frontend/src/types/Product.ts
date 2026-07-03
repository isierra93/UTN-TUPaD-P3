export interface Product {
    id: number;
    nombre: string;
    precio: number;
    descripcion: string;
    stock: number;
    imagen: string;
    disponible: boolean;
    eliminado: boolean;
    // Identificador plano de la categoria (la relacion se resuelve por id).
    categoriaId: number;
}
