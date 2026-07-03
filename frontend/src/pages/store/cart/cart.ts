import type { CartItem } from "../../../types/CartItem";
import type { Pedido, FormaPago } from "../../../types/Pedido";
import type { DetallePedido } from "../../../types/DetallePedido";
import { getCart } from "../../../utils/getCart";
import { getProducts } from "../../../utils/getProducts";
import { getSession } from "../../../utils/getSession";
import { navigate } from "../../../utils/navigate";
import { ENVIO } from "../../../utils/config";
import { ORDERS_URL, LOGIN_URL } from "../../../utils/relativeUrls";
import { protectRoute } from "../../../main";

protectRoute();

const containerCartProducts = document.getElementById("cart-products");
const emptyCartMessage = document.getElementById("empty-cart");
const tableProducts = document.getElementById("item-cart");
const subtotalElement = document.getElementById("cart-subtotal");
const envioElement = document.getElementById("cart-envio");
const cartTotalElement = document.getElementById("cart-total");
const cleanCartButton = document.getElementById("clear-cart-btn") as HTMLButtonElement;
const checkoutButton = document.getElementById("checkout-btn") as HTMLButtonElement;
const checkoutForm = document.getElementById("checkout-form") as HTMLFormElement;

// Stock por producto para respetar el maximo al modificar cantidades.
let stockMap = new Map<number, number>();

const carrito: CartItem[] = await getCart();
const productos = await getProducts();
stockMap = new Map(productos.map((p) => [p.id, p.stock]));

cleanCartButton?.addEventListener("click", () => {
    localStorage.removeItem("cart");
    renderCartProducts([]);
});

checkoutButton?.addEventListener("click", () => {
    // Alterna la visibilidad del formulario de checkout.
    checkoutForm.style.display = checkoutForm.style.display === "none" ? "block" : "none";
});

checkoutForm?.addEventListener("submit", (event) => {
    event.preventDefault();
    finalizarCompra();
});

function renderCartProducts(cartItems: CartItem[]): void {
    if (!containerCartProducts || !emptyCartMessage || !tableProducts) return;
    containerCartProducts.innerHTML = "";

    if (cartItems.length === 0) {
        emptyCartMessage.style.display = "block";
        tableProducts.style.display = "none";
        return;
    }
    emptyCartMessage.style.display = "none";
    tableProducts.style.display = "block";

    const subtotal = cartItems.reduce((sum, item) => sum + item.price * item.quantity, 0);
    if (subtotalElement) subtotalElement.textContent = subtotal.toFixed(2);
    if (envioElement) envioElement.textContent = ENVIO.toFixed(2);
    if (cartTotalElement) cartTotalElement.textContent = (subtotal + ENVIO).toFixed(2);
    checkoutButton.disabled = false;

    cartItems.forEach((item) => {
        const tr = document.createElement("tr");
        tr.innerHTML =
            `<td>${item.name}</td>
             <td><img class="table-desktop__thumb" src="${item.imageUrl}" alt="${item.name}"></td>
             <td>${item.quantity}</td>
             <td>
                <div class="cart-item-actions">
                  <button class="btn cart-action-btn" type="button" id="increase-qty-${item.id}"> + </button>
                  <button class="btn cart-action-btn" type="button" id="decrease-qty-${item.id}"> - </button>
                </div>
             </td>
             <td>$${item.price}</td>
             <td>$${(item.price * item.quantity).toFixed(2)}</td>`;
        containerCartProducts.appendChild(tr);
    });

    // Listeners de +/- por item.
    cartItems.forEach((item) => {
        const increaseBtn = document.getElementById(`increase-qty-${item.id}`);
        const decreaseBtn = document.getElementById(`decrease-qty-${item.id}`);
        increaseBtn?.addEventListener("click", () => updateCartItemQuantity(item.id, item.quantity + 1));
        decreaseBtn?.addEventListener("click", () => updateCartItemQuantity(item.id, item.quantity - 1));
    });
}

function updateCartItemQuantity(productId: number, newQuantity: number): void {
    const cart: CartItem[] = JSON.parse(localStorage.getItem("cart") || "[]");
    const itemIndex = cart.findIndex((item) => item.id === productId);
    if (itemIndex === -1) return;

    if (newQuantity <= 0) {
        cart.splice(itemIndex, 1);
    } else {
        // No permitir superar el stock disponible del producto.
        const stock = stockMap.get(productId) ?? newQuantity;
        if (newQuantity > stock) {
            alert(`Stock insuficiente. Disponible: ${stock}.`);
            return;
        }
        cart[itemIndex].quantity = newQuantity;
    }
    localStorage.setItem("cart", JSON.stringify(cart));
    renderCartProducts(cart);
}

function finalizarCompra(): void {
    const session = getSession();
    if (!session) {
        navigate(LOGIN_URL);
        return;
    }

    const cart: CartItem[] = JSON.parse(localStorage.getItem("cart") || "[]");
    if (cart.length === 0) {
        alert("Tu carrito está vacío.");
        return;
    }

    const celular = (document.getElementById("checkout-celular") as HTMLInputElement).value.trim();
    const formaPago = (document.getElementById("checkout-pago") as HTMLSelectElement).value as FormaPago;
    if (!celular || !formaPago) {
        alert("Completá el teléfono y la forma de pago.");
        return;
    }

    const detalles: DetallePedido[] = cart.map((item) => ({
        idProducto: item.id,
        cantidad: item.quantity,
        subtotal: item.price * item.quantity,
    }));
    const subtotal = detalles.reduce((sum, d) => sum + d.subtotal, 0);

    const pedido: Pedido = {
        id: Date.now(),
        fecha: new Date().toISOString().slice(0, 10),
        estado: "PENDIENTE",
        total: subtotal + ENVIO,
        formaPago,
        idUsuario: session.id,
        detalles,
    };

    const pedidos: Pedido[] = JSON.parse(localStorage.getItem("pedidos") || "[]");
    pedidos.push(pedido);
    localStorage.setItem("pedidos", JSON.stringify(pedidos));

    // El carrito se limpia luego de confirmar.
    localStorage.removeItem("cart");
    alert("¡Pedido confirmado! Total: $" + pedido.total.toFixed(2));
    navigate(ORDERS_URL);
}

renderCartProducts(carrito);
