const registerForm = document.querySelector('#register-form') as HTMLFormElement;

registerForm?.addEventListener('submit', async (event) => {
    event.preventDefault();
    const formData = new FormData(registerForm);
    const datos = {
        email: formData.get('email') as string,
        password: formData.get('password') as string,
    }
    console.log(datos);
    
});