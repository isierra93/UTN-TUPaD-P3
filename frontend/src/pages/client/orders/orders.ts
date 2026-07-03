import type { Pedido } from "../../../types/Pedido";
import type { Product } from "../../../types/Product";
import { getPedidos } from "../../../utils/getPedidos";
import { getProducts } from "../../../utils/getProducts";
import { getSession } from "../../../utils/getSession";
import { protectRoute } from "../../../main";

protectRoute();

const listContainer = document.getElementById("orders-list")!;
const emptyMessage = document.getElementById("orders-empty")!;
const modal = document.getElementById("order-modal")!;
const modalBody = document.getElementById("modal-body")!;

let productMap = new Map<number, Product>();

// Clase de badge segun el estado del pedido.
function badgeClass(estado: string): string {
    switch (estado) {
        case "PENDIENTE": return "badge badge--pendiente";
        case "CONFIRMADO": return "badge badge--confirmado";
        case "TERMINADO": return "badge badge--terminado";
        case "CANCELADO": return "badge badge--cancelado";
        default: return "badge";
    }
}

function nombreProducto(id: number): string {
    return productMap.get(id)?.nombre ?? `Producto #${id}`;
}

function abrirModal(pedido: Pedido): void {
    const subtotal = pedido.detalles.reduce((s, d) => s + d.subtotal, 0);
    const envio = pedido.total - subtotal;

    const filas = pedido.detalles.map((d) =>
        `<tr>
            <td>${nombreProducto(d.idProducto)}</td>
            <td>${d.cantidad}</td>
            <td>$${d.subtotal.toFixed(2)}</td>
         </tr>`).join("");

    modalBody.innerHTML = `
        <h2 class="modal__title">Pedido #${pedido.id}</h2>
        <p>Fecha: <strong>${pedido.fecha}</strong></p>
        <p>Estado: <span class="${badgeClass(pedido.estado)}">${pedido.estado}</span></p>
        <p>Forma de pago: <strong>${pedido.formaPago}</strong></p>
        <table class="table-desktop modal__table">
            <thead>
                <tr><th>Producto</th><th>Cantidad</th><th>Subtotal</th></tr>
            </thead>
            <tbody>${filas}</tbody>
        </table>
        <div class="modal__costs">
            <p><span>Subtotal:</span> <span>$${subtotal.toFixed(2)}</span></p>
            <p><span>Envío:</span> <span>$${envio.toFixed(2)}</span></p>
            <p class="modal__total"><span>Total:</span> <span>$${pedido.total.toFixed(2)}</span></p>
        </div>`;

    modal.style.display = "flex";
}

function cerrarModal(): void {
    modal.style.display = "none";
}

document.getElementById("modal-close")?.addEventListener("click", cerrarModal);
document.getElementById("modal-backdrop")?.addEventListener("click", cerrarModal);

function renderOrders(pedidos: Pedido[]): void {
    if (pedidos.length === 0) {
        emptyMessage.style.display = "block";
        listContainer.style.display = "none";
        return;
    }
    emptyMessage.style.display = "none";
    listContainer.style.display = "grid";
    listContainer.innerHTML = "";

    pedidos.forEach((pedido) => {
        const resumen = pedido.detalles
            .slice(0, 3)
            .map((d) => `${nombreProducto(d.idProducto)} x${d.cantidad}`)
            .join(", ");
        const extra = pedido.detalles.length > 3 ? ` y ${pedido.detalles.length - 3} más` : "";

        const card = document.createElement("article");
        card.classList.add("order-card");
        card.innerHTML = `
            <div class="order-card__header">
                <span class="order-card__number">Pedido #${pedido.id}</span>
                <span class="${badgeClass(pedido.estado)}">${pedido.estado}</span>
            </div>
            <p class="order-card__date">${pedido.fecha}</p>
            <p class="order-card__items">${resumen}${extra}</p>
            <p class="order-card__total">Total: $${pedido.total.toFixed(2)}</p>`;
        card.addEventListener("click", () => abrirModal(pedido));
        listContainer.appendChild(card);
    });
}

const init = async () => {
    const session = getSession();
    if (!session) return;

    try {
        const [pedidos, productos] = await Promise.all([getPedidos(), getProducts()]);
        productMap = new Map(productos.map((p) => [p.id, p]));

        const propios = pedidos
            .filter((p) => p.idUsuario === session.id)
            .sort((a, b) => (a.fecha < b.fecha ? 1 : -1)); // mas recientes primero
        renderOrders(propios);
    } catch (error) {
        console.error("Error cargando pedidos:", error);
    }
};

init();
