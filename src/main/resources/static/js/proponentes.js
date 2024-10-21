function agregarSelect() {
  const container = document.getElementById('proponentes-container');

  // Crear un nuevo div para envolver el select
  const wrapper = document.createElement('div');
  wrapper.className = 'proponente-select-wrapper';

  // Crear el nuevo select
  const select = document.createElement('select');
  select.className = 'proponente-select';
  select.name = 'proponentesIds';

  // Obtener los proponentes del primer select para clonarlos
  const opciones = document.querySelector('.proponente-select').innerHTML;
  select.innerHTML = opciones;

  // Añadir el select al contenedor
  wrapper.appendChild(select);
  container.appendChild(wrapper);
}
