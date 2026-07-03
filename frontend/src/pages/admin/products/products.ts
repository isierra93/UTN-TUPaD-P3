import type { Product } from "../../../types/Product";
import type { Category } from "../../../types/Category";
import { getProducts } from "../../../utils/getProducts";
import { getCategories } from "../../../utils/getCategories";
import { protectRoute } from "../../../main";

protectRoute();

// Estado en memoria: las operaciones no se persisten (se pierden al recargar).
let productos: Product[] = [];
let categorias: Category[] = [];

const tbody = document.getElementById("prod-tbody")!;
const modal = document.getElementById("prod-modal")!;
const modalTitle = document.getElementById("prod-modal-title")!;
const form = document.getElementById("prod-form") as HTMLFormElement;
const inputId = document.getElementById("prod-id") as HTMLInputElement;
const inputNombre = document.getElementById("prod-nombre") as HTMLInputElement;
const inputDescripcion = document.getElementById("prod-descripcion") as HTMLInputElement;
const inputPrecio = document.getElementById("prod-precio") as HTMLInputElement;
const inputStock = document.getElementById("prod-stock") as HTMLInputElement;
const selectCategoria = document.getElementById("prod-categoria") as HTMLSelectElement;
const inputImagen = document.getElementById("prod-imagen") as HTMLInputElement;
const inputDisponible = document.getElementById("prod-disponible") as HTMLInputElement;

function nombreCategoria(id: number): string {
    return categorias.find((c) => c.id === id)?.nombre ?? "Sin categoría";
}

function llenarSelectCategorias(): void {
    selectCategoria.innerHTML = `<option value="" disabled selected>Seleccioná una categoría</option>`;
    categorias
        .filter((c) => !c.eliminado)
        .forEach((c) => {
            const option = document.createElement("option");
            option.value = c.id.toString();
            option.textContent = c.nombre;
            selectCategoria.appendChild(option);
        });
}

function abrirModal(producto?: Product): void {
    if (producto) {
        modalTitle.textContent = "Editar producto";
        inputId.value = producto.id.toString();
        inputNombre.value = producto.nombre;
        inputDescripcion.value = producto.descripcion;
        inputPrecio.value = producto.precio.toString();
        inputStock.value = producto.stock.toString();
        selectCategoria.value = producto.categoriaId.toString();
        inputImagen.value = producto.imagen;
        inputDisponible.checked = producto.disponible;
    } else {
        modalTitle.textContent = "Nuevo producto";
        form.reset();
        inputId.value = "";
        inputDisponible.checked = true;
    }
    modal.style.display = "flex";
}

function cerrarModal(): void {
    modal.style.display = "none";
}

function render(): void {
    tbody.innerHTML = "";
    const activos = productos.filter((p) => !p.eliminado);
    if (activos.length === 0) {
        tbody.innerHTML = `<tr><td colspan="9">No hay productos cargados.</td></tr>`;
        return;
    }
    activos.forEach((prod) => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>${prod.id}</td>
            <td><img class="table-desktop__thumb" src="${prod.imagen}" alt="${prod.nombre}"></td>
            <td>${prod.nombre}</td>
            <td>${prod.descripcion}</td>
            <td>$${prod.precio.toFixed(2)}</td>
            <td>${nombreCategoria(prod.categoriaId)}</td>
            <td>${prod.stock}</td>
            <td>${prod.disponible ? "Disponible" : "No disponible"}</td>
            <td class="table-desktop__actions">
              <a href="#" data-edit="${prod.id}">Editar</a>
              <a href="#" data-delete="${prod.id}">Eliminar</a>
            </td>`;
        tbody.appendChild(tr);
    });
}

tbody.addEventListener("click", (event) => {
    const target = event.target as HTMLElement;
    const editId = target.dataset.edit;
    const deleteId = target.dataset.delete;

    if (editId) {
        event.preventDefault();
        const prod = productos.find((p) => p.id === Number(editId));
        if (prod) abrirModal(prod);
    } else if (deleteId) {
        event.preventDefault();
        const prod = productos.find((p) => p.id === Number(deleteId));
        if (prod && confirm(`¿Eliminar el producto "${prod.nombre}"?`)) {
            prod.eliminado = true; // baja lógica en memoria
            render();
        }
    }
});

form.addEventListener("submit", (event) => {
    event.preventDefault();

    const nombre = inputNombre.value.trim();
    const descripcion = inputDescripcion.value.trim();
    const precio = parseFloat(inputPrecio.value);
    const stock = parseInt(inputStock.value, 10);
    const categoriaId = Number(selectCategoria.value);
    const imagen = inputImagen.value.trim();
    const disponible = inputDisponible.checked;

    // Validaciones: precio > 0, stock >= 0, categoría existente.
    if (!nombre || !descripcion) {
        alert("Nombre y descripción son obligatorios.");
        return;
    }
    if (isNaN(precio) || precio <= 0) {
        alert("El precio debe ser mayor a 0.");
        return;
    }
    if (isNaN(stock) || stock < 0) {
        alert("El stock no puede ser negativo.");
        return;
    }
    if (!categoriaId || !categorias.some((c) => c.id === categoriaId && !c.eliminado)) {
        alert("Seleccioná una categoría válida.");
        return;
    }

    if (inputId.value) {
        const prod = productos.find((p) => p.id === Number(inputId.value));
        if (prod) {
            prod.nombre = nombre;
            prod.descripcion = descripcion;
            prod.precio = precio;
            prod.stock = stock;
            prod.categoriaId = categoriaId;
            prod.imagen = imagen;
            prod.disponible = disponible;
        }
    } else {
        const nuevoId = productos.reduce((max, p) => Math.max(max, p.id), 0) + 1;
        productos.push({
            id: nuevoId,
            nombre,
            descripcion,
            precio,
            stock,
            imagen,
            disponible,
            eliminado: false,
            categoriaId,
        });
    }

    cerrarModal();
    render();
});

document.getElementById("new-prod-btn")?.addEventListener("click", () => abrirModal());
document.getElementById("prod-modal-close")?.addEventListener("click", cerrarModal);
document.getElementById("prod-modal-backdrop")?.addEventListener("click", cerrarModal);

const init = async () => {
    [productos, categorias] = await Promise.all([getProducts(), getCategories()]);
    llenarSelectCategorias();
    render();
};

init();
