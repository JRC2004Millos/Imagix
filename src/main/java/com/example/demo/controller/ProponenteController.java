package com.example.demo.controller;

import java.sql.Date;
import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.service.GerenciaService;
import com.example.demo.service.IdeaService;
import com.example.demo.service.ProponenteService;
import com.example.demo.model.Idea;
import com.example.demo.model.Proponente;

import okhttp3.*;
import okhttp3.RequestBody;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/proponente")
public class ProponenteController {

    @Autowired
    private ProponenteService proponenteService;

    @Autowired
    private IdeaService ideaService;

    @Autowired
    private GerenciaService gerenciaService;

    @Value("${openai.api.key}")
    private String API_KEY;

    private static final String EMBEDDING_URL = "https://api.openai.com/v1/embeddings";

    // Mapa con las categorías y sus descripciones generales
    private static final Map<String, String> categorias = Map.of(
            "Ventas", "Gestionar la venta de productos y servicios.",
            "Financiera", "Administración de recursos económicos y financieros.",
            "Cadena", "Logística y gestión de la cadena de suministros.",
            "Mercadeo", "Promoción y estrategias de mercado para atraer clientes.",
            "DHCO", "Desarrollo humano y gestión de talento organizacional.");

    public String clasificarIdea(String nuevaIdea) throws IOException {
        // Obtener el embedding de la nueva idea
        double[] embeddingIdea = obtenerEmbedding(nuevaIdea);

        // Calcular la similitud entre la idea y cada categoría
        String mejorCategoria = null;
        double mejorSimilitud = -1;

        for (Map.Entry<String, String> categoria : categorias.entrySet()) {
            double[] embeddingCategoria = obtenerEmbedding(categoria.getValue());
            double similitud = calcularCosineSimilarity(embeddingIdea, embeddingCategoria);

            if (similitud > mejorSimilitud) {
                mejorSimilitud = similitud;
                mejorCategoria = categoria.getKey();
            }
        }

        return mejorCategoria;
    }

    private double[] obtenerEmbedding(String texto) throws IOException {
        OkHttpClient client = new OkHttpClient();

        JSONObject json = new JSONObject();
        json.put("model", "text-embedding-ada-002");
        json.put("input", texto);

        RequestBody body = RequestBody.create(
                json.toString(), MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(EMBEDDING_URL)
                .header("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            int statusCode = response.code();

            if (statusCode == 401) {
                throw new IOException("Error: Unauthorized. Verifica tu clave API.");
            } else if (statusCode == 429) {
                throw new IOException("Error: Too Many Requests. Reduce la frecuencia de las solicitudes.");
            } else if (!response.isSuccessful()) {
                throw new IOException("Error en la solicitud: " + response);
            }

            String responseBody = response.body().string();
            JSONArray data = new JSONObject(responseBody)
                    .getJSONArray("data")
                    .getJSONObject(0)
                    .getJSONArray("embedding");

            double[] embedding = new double[data.length()];
            for (int i = 0; i < data.length(); i++) {
                embedding[i] = data.getDouble(i);
            }
            return embedding;
        }
    }

    private double calcularCosineSimilarity(double[] vec1, double[] vec2) {
        double dotProduct = 0.0;
        double normVec1 = 0.0;
        double normVec2 = 0.0;

        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];
            normVec1 += Math.pow(vec1[i], 2);
            normVec2 += Math.pow(vec2[i], 2);
        }

