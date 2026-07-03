import type { Pedido, EstadoPedido } from "../../../types/Pedido";
import type { Product } from "../../../types/Product";
import type { User } from "../../../types/User";
import { getPedidos } from "../../../utils/getPedidos";
import { getProducts } from "../../../utils/getProducts";
import { getUsuarios } from "../../../utils/getUsuarios";
import { protectRoute } from "../../../main";

protectRoute();

const ESTADOS: EstadoPedido[] = ["PENDIENTE", "CONFIRMADO", "TERMINADO", "CANCELADO"];

// Estado en memoria: el cambio de estado no se persiste (se pierde al recargar).
let pedidos: Pedido[] = [];
let productMap = new Map<number, Product>();
let userMap = new Map<number, User>();

const listContainer = document.getElementById("admin-orders-list")!;
const emptyMessage = document.getElementById("admin-orders-empty")!;
const filterSelect = document.getElementById("estado-filter") as HTMLSelectElement;
const modal = document.getElementById("order-modal")!;
const modalBody = document.getElementById("modal-body")!;

function badgeClass(estado: string): string {
    return `badge badge--${estado.toLowerCase()}`;
}

function nombreProducto(id: number): string {
    return productMap.get(id)?.nombre ?? `Producto #${id}`;
}

function nombreCliente(idUsuario: number): string {
    const u = userMap.get(idUsuario);
    return u ? `${u.nombre} ${u.apellido}`.trim() : `Usuario #${idUsuario}`;
}

function cerrarModal(): void {
    modal.style.display = "none";
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

    const opciones = ESTADOS.map((e) =>
        `<option value="${e}" ${e === pedido.estado ? "selected" : ""}>${e}</option>`).join("");

    modalBody.innerHTML = `
        <h2 class="modal__title">Pedido #${pedido.id}</h2>
        <p>Cliente: <strong>${nombreCliente(pedido.idUsuario)}</strong></p>
        <p>Fecha: <strong>${pedido.fecha}</strong></p>
        <p>Estado actual: <span class="${badgeClass(pedido.estado)}">${pedido.estado}</span></p>
        <p>Forma de pago: <strong>${pedido.formaPago}</strong></p>
        <table class="table-desktop modal__table">
            <thead><tr><th>Producto</th><th>Cantidad</th><th>Subtotal</th></tr></thead>
            <tbody>${filas}</tbody>
        </table>
        <div class="modal__costs">
            <p><span>Subtotal:</span> <span>$${subtotal.toFixed(2)}</span></p>
            <p><span>Envío:</span> <span>$${envio.toFixed(2)}</span></p>
            <p class="modal__total"><span>Total:</span> <span>$${pedido.total.toFixed(2)}</span></p>
        </div>
        <div class="form-field">
            <label class="form-field__label" for="estado-select">Cambiar estado</label>
            <select class="form-field__select" id="estado-select">${opciones}</select>
        </div>
        <div class="form-actions">
            <button class="btn btn--full btn--primary" id="guardar-estado">Guardar estado</button>
        </div>`;

    document.getElementById("guardar-estado")?.addEventListener("click", () => {
        const nuevo = (document.getElementById("estado-select") as HTMLSelectElement).value as EstadoPedido;
        pedido.estado = nuevo; // cambio en memoria
        cerrarModal();
        render();
    });

    modal.style.display = "flex";
}

document.getElementById("modal-close")?.addEventListener("click", cerrarModal);
document.getElementById("modal-backdrop")?.addEventListener("click", cerrarModal);
filterSelect.addEventListener("change", render);

function render(): void {
    const filtro = filterSelect.value;
    const visibles = pedidos
        .filter((p) => !filtro || p.estado === filtro)
        .sort((a, b) => (a.fecha < b.fecha ? 1 : -1)); // más recientes primero

    if (visibles.length === 0) {
        emptyMessage.style.display = "block";
        listContainer.style.display = "none";
        return;
    }
    emptyMessage.style.display = "none";
    listContainer.style.display = "grid";
    listContainer.innerHTML = "";

    visibles.forEach((pedido) => {
        const cantidad = pedido.detalles.reduce((s, d) => s + d.cantidad, 0);
        const card = document.createElement("article");
        card.classList.add("order-card");
        card.innerHTML = `
            <div class="order-card__header">
                <span class="order-card__number">Pedido #${pedido.id}</span>
                <span class="${badgeClass(pedido.estado)}">${pedido.estado}</span>
            </div>
            <p class="order-card__date">${nombreCliente(pedido.idUsuario)} — ${pedido.fecha}</p>
            <p class="order-card__items">${cantidad} producto(s)</p>
            <p class="order-card__total">Total: $${pedido.total.toFixed(2)}</p>`;
        card.addEventListener("click", () => abrirModal(pedido));
        listContainer.appendChild(card);
    });
}

const init = async () => {
    try {
        const [ped, productos, usuarios] = await Promise.all([
            getPedidos(),
            getProducts(),
            getUsuarios(),
        ]);
        pedidos = ped;
        productMap = new Map(productos.map((p) => [p.id, p]));
        userMap = new Map(usuarios.map((u) => [u.id, u]));
        render();
    } catch (error) {
        console.error("Error cargando pedidos:", error);
    }
};

init();
