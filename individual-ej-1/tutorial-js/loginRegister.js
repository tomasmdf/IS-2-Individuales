const formTitle = document.getElementById('formTitle');
const loginForm = document.getElementById('loginForm');
const toggleButton = document.getElementById    ('toggleButton');
const registerForm = document.getElementById('registerForm');

registerForm.style.display = 'none';

toggleButton.addEventListener('click', () => {
    if (registerForm.style.display === 'none') {
        registerForm.style.display = 'block';
        loginForm.style.display = 'none';
        formTitle.textContent = 'Registro de cuenta';
        toggleButton.textContent = 'Iniciar sesión';
    } else {
        registerForm.style.display = 'none';
        loginForm.style.display = 'block';
        formTitle.textContent = 'Inicio de sesión';
        toggleButton.textContent = 'Crear una cuenta';
    }   
});