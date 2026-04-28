import type { CartItem } from "../../types/CartItem";
import { getCart } from "../../utils/getCart";
import { protectRoute } from "../../main";

protectRoute();

const carrito: CartItem[] = await getCart();

const containerCartProducts = document.getElementById("cart-products");
const emptyCartMessage = document.getElementById("empty-cart");
const cartTotalElement = document.getElementById("cart-total");

const cleanCartButton = document.getElementById("clear-cart-btn") as HTMLButtonElement;
if (cleanCartButton) {
    cleanCartButton.addEventListener("click", () => {
        localStorage.removeItem("cart");
        renderCartProducts([]);
    });
};


function renderCartProducts(cartItems: CartItem[]): void {
    if (!containerCartProducts || !emptyCartMessage) return;
    containerCartProducts.innerHTML = "";
    if (cartItems.length === 0) {
        emptyCartMessage.style.display = "block";
        if (cartTotalElement) {
        cartTotalElement.textContent = "0.00";
        }
        return;
    }
    emptyCartMessage.style.display = "none";


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
                <td>${item.quantity}</td>
                <td>$${item.price}</td>
                <td>$${(item.price * item.quantity).toFixed(2)}</td>
            </tr>`;
        containerCartProducts.appendChild(tr);
    });


}

renderCartProducts(carrito);