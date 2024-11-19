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
 document.getElementById('dateForm').addEventListener('submit', function(event) {
     event.preventDefault();

     const startDate = document.getElementById('startDate').value;
     const endDate = document.getElementById('endDate').value;

     // Hacer la llamada AJAX para obtener los datos filtrados
     fetch(`/gerente/dashboardF?startDate=${startDate}&endDate=${endDate}`, {
         method: 'GET',
         headers: {
             'Accept': 'application/json',  // Asegurarse de que la respuesta sea JSON
         },
     })
     .then(response => response.json())  // Respuesta en formato JSON
     .then(data => {
         // Procesar la respuesta JSON y actualizar la gráfica
         createOrUpdateChart(data.groupedIdeas);
         console.log(data.groupedIdeas);
     })
     .catch(error => console.error('Error al obtener los datos:', error));
 });