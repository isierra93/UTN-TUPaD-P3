import { protectRoute } from "../../../main";
import type { User } from "../../../types/User";
import { getUsuarios } from "../../../utils/getUsuarios";
import { navigate } from "../../../utils/navigate";
import { HOME_URL, ADMIN_URL } from "../../../utils/relativeUrls";

protectRoute();

const loginForm = document.querySelector<HTMLFormElement>("#login-form")!;

loginForm?.addEventListener("submit", async (event) => {
    event.preventDefault();

    const formElement = event.currentTarget as HTMLFormElement;
    const formData = new FormData(formElement);
    const mail = (formData.get("email") as string).trim();
    const password = formData.get("password") as string;

    // Credenciales validas: las del JSON mas las registradas en localStorage.
    const desdeJson = await getUsuarios();
    const desdeLocal: User[] = JSON.parse(localStorage.getItem("users") || "[]");
    const usuarios = [...desdeJson, ...desdeLocal];

    const user = usuarios.find((u) => u.mail === mail && u.password === password);

    if (user) {
        // La sesion se guarda sin la contrasena.
        const session: User = { ...user };
        delete session.password;
        localStorage.setItem("userData", JSON.stringify(session));
        formElement.reset();
        alert(`Bienvenido, ${user.nombre}!`);
        navigate(user.rol === "ADMIN" ? ADMIN_URL : HOME_URL);
    } else {
        formElement.reset();
        alert("Email o contrasena incorrectos. Por favor, intenta de nuevo.");
    }
});
