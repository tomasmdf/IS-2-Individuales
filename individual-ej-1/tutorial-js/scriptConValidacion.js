document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("myForm");
    const mensaje = document.getElementById("mensaje");

    mensaje.style.display = "none"; // Oculta el mensaje inicialmente

    form.addEventListener("submit", function (event) {
        event.preventDefault(); // Evita el envío del formulario

        console.log("Formulario enviado"); // Muestra un mensaje en la consola

        const name = document.getElementById("name").value.trim();
        const email = document.getElementById("email").value.trim();
        const mensajeInput = document.getElementById("mensaje");

        if (name === "" || email === "" || mensajeInput === "") {
            mensajeInput.textContent = "Error: Por favor, complete todos los campos.";
            mensajeInput.style.display = "block"; // Muestra el mensaje de error
            mensajeInput.style.color = "red"; // Cambia el color del mensaje a rojo
            mensajeInput.style.backgroundColor = "rgba(255, 0, 0, 0.1)"; // Cambia el fondo del mensaje a rojo claro
        } else {
            mensajeInput.textContent = "Formulario enviado correctamente.";
            mensajeInput.style.display = "block"; // Muestra el mensaje de éxito
            mensajeInput.style.color = "green"; // Cambia el color del mensaje a verde
            mensajeInput.style.backgroundColor = "rgba(0, 128, 0, 0.1)"; // Cambia el fondo del mensaje a verde claro
        }
    });

});