import type { CartItem } from "../../types/CartItem";
import { getCart } from "../../utils/getCart";
import { protectRoute } from "../../main";

protectRoute();

const carrito: CartItem[] = await getCart();

const containerCartProducts = document.getElementById("cart-products");
const emptyCartMessage = document.getElementById("empty-cart");
const cartTotalElement = document.getElementById("cart-total");
const tableProducts = document.getElementById("item-cart");

const cleanCartButton = document.getElementById("clear-cart-btn") as HTMLButtonElement;
if (cleanCartButton) {
    cleanCartButton.addEventListener("click", () => {
        localStorage.removeItem("cart");
        renderCartProducts([]);
    });
};

function renderCartProducts(cartItems: CartItem[]): void {
    if (!containerCartProducts || !emptyCartMessage || !tableProducts) return;
    containerCartProducts.innerHTML = "";

    if (cartItems.length === 0) {
        emptyCartMessage.style.display = "block";
        tableProducts.style.display = "none";
        cleanCartButton.style.display = "none";
        if (cartTotalElement) {
        cartTotalElement.textContent = "0.00";
        }
        return;
    }
    emptyCartMessage.style.display = "none";
    tableProducts.style.display = "block";
    cleanCartButton.style.display = "block";
    if (cartTotalElement) {
        const total = cartItems.reduce((sum, item) => sum + item.price * item.quantity, 0);
        cartTotalElement.textContent = total.toFixed(2);
    }

    cartItems.forEach(item => {
        const tr = document.createElement("tr");
        tr.innerHTML =
            `<tr>
              <td>${item.name}</td>  
                <td><img class="table-desktop__thumb" src="${item.imageUrl}" alt="${item.name}"></td>
                <td>${item.quantity} 
                <td>
                    <div class="cart-item-actions">
                    <button class="btn cart-action-btn" type="button" id="increase-qty-${item.id}"> + </button> 
                    <button class="btn cart-action-btn" type="button" id="decrease-qty-${item.id}"> - </button>
                    </div>
                </td>
                <td>$${item.price}</td>
                <td>$${(item.price * item.quantity).toFixed(2)}</td>
            </tr>`;
        containerCartProducts.appendChild(tr);
    });

    // Agregar event listeners para los botones de aumentar/disminuir cantidad
    cartItems.forEach(item => {
        const increaseBtn = document.getElementById(`increase-qty-${item.id}`);
        const decreaseBtn = document.getElementById(`decrease-qty-${item.id}`);
        if (increaseBtn) {
            increaseBtn.addEventListener("click", () => updateCartItemQuantity(item.id, item.quantity + 1));
        }
        if (decreaseBtn) {
            decreaseBtn.addEventListener("click", () => updateCartItemQuantity(item.id, item.quantity - 1));
        }
    });
}

function updateCartItemQuantity(productId: number, newQuantity: number): void {
    const cart: CartItem[] = JSON.parse(localStorage.getItem('cart') || '[]');
    const itemIndex = cart.findIndex(item => item.id === productId);
    if (itemIndex !== -1) {
        if (newQuantity <= 0) {
            cart.splice(itemIndex, 1);
        } else {
            cart[itemIndex].quantity = newQuantity;
        }   
        localStorage.setItem('cart', JSON.stringify(cart));
        renderCartProducts(cart);
    } else {
        console.warn(`Producto con ID ${productId} no encontrado en el carrito.`);
    }
}

renderCartProducts(carrito);