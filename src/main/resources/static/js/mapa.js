var map = L.map('map').setView([7.7110, -74.0715], 6); // Centrado en Colombia, Bogotá

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
}).addTo(map);

// Añadir pines en las ciudades que desees
var locations = [
    { name: "Bucaramanga", lat: 7.119571796526307, lon: -73.1235374859103 }, 
    { name: "Bogotá", lat: 4.7050802618331575, lon: -74.08996820689602 },
    { name: "Monteria", lat: 8.750705938132953, lon: -75.88025424632917 },
    { name: "Manizales", lat: 5.0620275503151335, lon: -75.50132498526722 },
    { name: "Armenia", lat:4.535084039250334, lon:-75.67634411125702},
    { name: "Pereira", lat:4.808203850215178, lon:-75.69006877518336},
    { name: "Barranquilla", lat: 11.00008828036706, lon: -74.8087110822629 }, 
    { name: "Cali", lat: 3.451265371662754, lon: -76.53253387388106 },
    { name: "Medellin", lat: 6.247785340253921, lon: -75.56679280648493 }, 
];

// Crear un marcador por cada ciudad
locations.forEach(function(location) {
    var marker = L.marker([location.lat, location.lon]).addTo(map)
        .bindPopup('<b>' + location.name + '</b>');  // Ventana emergente con el nombre de la ciudad
});