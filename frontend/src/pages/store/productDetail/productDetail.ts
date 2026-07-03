import type { Product } from "../../../types/Product";
import type { Category } from "../../../types/Category";
import type { CartItem } from "../../../types/CartItem";
import { getProducts } from "../../../utils/getProducts";
import { getCategories } from "../../../utils/getCategories";
import { protectRoute } from "../../../main";

protectRoute();

const container = document.getElementById("detail-container")!;
const FALLBACK_IMG = "https://cdn.producttutor.com/2022/10/16451874451570033394image1-min-ib7z19a601i3rol733qs1.png";

// Actualiza el badge del carrito en el encabezado.
function cartCount(): void {
    const cart: CartItem[] = JSON.parse(localStorage.getItem("cart") || "[]");
    const count = cart.reduce((total, item) => total + item.quantity, 0);
    const el = document.getElementById("cart-count");
    if (el) el.textContent = count.toString();
}

// Agrega (o incrementa) el producto en el carrito, respetando el stock.
function addToCart(product: Product, categoria: string, cantidad: number): void {
    const cart: CartItem[] = JSON.parse(localStorage.getItem("cart") || "[]");
    const existing = cart.find((item) => item.id === product.id);

    if (existing) {
        existing.quantity = Math.min(existing.quantity + cantidad, product.stock);
    } else {
        cart.push({
            id: product.id,
            name: product.nombre,
            price: product.precio,
            quantity: Math.min(cantidad, product.stock),
            imageUrl: product.imagen,
            category: categoria,
        });
    }

    localStorage.setItem("cart", JSON.stringify(cart));
    cartCount();
}

function renderNotFound(): void {
    container.innerHTML = `
        <div class="detail__notfound">
            <p>No se encontró el producto solicitado.</p>
            <a class="btn" href="../home/home.html">Volver al catálogo</a>
        </div>`;
}

function renderDetail(product: Product, categoria: string): void {
    const agotado = !product.disponible || product.stock === 0;

    container.innerHTML = `
        <div class="detail__media">
            <img class="detail__image" src="${product.imagen}" alt="${product.nombre}"
                 onerror="this.src='${FALLBACK_IMG}'">
        </div>
        <div class="detail__info">
            <span class="detail__category">${categoria}</span>
            <h1 class="detail__title">${product.nombre}</h1>
            <p class="detail__desc">${product.descripcion}</p>
            <p class="detail__price">$${product.precio.toFixed(2)}</p>
            <p class="detail__stock">Stock disponible: <strong>${product.stock}</strong></p>
            <p class="detail__status">
                Estado: <strong>${agotado ? "No disponible" : "Disponible"}</strong>
            </p>

            <div class="detail__actions">
                <div class="detail__qty">
                    <button class="btn cart-action-btn" type="button" id="qty-dec" ${agotado ? "disabled" : ""}>-</button>
                    <input id="qty" type="number" min="1" max="${product.stock}" value="1" ${agotado ? "disabled" : ""}>
                    <button class="btn cart-action-btn" type="button" id="qty-inc" ${agotado ? "disabled" : ""}>+</button>
                </div>
                <button class="btn btn--primary" type="button" id="add-btn" ${agotado ? "disabled" : ""}>
                    ${agotado ? "Sin stock" : "Agregar al carrito"}
                </button>
            </div>
            <p id="feedback" class="detail__feedback" role="status"></p>
        </div>`;

    if (agotado) return;

    const qtyInput = document.getElementById("qty") as HTMLInputElement;
    const feedback = document.getElementById("feedback")!;

    const clampQty = (): number => {
        let v = parseInt(qtyInput.value, 10);
        if (isNaN(v) || v < 1) v = 1;
        if (v > product.stock) v = product.stock;
        qtyInput.value = v.toString();
        return v;
    };

    document.getElementById("qty-dec")!.addEventListener("click", () => {
        qtyInput.value = Math.max(1, clampQty() - 1).toString();
    });
    document.getElementById("qty-inc")!.addEventListener("click", () => {
        qtyInput.value = Math.min(product.stock, clampQty() + 1).toString();
    });
    qtyInput.addEventListener("change", clampQty);

    document.getElementById("add-btn")!.addEventListener("click", () => {
        const cantidad = clampQty();
        addToCart(product, categoria, cantidad);
        feedback.textContent = `Agregado al carrito: ${product.nombre} x${cantidad}.`;
    });
}

const init = async () => {
    cartCount();

    const params = new URLSearchParams(window.location.search);
    const id = Number(params.get("id"));
    if (!id) {
        renderNotFound();
        return;
    }

    try {
        const products = await getProducts();
        const product = products.find((p) => p.id === id);
        if (!product) {
            renderNotFound();
            return;
        }
        const categories: Category[] = await getCategories();
        const categoria = categories.find((c) => c.id === product.categoriaId)?.nombre || "Sin categoría";
        renderDetail(product, categoria);
    } catch (error) {
        console.error("Error cargando el detalle:", error);
        renderNotFound();
    }
};

init();
