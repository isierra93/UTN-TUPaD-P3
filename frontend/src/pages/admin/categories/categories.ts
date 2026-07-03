import type { Category } from "../../../types/Category";
import { getCategories } from "../../../utils/getCategories";
import { protectRoute } from "../../../main";

protectRoute();

// Estado en memoria: las operaciones no se persisten (se pierden al recargar).
let categorias: Category[] = [];

const tbody = document.getElementById("cat-tbody")!;
const modal = document.getElementById("cat-modal")!;
const modalTitle = document.getElementById("cat-modal-title")!;
const form = document.getElementById("cat-form") as HTMLFormElement;
const inputId = document.getElementById("cat-id") as HTMLInputElement;
const inputNombre = document.getElementById("cat-nombre") as HTMLInputElement;
const inputDescripcion = document.getElementById("cat-descripcion") as HTMLInputElement;
const inputImagen = document.getElementById("cat-imagen") as HTMLInputElement;

function abrirModal(categoria?: Category): void {
    if (categoria) {
        modalTitle.textContent = "Editar categoría";
        inputId.value = categoria.id.toString();
        inputNombre.value = categoria.nombre;
        inputDescripcion.value = categoria.descripcion;
        inputImagen.value = categoria.imagen;
    } else {
        modalTitle.textContent = "Nueva categoría";
        form.reset();
        inputId.value = "";
    }
    modal.style.display = "flex";
}

function cerrarModal(): void {
    modal.style.display = "none";
}

function render(): void {
    tbody.innerHTML = "";
    const activas = categorias.filter((c) => !c.eliminado);
    if (activas.length === 0) {
        tbody.innerHTML = `<tr><td colspan="5">No hay categorías cargadas.</td></tr>`;
        return;
    }
    activas.forEach((cat) => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>${cat.id}</td>
            <td><img class="table-desktop__thumb" src="${cat.imagen}" alt="${cat.nombre}"></td>
            <td>${cat.nombre}</td>
            <td>${cat.descripcion}</td>
            <td class="table-desktop__actions">
              <a href="#" data-edit="${cat.id}">Editar</a>
              <a href="#" data-delete="${cat.id}">Eliminar</a>
            </td>`;
        tbody.appendChild(tr);
    });
}

// Delegación de eventos para Editar / Eliminar.
tbody.addEventListener("click", (event) => {
    const target = event.target as HTMLElement;
    const editId = target.dataset.edit;
    const deleteId = target.dataset.delete;

    if (editId) {
        event.preventDefault();
        const cat = categorias.find((c) => c.id === Number(editId));
        if (cat) abrirModal(cat);
    } else if (deleteId) {
        event.preventDefault();
        const cat = categorias.find((c) => c.id === Number(deleteId));
        if (cat && confirm(`¿Eliminar la categoría "${cat.nombre}"?`)) {
            cat.eliminado = true; // baja lógica en memoria
            render();
        }
    }
});

form.addEventListener("submit", (event) => {
    event.preventDefault();
    const nombre = inputNombre.value.trim();
    const descripcion = inputDescripcion.value.trim();
    const imagen = inputImagen.value.trim();
    if (!nombre || !descripcion || !imagen) {
        alert("Completá todos los campos.");
        return;
    }

    if (inputId.value) {
        // Edición
        const cat = categorias.find((c) => c.id === Number(inputId.value));
        if (cat) {
            cat.nombre = nombre;
            cat.descripcion = descripcion;
            cat.imagen = imagen;
        }
    } else {
        // Alta: id nuevo = max + 1
        const nuevoId = categorias.reduce((max, c) => Math.max(max, c.id), 0) + 1;
        categorias.push({ id: nuevoId, nombre, descripcion, imagen, eliminado: false });
    }

    cerrarModal();
    render();
});

document.getElementById("new-cat-btn")?.addEventListener("click", () => abrirModal());
document.getElementById("cat-modal-close")?.addEventListener("click", cerrarModal);
document.getElementById("cat-modal-backdrop")?.addEventListener("click", cerrarModal);

const init = async () => {
    categorias = await getCategories();
    render();
};

init();
