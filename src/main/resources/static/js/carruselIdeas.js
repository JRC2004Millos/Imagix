const cardImg = document.getElementById('card-img');
const textContent = document.getElementById('text-content');

// Array de objetos con diferentes películas e imágenes
const movies = [
    {
        title: 'Stranger Things',
        img: '/images/idea.png',
        text: 'Stranger Things te lleva a los años 80 con misterio y aventuras sobrenaturales.'
    },
    {
        title: 'Breaking Bad',
        img: '/images/usuario.png',
        text: 'Breaking Bad sigue la transformación de un profesor en uno de los criminales más buscados.'
    },
    {
        title: 'The Crown',
        img: '/images/usuario.png',
        text: 'The Crown narra la vida de la Reina Isabel II y su impacto en el mundo moderno.'
    }
];

let currentMovieIndex = 0;

// Función para cambiar la imagen y el texto al hacer clic
cardImg.addEventListener('click', () => {
    currentMovieIndex = (currentMovieIndex + 1) % movies.length; // Cambia al siguiente índice
    const movie = movies[currentMovieIndex]; // Obtiene la película actual

    // Cambiar la imagen y el texto
    cardImg.src = movie.img;
    textContent.textContent = movie.text;
});
