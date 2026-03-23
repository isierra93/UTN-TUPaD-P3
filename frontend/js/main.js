import { listOfCategories } from "./data.js";
import { listOfproducts } from "./data.js";

const containerCategories = document.getElementById("list-categories");

const renderCategories = () => {
  if (!containerCategories) return;
  containerCategories.innerHTML = "";

  listOfCategories.forEach(category => {
    const li = document.createElement("li");
    li.innerHTML = `<a href="#" class="categories__link">${category}</a>`;
    containerCategories.appendChild(li);
  });
};

renderCategories();

const containerProducts = document.getElementById("products-container");

const renderProducts = () => {
  if (!containerProducts) return;
  containerProducts.innerHTML = "";

  listOfproducts.forEach(product => {
    const card = document.createElement("article");
    card.classList.add("product-card");
    card.innerHTML = `
      <img class="product-card__image" src="${product.img}" alt="${product.descripcion}">
      <div class="product-card__body">
        <span class="product-card__category">${product.categoria}</span>
        <h3 class="product-card__title">${product.nombre}</h3>
        <p class="product-card__desc">${product.descripcion}</p>
        <div class="product-card__footer">
          <span class="product-card__price">${product.precio}</span>
          <button class="btn" type="button">Agregar</button>
        </div>
      </div>
    `;
    containerProducts.appendChild(card);
  });
};

renderProducts();

const containerAdminCategories = document.getElementById("p-cat");

const renderAdminCategories = () => {
  if (!containerAdminCategories) return;

  listOfCategories.forEach(category => {
    const option = document.createElement("option");
    option.value = category;
    option.textContent = category;
    containerAdminCategories.appendChild(option);
  });
};

renderAdminCategories();
