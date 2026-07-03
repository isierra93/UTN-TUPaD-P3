import { protectRoute } from "../../../main";
import type { User } from "../../../types/User";
import { getUsuarios } from "../../../utils/getUsuarios";
import { navigate } from "../../../utils/navigate";
import { HOME_URL } from "../../../utils/relativeUrls";

protectRoute();

const registerForm = document.querySelector("#register-form") as HTMLFormElement;

registerForm?.addEventListener("submit", async (event) => {
    event.preventDefault();
    const formElement = event.currentTarget as HTMLFormElement;
    const formData = new FormData(formElement);

    const nombre = ((formData.get("nombre") as string) ?? "").trim();
    const mail = (formData.get("email") as string).trim();
    const password = formData.get("password") as string;

    // Validaciones: email con formato valido y contrasena de al menos 6 caracteres.
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(mail)) {
        alert("Ingresa un email valido.");
        return;
    }
    if (password.length < 6) {
        alert("La contrasena debe tener al menos 6 caracteres.");
        return;
    }

    const desdeJson = await getUsuarios();
    const desdeLocal: User[] = JSON.parse(localStorage.getItem("users") || "[]");
    if ([...desdeJson, ...desdeLocal].some((u) => u.mail === mail)) {
        alert("El email ya esta registrado. Ingresa con ese email o usa otro.");
        return;
    }

    // Solo se registran clientes (rol USUARIO). Se guarda en el estado local.
    const nuevo: User = {
        id: Date.now(),
        nombre: nombre || mail.split("@")[0],
        apellido: "",
        mail,
        celular: "",
        password,
        rol: "USUARIO",
    };
    desdeLocal.push(nuevo);
    localStorage.setItem("users", JSON.stringify(desdeLocal));

    // Auto-login tras el registro (sesion sin la contrasena).
    const session: User = { ...nuevo };
    delete session.password;
    localStorage.setItem("userData", JSON.stringify(session));

    alert("Registro exitoso! Bienvenido, " + session.nombre + ".");
    formElement.reset();
    navigate(HOME_URL);
});
