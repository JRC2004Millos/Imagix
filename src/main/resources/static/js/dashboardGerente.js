// Obtener el gráfico y sus configuraciones
let myChart;

// Función para crear o actualizar el gráfico
function createOrUpdateChart(groupedIdeas) {
    // Los nombres de los meses en español
    const months = [
        'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
        'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'
    ];

    // Extraer los meses y las cantidades de ideas
    const meses = groupedIdeas.map(item => months[item.mes - 1]); // Restamos 1 porque los meses empiezan en 0
    const cantidadIdeas = groupedIdeas.map(item => item.cantidadIdeas);

    const chartData = {
        labels: meses,  // Usamos los nombres de los meses en lugar de los números
        datasets: [{
            label: 'Ideas Propuestas',
            data: cantidadIdeas,  // Cantidad de ideas por mes
            backgroundColor: 'rgba(75, 192, 192, 0.2)',
            borderColor: 'rgba(75, 192, 192, 1)',
            borderWidth: 1
        }]
    };

    const config = {
        type: 'bar',  // Tipo de gráfico (barra)
        data: chartData,
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: 'top',
                },
                tooltip: {
                    enabled: true
                }
            }
        },
    };

    // Si ya existe un gráfico, lo destruimos y creamos uno nuevo
    const ctx = document.getElementById('myChart');
    if (window.chartInstance) {
        window.chartInstance.destroy();
    }

    window.chartInstance = new Chart(ctx, config);
}

// Manejar el envío del formulario
document.getElementById('dateForm').addEventListener('submit', function (event) {
    event.preventDefault(); // Evitar que el formulario se envíe de la manera tradicional

    const year = document.querySelector('[name="year"]').value;

    // Validar si el año es correcto
    if (!year || isNaN(year) || year.length !== 4) {
        alert('Por favor ingrese un año válido.');
        return;
    }

    // Hacer la solicitud GET al servidor
    fetch(`/gerente/dashboardF?year=${year}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Error al obtener los datos');
            }
            return response.json();
        })
        .then(data => {
            // Verificar si los datos son válidos antes de crear o actualizar el gráfico
            if (data && Array.isArray(data)) {
                createOrUpdateChart(data);  // Usar datos solo si son válidos
            } else {
                console.error('Formato de datos no válido:', data);
            }
        })
        .catch(error => {
            console.error('Error al obtener los datos:', error);
        });
});