        return dotProduct / (Math.sqrt(normVec1) * Math.sqrt(normVec2));
    }

    // Página principal del proponente
    @GetMapping
    public String proponente() {
        return "empleadoDecision";
    }

    @GetMapping("/all")
    public List<Proponente> obtenerProponentes() {
        return proponenteService.findAll(); // Devuelve la lista de proponentes
    }

    // GET para mostrar la vista de subir idea
    @GetMapping("/subirIdea")
    public String subirIdea(HttpSession session, Model model) {
        Proponente proponente = (Proponente) session.getAttribute("proponente");

        System.out.println("proponente: " + proponente.getNombre());

        model.addAttribute("proponente", proponente);

        return "crearIdea";
    }

    // POST para recibir los datos del formulario de subir idea (formulario1)
    @PostMapping("/subirIdea")
    public String procesarIdea(@RequestParam("nombreIdea") String nombreIdea, HttpSession session) {
        // Guardar la idea en la sesión temporalmente
        session.setAttribute("nombreIdea", nombreIdea);

        // Redirigir a la siguiente pantalla (por ejemplo, subir proponentes)
        return "redirect:/proponente/subirProponentes";
    }

    // GET para mostrar la vista de ingresar la descripción del problema
    @GetMapping("/descripcionProblema")
    public String subirDescripcion(Model model, HttpSession session) {
        Proponente proponente = (Proponente) session.getAttribute("proponente");

        model.addAttribute("proponente", proponente);
        return "descripcionProblema";
    }

    // POST para recibir la descripción del problema
    @PostMapping("/descripcionProblema")
    public String recibirDescripcionProblema(@RequestParam("descripcionProblema") String descripcionProblema,
            HttpSession session) {
        // Guardar la descripción de la solución en la sesión
        session.setAttribute("descripcionProblema", descripcionProblema);

        // Redirigir a la siguiente pantalla
        return "redirect:/proponente/descripcionSolucion";
    }

    // GET para mostrar la vista de ingresar la descripción de la solución
    @GetMapping("/descripcionSolucion")
    public String subirSolucion() {
        return "descripcionSolucion";
    }

    // POST para recibir la descripción de la solución
    @PostMapping("/descripcionSolucion")
    public String recibirDescripcionSolucion(
            @RequestParam("descripcionSolucion") String descripcionSolucion,
            HttpSession session) throws IOException {

        if (descripcionSolucion == null || descripcionSolucion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción de la solución no puede estar vacía.");
        }

        // Guardar la descripción de la solución en la sesión
        session.setAttribute("descripcionSolucion", descripcionSolucion);

        // Recuperar los datos de la idea desde la sesión
        String nombreIdea = (String) session.getAttribute("nombreIdea");
        String descripcionProblema = (String) session.getAttribute("descripcionProblema");
        @SuppressWarnings("unchecked")
        List<Proponente> proponentes = (List<Proponente>) session.getAttribute("proponentes");

        // Gestionar proponentes
        List<Proponente> proponentesGestionados = new ArrayList<>();
        for (Proponente proponente : proponentes) {
            Proponente proponenteGestionado = proponenteService.findById(proponente.getId());
            proponentesGestionados.add(proponenteGestionado);
        }

        // Crear y clasificar la idea
        Idea idea = new Idea();
        idea.setNombreIdea(nombreIdea);
        idea.setDescripcion(descripcionSolucion);
        idea.setSituacionDetectada(descripcionProblema);
        idea.setProponentes(proponentesGestionados); // Usar la lista gestionada
        idea.setFechaCreacion(new Date(System.currentTimeMillis()));
        idea.setEstado("Propuesta");
        idea.setEstadoImplementada(false);

        // Recuperar el proponente desde la sesión y verificar que no sea null
        Proponente proponente = (Proponente) session.getAttribute("proponente");
        idea.setResponsable(proponente != null ? proponente.getNombre() : "Desconocido");

        idea.setCalificacion(0);
        idea.setComentario("");
        idea.setEstadoCalificacion("Pendiente");
        idea.setFechaAprobacion(null);

        // **Clasificar la idea según la descripción**
        String gerenciaString = clasificarIdea(descripcionSolucion);

        idea.setGerencia(gerenciaService.findByNombre(gerenciaString)); // Asignar la categoría clasificada

        System.out.println("Se ha clasificado la idea como: " + gerenciaString);

        // Guardar la idea usando el servicio IdeaService
        ideaService.save(idea);

        System.out.println("Se ha guardado la idea: " + idea.getNombreIdea());

        // Mostrar los proponentes asociados
        for (Proponente p : proponentesGestionados) {
            System.out.println("Se ha guardado el proponente: " + p.getNombre());
        }

        // Limpiar los atributos de la sesión
        session.removeAttribute("nombreIdea");
        session.removeAttribute("descripcionProblema");
        session.removeAttribute("descripcionSolucion");
        session.removeAttribute("proponentes");

        // Redirigir a la pantalla final o dashboard
        return "redirect:/proponente";
    }

    // GET para mostrar la vista de subir reto
    @GetMapping("/subirReto")
    public String subirReto() {
        return "crearReto";
    }

    // POST para procesar el reto del formulario
    @PostMapping("/subirReto")
    public String procesarReto(@RequestParam("nombreReto") String nombreReto, HttpSession session) {
        session.setAttribute("nombreReto", nombreReto);
        return "redirect:/proponente/solucion"; // Ir a la siguiente pantalla
    }

    // GET para mostrar la vista de ingresar proponentes
    @GetMapping("/subirProponentes")
    public String subirProponentes(Model model, HttpSession session) {
        model.addAttribute("proponentes", proponenteService.findAll());
        model.addAttribute("proponente", session.getAttribute("proponente"));
        return "ingresarProponentes";
    }

    // POST para recibir los proponentes
    @PostMapping("/subirProponentes")
    public String procesarProponentes(@RequestParam("proponentesIds") List<Long> proponentesIds, HttpSession session) {

        List<Proponente> proponentes = new ArrayList<>(); // Suponiendo que este método

        for (Long proponenteId : proponentesIds) {
            Proponente proponente = proponenteService.findById(proponenteId);
            if (proponente != null) {
                proponentes.add(proponente);
            } else {
                System.out.println("Proponente no encontrado: " + proponenteId);
                ;
            }
        }
        // existe
        for (Proponente proponente : proponentes) {
            System.out.println("Se ha guardado el proponente: " + proponente.getNombre());
        }
        session.setAttribute("proponentes", proponentes);

        return "redirect:/proponente/descripcionProblema";
    }

    // GET para mostrar la vista de solución
    @GetMapping("/solucion")
    public String solucion() {
        return "solucionEmpleado";
    }

    // GET para mostrar la vista de retos
    @GetMapping("/reto")
    public String reto() {
        return "retosEmpleado";
    }

    // GET para mostrar la página de confirmación
    @GetMapping("/ideaCreada")
    public String ideaCreada() {
        return "ideaCreada"; // Página de confirmación
    }
}
