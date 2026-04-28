import type { Product } from "../../types/Product";
import type { Category } from "../../types/Category";
import { getProducts } from "../../utils/getProducts";
import { getCategories } from "../../utils/getCategories";
import { protectRoute } from "../../main";

protectRoute();

const containerProducts = document.querySelector<HTMLElement>('#products-container');
if (containerProducts) {
    containerProducts.innerHTML = `<p class="loading">Cargando productos...</p>`;
}
const containerCategories = document.querySelector<HTMLElement>('#list-categories');
if (containerCategories) {
    containerCategories.innerHTML = `<p class="loading">Cargando categorías...</p>`;
}

const searchInput = document.querySelector<HTMLInputElement>('#search');
const noResultsMessage = document.querySelector<HTMLElement>('#no-results');

if (searchInput) {
    searchInput.addEventListener('input', () => {
        const query = searchInput.value.toLowerCase();
        const productCards = document.querySelectorAll('.product-card');
        let visibleCount = 0;

        productCards.forEach(card => {
            const title = card.querySelector('.product-card__title')?.textContent?.toLowerCase() || '';
            const category = card.querySelector('.product-card__category')?.textContent?.toLowerCase() || '';

            const isVisible = title.includes(query) || category.includes(query);

            if (isVisible) {
                (card as HTMLElement).style.display = 'flex';
                visibleCount++;
            } else {
                (card as HTMLElement).style.display = 'none';
            }
        });

        if (noResultsMessage) {
            noResultsMessage.style.display = visibleCount === 0 ? 'block' : 'none';
        }
    });
}

//Agregar render condicional de admin button

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

// Función para actualizar el contador del carrito
function cartCount(): void {
    const cart = JSON.parse(localStorage.getItem('cart') || '[]');
    const count = cart.reduce((total: number, item: { quantity: number }) => total + item.quantity, 0);
    const cartCountElement = document.getElementById('cart-count');
    if (cartCountElement) {
        cartCountElement.textContent = count.toString();
    }
}
cartCount();

function renderProducts(products: Product[]): void {
    if (!containerProducts) return;
    containerProducts.innerHTML = "";
    if (products.length === 0) {
        containerProducts.innerHTML = `<p class="no-products">No hay productos disponibles.</p>`;
        return;
    }

    function addToCart(productId: number): void {
        const cart = JSON.parse(localStorage.getItem('cart') || '[]');
        const existingProduct = cart.find((item: { id: number }) => item.id === productId);

        if (existingProduct) {
            existingProduct.quantity += 1;
        } else {
            cart.push({ id: productId, quantity: 1 });
        }

        localStorage.setItem('cart', JSON.stringify(cart));
    }

    products.forEach(product => {
        const card = document.createElement("article");
        card.classList.add("product-card");
        card.innerHTML = `
            <img class="product-card__image" src="${product.imagen}" alt="${product.descripcion}" onerror="this.src='https://cdn.producttutor.com/2022/10/16451874451570033394image1-min-ib7z19a601i3rol733qs1.png'">
            <div class="product-card__body">
                <span class="product-card__category">${product.categorias[0]?.nombre || 'Sin categoría'}</span>
                <h3 class="product-card__title">${product.nombre}</h3>
                <p class="product-card__desc">${product.descripcion}</p>
                <div class="product-card__footer">
                <span class="product-card__price">${product.precio}</span>
                <button class="btn add-to-cart" type="button">Agregar</button>
                </div>
            </div>`;
        containerProducts.appendChild(card);

        const addBtn = card.querySelector<HTMLButtonElement>('.add-to-cart');
        if (addBtn) {
            addBtn.addEventListener('click', () => {
                addToCart(product.id);
                cartCount();
            });
        }
    });
};


function renderCategories(categories: Category[]): void {
    if (!containerCategories) return;
    containerCategories.innerHTML = "";
    const li = document.createElement("li");
    li.innerHTML = `<a href="#" class="categories__link">Todos</a>`;
    li.addEventListener('click', (e) => {
        e.preventDefault();
        const productCards = document.querySelectorAll('.product-card');
        productCards.forEach(card => {
            (card as HTMLElement).style.display = 'flex';
        });
    });
    containerCategories.appendChild(li);

    categories.forEach(category => {
        const li = document.createElement("li");
        li.innerHTML = `<a href="#" class="categories__link">${category.nombre}</a>`;
        li.addEventListener('click', (e) => {
            e.preventDefault();

            const productCards = document.querySelectorAll('.product-card');
            productCards.forEach(card => {
                const categoryText = card.querySelector('.product-card__category')?.textContent || '';
                const isVisible = categoryText.toLowerCase() === category.nombre.toLowerCase();
                (card as HTMLElement).style.display = isVisible ? 'flex' : 'none';
            });
        });

        containerCategories.appendChild(li);
    });
};

renderHome();