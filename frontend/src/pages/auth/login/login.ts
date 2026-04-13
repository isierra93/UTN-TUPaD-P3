import type { User } from "../../../types/User";
import { navigate } from "../../../utils/navigate";

console.log("Login");

const loginForm = document.querySelector<HTMLFormElement>("#login-form")!;

loginForm?.addEventListener("submit", (event) => {
    event.preventDefault();

    const formElement = event.currentTarget as HTMLFormElement;
    const formData = new FormData(formElement);

    const email = formData.get("email") as string;
    const password = formData.get("password") as string;

    const users: User[] = JSON.parse(localStorage.getItem('users') || '[]');
    const user = users.find(u => u.email === email && u.password === password);

    if (user) {
        formElement.reset();
        alert(`Bienvenido, ${user.email}!`);
        localStorage.setItem('userData', JSON.stringify(user));
        navigate("../home/home.html");
    } else {
        formElement.reset();
        alert("Email o contraseña incorrectos. Por favor, intenta de nuevo.");
    }

});