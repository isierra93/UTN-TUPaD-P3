import type { User } from '../../../types/User';
import { navigate } from '../../../utils/navigate';

const registerForm = document.querySelector('#register-form') as HTMLFormElement;

registerForm?.addEventListener('submit', async (event) => {
    event.preventDefault();
    const formElement = event.currentTarget as HTMLFormElement;
    const formData = new FormData(formElement);
    //Todos los usuarios registrados tendrán el rol de "client" por defecto
    const user = {
        email: formData.get('email') as string,
        password: formData.get('password') as string,
        role : "client" as const
    }

    let isValid = true;
    const users: User[] = JSON.parse(localStorage.getItem('users') || '[]');
    users.forEach(u => {
        if (u.email == user.email){
            alert('El email ya está registrado. Por favor, ingresa con ese email o usa otro.');
            isValid = false;
            return;
        }
    });
    if (isValid) {
        users.push(user);
        localStorage.setItem('users', JSON.stringify(users));
        alert('Registro exitoso! Ahora puedes ingresar con tu email y contraseña.');
        formElement.reset();
        navigate("../login/login.html");
    }
});