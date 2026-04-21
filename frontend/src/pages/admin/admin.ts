import type { Product } from "../../types/Product";
import { getProducts } from "../../utils/getProducts";
import { getCategories } from "../../utils/getCategories";
import type { Category } from "../../types/Category";
import { protectRoute } from "../../main";

protectRoute();

const containerAdminCategories = document.getElementById("p-cat");
if (containerAdminCategories) {
    containerAdminCategories.innerHTML = `<option value="" disabled selected>Selecciona una categoría</option>`;
}

const containerAdminProducts = document.getElementById("admin-products");
if (containerAdminProducts) {
    containerAdminProducts.innerHTML = '<p class="loading">Cargando productos...</p>';
}

const renderAdminHome = async () => {
    if (!containerAdminCategories) return;
    try {
        const products: Product[] = await getProducts();
        const listOfCategories: Category[] = await getCategories();

        renderAdminCategories(listOfCategories);
        renderAdminProducts(products);

    } catch (error) {
        console.error("Error rendering admin categories:", error);
    }

};

function renderAdminProducts(products: Product[]): void {
    if (!containerAdminCategories) return;
    containerAdminProducts!.innerHTML = "";
    if (products.length === 0) {
        containerAdminProducts!.innerHTML = `<p class="no-products">No hay productos disponibles.</p>`;
        return;
    }
    products.forEach(product => {
        const tr = document.createElement("tr");
        tr.innerHTML =
            `<tr>
              <td>${product.id}</td>
              <td><img class="table-desktop__thumb" src="${product.imagen}" alt="${product.nombre}"></td>
              <td>${product.nombre}</td>
              <td>${product.categorias[0]?.nombre || "Sin categoría"}</td>
              <td>$${product.precio.toFixed(2)}</td>
              <td>${product.stock}</td>
              <td class="table-desktop__actions">
                <a href="#">Editar</a>
                <a href="#">Eliminar</a>
              </td>`
        containerAdminProducts!.appendChild(tr);
    });
}

function renderAdminCategories(categories: Category[]): void {
            if (!containerAdminCategories) return;
            categories.forEach(category => {
                const option = document.createElement("option");
                option.value = category.nombre;
                option.textContent = category.nombre;
                containerAdminCategories.appendChild(option);
            });
        }

renderAdminHome();