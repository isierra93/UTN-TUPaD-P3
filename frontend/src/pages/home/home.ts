import type { Product } from "../../types/Product";
import type { Category } from "../../types/Category";
import { getProducts } from "../../utils/getProducts";
import { getCategories } from "../../utils/getCategories";

const containerProducts = document.querySelector<HTMLElement>('#products-container');
if (containerProducts) {
    containerProducts.innerHTML = `<p class="loading">Cargando productos...</p>`;
}
const containerCategories = document.querySelector<HTMLElement>('#list-categories');
if (containerCategories) {
    containerCategories.innerHTML = `<p class="loading">Cargando categorías...</p>`;
}


const renderHome = async () => {
    try {
        const products: Product[] = await getProducts();
        const categories: Category[] = await getCategories();
        renderProducts(products);
        renderCategories(categories);
    } catch (error) {
        console.error("Error rendering products:", error);
    }
};

function renderProducts(products: Product[]): void {
    if (!containerProducts) return;
    containerProducts.innerHTML = "";
    if (products.length === 0) {
        containerProducts.innerHTML = `<p class="no-products">No hay productos disponibles.</p>`;
        return;
    }
    products.forEach(product => {
        const card = document.createElement("article");
        card.classList.add("product-card");
        card.innerHTML = `
            <img class="product-card__image" src="${product.image}" alt="${product.description}">
            <div class="product-card__body">
                <span class="product-card__category">${product.category}</span>
                <h3 class="product-card__title">${product.title}</h3>
                <p class="product-card__desc">${product.description}</p>
                <div class="product-card__footer">
                <span class="product-card__price">${product.price}</span>
                <button class="btn" type="button">Agregar</button>
                </div>
            </div>`;
        containerProducts.appendChild(card);
    });
};

function renderCategories(categories: Category[]): void {
    if (!containerCategories) return;
    containerCategories.innerHTML = "";
    categories.forEach(category => {
        const li = document.createElement("li");
        li.innerHTML = `<a href="#" class="categories__link">${category.name}</a>`;
        containerCategories.appendChild(li);
    });
};

renderHome();