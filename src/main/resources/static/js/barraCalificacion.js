// Obtener elementos del DOM
const slider = document.getElementById('rating-slider');
const ratingValue = document.getElementById('rating-value');
const calificacionActual = document.getElementById('calificacion-actual');
const estadoSpinner = document.getElementById('estado-spinner');
const estadoActualH2 = document.getElementById('estado-actual'); // H2 que muestra el estado actual
const btnAceptar = document.getElementById('btn-aceptar'); // Botón de Aceptar

// Obtener el ID de la idea desde la URL
const ideaId = window.location.pathname.split('/').pop();

// Escuchar el evento de movimiento de la barra deslizante
slider.addEventListener('input', () => {
    const valor = slider.value;
    ratingValue.textContent = valor; // Mostrar la nueva calificación en tiempo real

    // Enviar la nueva calificación al backend
    fetch(`http://localhost:8100/promotor/idea/${ideaId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ calificacion: valor }),
    })
    .then(response => {
        if (response.ok) {
            // Actualizar la calificación actual en la página
            calificacionActual.textContent = valor;
            console.log('Calificación actualizada correctamente');
        } else {
            console.error('Error al actualizar la calificación');
        }
    })
    .catch(error => console.error('Error en la petición:', error));
});

// Escuchar cambios en el Spinner de estado
estadoSpinner.addEventListener('change', () => {
    const nuevoEstado = estadoSpinner.value;

    // Enviar el nuevo estado al backend al cambiar el spinner (opcional)
    fetch(`http://localhost:8100/promotor/idea/${ideaId}/estado`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ estado: nuevoEstado }),
    })
    .then(response => {
        if (response.ok) {
            console.log('Estado cambiado desde el Spinner');
        } else {
            console.error('Error al cambiar el estado');
        }
    })
    .catch(error => console.error('Error en la petición:', error));
});

// Escuchar el evento de clic en el botón Aceptar
btnAceptar.addEventListener('click', () => {
    const nuevoEstado = estadoSpinner.value;

    // Enviar el nuevo estado al backend
    fetch(`http://localhost:8100/promotor/idea/${ideaId}/estado`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ estado: nuevoEstado }),
    })
    .then(response => {
        if (response.ok) {
            // Actualizar el H2 con el nuevo estado en tiempo real
            estadoActualH2.textContent = nuevoEstado;
            console.log('Estado actualizado correctamente y reflejado en la página');
        } else {
            console.error('Error al actualizar el estado');
        }
    })
    .catch(error => console.error('Error en la petición:', error));
});
