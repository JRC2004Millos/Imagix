package com.example.demo.model;

import java.io.InputStream;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Controller;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.example.demo.repository.*;
import jakarta.transaction.Transactional;

@Controller
@Transactional
public class DataBaseInit implements ApplicationRunner {

    @Autowired
    private CargoRepository cargoRepository;
    @Autowired
    private IdeaRepository ideaRepository;
    @Autowired
    private AreaRepository areaRepository;
    @Autowired
    private GerenciaRepository gerenciaRepository;
    @Autowired
    private RegionalRepository regionalRepository;
    @Autowired
    private PromotorRepository promotorRepository;
    @Autowired
    private ProponenteRepository proponenteRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private GerenteRepository gerenteRepository;

    // Contador para generar claves únicas
    private final AtomicInteger contadorClave = new AtomicInteger(123); // Inicia en 123

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Agregar admin
        adminRepository.save(new Admin("admin", "admin"));

        // Promotores
        // Ruta al archivo Excel
        InputStream inputStream = getClass().getResourceAsStream("/static/excel/Base promotores 2024.xlsx");

        // Leer el archivo Excel
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0); // Leer la primera hoja

        // Iterar solo sobre las filas con contenido
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);

            if (row == null || isRowEmpty(row)) {
                System.out.println("Fila vacía en índice: " + i);
                continue; // Saltar filas vacías
            }

            try {
                // Leer los valores de las celdas
                String nivel = getCellValue(row, 0);
                String nombre = getCellValue(row, 1);
                String gerenciaNombre = getCellValue(row, 2);
                String regionalNombre = getCellValue(row, 3);
                String cargoNombre = getCellValue(row, 4);
                String correo = getCellValue(row, 5);

                // Buscar o crear entidades sin duplicados
                Gerencia gerencia = findOrCreateGerencia(gerenciaNombre);
                Regional regional = findOrCreateRegional(regionalNombre);
                Cargo cargo = findOrCreateCargo(cargoNombre);

                // Crear y guardar promotor con clave única
                Promotor promotor = new Promotor();
                promotor.setNivel(nivel);
                promotor.setName(nombre);
                promotor.setGerencia(gerencia);
                promotor.setRegional(regional);
                promotor.setCargo(cargo);
                promotor.setEmail(correo);
                promotor.setClave(generarClave()); // Asignar clave única

                promotorRepository.save(promotor);
                System.out.println("Promotor guardado: " + nombre + " con clave: " + promotor.getClave());

            } catch (Exception e) {
                System.err.println("Error al procesar la fila " + i + ": " + e.getMessage());
            }
        }

        // Cerrar recursos
        workbook.close();
        inputStream.close();

        // Proponentes
        // Ruta al archivo Excel
        inputStream = getClass().getResourceAsStream("/static/excel/Informe_Ideas_2020- Junio 2021.xlsx");

        // Leer el archivo Excel
        workbook = new XSSFWorkbook(inputStream);
        sheet = workbook.getSheetAt(1); // Leer la segunda hoja (índice 1)

        // Iterar sobre las filas, comenzando desde la segunda (índice 1)
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);

            if (row == null || isRowEmpty(row)) {
                System.out.println("Fila vacía en índice: " + i);
                continue; // Saltar filas vacías
            }

            try {
                // Extraer valores de las celdas
                String cedula = getCellValue(row, 1);
                String nombre = getCellValue(row, 2);
                String correo = getCellValue(row, 3);
                String ciudad = getCellValue(row, 4);
                String gerenciaNom = getCellValue(row, 5);
                String areaNom = getCellValue(row, 6);
                String cargoNom = getCellValue(row, 8);
                String telefono = getCellValue(row, 9);
                String puntosAsignados = getCellValue(row, 10);
                String puntosRedimidos = getCellValue(row, 11);
                String puntosDisponibles = getCellValue(row, 12);

                // Buscar o crear entidades sin duplicados
                Cargo cargo = findOrCreateCargo(cargoNom);
                Area area = findOrCreateArea(areaNom);
                Gerencia gerencia = findOrCreateGerencia(gerenciaNom);

                // Crear y guardar proponente con clave única
                Proponente proponente = new Proponente();
                proponente.setCedula(Integer.valueOf(cedula));
                proponente.setNombre(nombre);
                proponente.setEmail(correo);
                proponente.setCiudad(ciudad);
                proponente.setGerencia(gerencia);
                proponente.setArea(area);
                proponente.setCargo(cargo);
                proponente.setTelefono(telefono);
                proponente.setClave(generarClave());
                proponente.setPuntosAsignados(convertirStringAInt(puntosAsignados));
                proponente.setPuntosRedimidos(convertirStringAInt(puntosRedimidos));
                proponente.setPuntosDisponibles(convertirStringAInt(puntosDisponibles));

                Proponente existente = proponenteRepository.findByNombre(nombre);
                if (existente != null) {
                    System.out.println("Proponente duplicado: " + nombre);
                } else {
                    proponenteRepository.save(proponente);
                    System.out.println("Proponente guardado: " + nombre);
                }
            } catch (Exception e) {
                System.err.println("Error al procesar la fila " + i + ": " + e.getMessage());
            }
        }

        // Cerrar recursos
        workbook.close();
        inputStream.close();

        // Ideas
        // Ruta al archivo Excel
        inputStream = getClass().getResourceAsStream("/static/excel/Informe_Ideas_2020- Junio 2021.xlsx");

        // Leer el archivo Excel
        workbook = new XSSFWorkbook(inputStream);
        sheet = workbook.getSheetAt(0); // Leer la primera hoja (índice 0)

        // Iterar sobre las filas, comenzando desde la segunda (índice 1)
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);

            if (row == null || isRowEmpty(row)) {
                System.out.println("Fila vacía en índice: " + i);
                continue; // Saltar filas vacías
            }

            try {
                // Extraer valores de las celdas
                String estado = getCellValue(row, 1);
                String responsable = getCellValue(row, 2);
                String personaRegistro = getCellValue(row, 3);
                String fechaCreacion = getCellValue(row, 4);
                String fechaAprobacion = getCellValue(row, 5);
                String gerenciaNom = getCellValue(row, 6);
                String nombreIdea = getCellValue(row, 9);
                String situacionDetectada = getCellValue(row, 10);
                String descripcion = getCellValue(row, 11);
                String estadoImplementada = getCellValue(row, 15);
                String calificacion = getCellValue(row, 25);
                String estadoCalificacion = getCellValue(row, 26);
                String comentario = getCellValue(row, 27);

                Gerencia gerencia = findOrCreateGerencia(gerenciaNom);

                if (ideaRepository.existsByNombreIdeaAndGerencia(nombreIdea, gerencia)) {
                    System.out.println("Idea repetida encontrada en índice: " + i + ", Nombre: " + nombreIdea);
                    continue; // Saltar esta fila
                }

                // Crear y guardar una nueva idea
                Idea idea = new Idea();
                idea.setEstado(estado);
                idea.setResponsable(responsable);

                // Proponentes
                List<Proponente> proponentes = new ArrayList<>();
                proponentes.add(findOrCreateProponente(personaRegistro));
                idea.setProponentes(proponentes);

                // Validación y conversión segura de fechas
                idea.setFechaCreacion(convertirStringAFecha(fechaCreacion));
                idea.setFechaAprobacion(convertirStringAFecha(fechaAprobacion));

                idea.setGerencia(findOrCreateGerencia(gerenciaNom));
                idea.setNombreIdea(truncarDescripcion(nombreIdea));
                idea.setSituacionDetectada(truncarDescripcion(situacionDetectada));
                idea.setDescripcion(truncarDescripcion(descripcion));

                // Convertir estado implementada a boolean
                idea.setEstadoImplementada(estadoImplementada.equalsIgnoreCase("Si"));

                // Conversión segura de calificación a float
                idea.setCalificacion(convertirStringAFloat(calificacion));

                idea.setEstadoCalificacion(truncarDescripcion(estadoCalificacion));
                idea.setComentario(truncarDescripcion(comentario));

                // Guardar la idea en el repositorio
                ideaRepository.save(idea);

            } catch (Exception e) {
                System.err.println("Error al procesar la fila " + i + ": " + e.getMessage());
            }
        }

        // Cerrar recursos
        workbook.close();
        inputStream.close();

        // Agregar gerentes
        gerenteRepository.save(new Gerente("Fernanda", "fernanda@cremhelado.com.co", "123", cargoRepository.findById(3L).get(),
                regionalRepository.findById(2L).get(), gerenciaRepository.findById(1L).get()));
        gerenteRepository.save(new Gerente("Pedro", "pedro@cremhelado.com.co", "123", cargoRepository.findById(3L).get(),
                regionalRepository.findById(2L).get(), gerenciaRepository.findById(2L).get()));
        gerenteRepository.save(new Gerente("Monica", "monica@cremhelado.com.co", "123", cargoRepository.findById(3L).get(),
                regionalRepository.findById(2L).get(), gerenciaRepository.findById(3L).get()));
    }

    // Método para generar claves únicas del estilo "clave123", "clave124", etc.
    private String generarClave() {
        return "clave" + contadorClave.getAndIncrement();
    }

    // Métodos para buscar o crear entidades si no existen
    private Gerencia findOrCreateGerencia(String nombre) {
        Gerencia gerencia = gerenciaRepository.findByNombre(nombre);
        if (gerencia == null) {
            gerencia = new Gerencia();
            gerencia.setNombre(nombre);
            gerenciaRepository.save(gerencia);
        }
        return gerencia;
    }

    private Regional findOrCreateRegional(String nombre) {
        Regional regional = regionalRepository.findByNombre(nombre);
        if (regional == null) {
            regional = new Regional();
            regional.setNombre(nombre);
            regionalRepository.save(regional);
        }
        return regional;
    }

    private Cargo findOrCreateCargo(String nombre) {
        Cargo cargo = cargoRepository.findByNombre(nombre);
        if (cargo == null) {
            cargo = new Cargo();
            cargo.setNombre(nombre);
            cargoRepository.save(cargo);
        }
        return cargo;
    }

    public Area findOrCreateArea(String nombre) {
        // Buscar si el área ya existe
        Area area = areaRepository.findByNombre(nombre);
        if (area == null) {
            // Crear un nuevo área si no existe
            area = new Area();
            area.setNombre(nombre);
            areaRepository.save(area);
        }
        return area;
    }

    private Proponente findOrCreateProponente(String nombre) {
        Proponente proponente = proponenteRepository.findByNombre(nombre);
        if (proponente == null) {
            proponente = new Proponente();
            proponente.setNombre(nombre);
            proponenteRepository.save(proponente);
        }
        return proponente;
    }

    // Método para obtener el valor de una celda de forma segura
    private String getCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null)
            return ""; // Si la celda es nula, devolver cadena vacía

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private float convertirStringAFloat(String texto) {
        try {
            if (texto == null || texto.trim().isEmpty()) {
                return 0.0f; // Valor predeterminado si está vacío
            }
            // Verificar si el texto es numérico antes de convertir
            if (!esNumero(texto)) {
                System.out.println("Advertencia: '" + texto + "' no es un número válido.");
                return 0.0f; // O usa Float.NaN si prefieres indicar que no hay valor
            }
            return Float.parseFloat(texto.trim());
        } catch (NumberFormatException e) {
            System.err.println("Error al convertir '" + texto + "' a float: " + e.getMessage());
            return 0.0f; // Valor predeterminado en caso de error
        }
    }

    // Método auxiliar para verificar si un String es numérico
    private boolean esNumero(String texto) {
        try {
            Float.parseFloat(texto);
            return true;
        } catch (NumberFormatException e) {
            return false; // No es numérico
        }
    }

    // Método para evitar que la descripción sea más larga de 255, si se coloca no
    // se va a tener en cuenta
    private String truncarDescripcion(String descripcion) {
        if (descripcion.length() > 255) {
            return descripcion.substring(0, 255); // Truncar a 255 caracteres
        }
        return descripcion;
    }

    // Método para convertir una cadena a fecha de forma segura
    private java.sql.Date convertirStringAFecha(String fecha) {
        if (fecha == null || fecha.trim().isEmpty()) {
            System.err.println("Advertencia: La fecha está vacía o es nula.");
            return null;
        }

        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date parsedDate = formato.parse(fecha.trim());
            return new java.sql.Date(parsedDate.getTime());
        } catch (ParseException e) {
            System.err.println("Error al convertir la fecha '" + fecha + "': " + e.getMessage());
            return null;
        }
    }

    // Método para verificar si una fila está vacía
    private boolean isRowEmpty(Row row) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false; // Si alguna celda tiene contenido, la fila no está vacía
            }
        }
        return true;
    }

    private int convertirStringAInt(String texto) {
        try {
            if (texto == null || texto.trim().isEmpty()) {
                System.out.println("Advertencia: Cadena vacía, devolviendo 0.");
                return 0; // Devuelve 0 o cualquier valor predeterminado
            }
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            System.err.println("Error: No se pudo convertir '" + texto + "' a entero.");
            return 0; // O cualquier valor predeterminado
        }
    }

}
