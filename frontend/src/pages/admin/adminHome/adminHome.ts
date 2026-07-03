import type { EstadoPedido } from "../../../types/Pedido";
import { getCategories } from "../../../utils/getCategories";
import { getProducts } from "../../../utils/getProducts";
import { getPedidos } from "../../../utils/getPedidos";
import { protectRoute } from "../../../main";

protectRoute();

const ESTADOS: EstadoPedido[] = ["PENDIENTE", "CONFIRMADO", "TERMINADO", "CANCELADO"];

function setText(id: string, value: string | number): void {
    const el = document.getElementById(id);
    if (el) el.textContent = value.toString();
}

const init = async () => {
    try {
        const [categorias, productos, pedidos] = await Promise.all([
            getCategories(),
            getProducts(),
            getPedidos(),
        ]);

        const disponibles = productos.filter((p) => p.disponible).length;
        const noDisponibles = productos.length - disponibles;

        // Tarjetas
        setText("stat-categorias", categorias.length);
        setText("stat-productos", productos.length);
        setText("stat-pedidos", pedidos.length);
        setText("stat-disponibles", disponibles);

        // Resumen productos
        setText("resumen-disponibles", disponibles);
        setText("resumen-no-disponibles", noDisponibles);

        // Resumen pedidos por estado
        const lista = document.getElementById("resumen-estados");
        if (lista) {
            lista.innerHTML = "";
            ESTADOS.forEach((estado) => {
                const count = pedidos.filter((p) => p.estado === estado).length;
                const li = document.createElement("li");
                li.innerHTML = `<span class="badge badge--${estado.toLowerCase()}">${estado}</span> <strong>${count}</strong>`;
                lista.appendChild(li);
            });
        }
    } catch (error) {
        console.error("Error cargando el dashboard:", error);
    }
};

init();
