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
import com.example.demo.model.IdeaSimilarDTO;
import com.example.demo.model.Proponente;

import okhttp3.*;
import okhttp3.RequestBody;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/proponente")
public class ProponenteController {

    private static final Logger log = LoggerFactory.getLogger(ProponenteController.class);

    @Autowired
    private ProponenteService proponenteService;

    @Autowired
    private IdeaService ideaService;

    @Autowired
    private GerenciaService gerenciaService;

    @Value("${openai.api.key}")
    private String API_KEY;

    private static final String EMBEDDING_URL = "https://api.openai.com/v1/embeddings";
    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

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

    /**
     * Genera una calificación sugerida para la idea dada.
     *
     * @param descripcionIdea La descripción de la idea.
     * @return Una calificación sugerida como float.
     * @throws IOException Si hay un error en la llamada a la API.
     */
    public float generarCalificacionSugerida(String descripcionIdea) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // Crear el JSON para la solicitud en formato compatible con chat models
        JSONObject json = new JSONObject();
        json.put("model", "gpt-4");

        // Mensajes según el formato de chat
        JSONArray messages = new JSONArray();
        messages.put(new JSONObject()
                .put("role", "system")
                .put("content", "Eres un evaluador que asigna calificaciones numéricas."));
        messages.put(new JSONObject()
                .put("role", "user")
                .put("content", "A continuación, se describe una idea. Evalúa su claridad, relevancia y aplicabilidad. "
                        +
                        "Devuelve únicamente una calificación numérica (float) entre 0 y 10. No incluyas texto adicional.\n\n"
                        +
                        "Descripción de la idea: " + descripcionIdea + "\n\nCalificación numérica:"));

        json.put("messages", messages);
        json.put("max_tokens", 10); // Solo queremos un número
        json.put("temperature", 0.0); // Fija la temperatura a 0 para respuestas más consistentes

        // Configurar la solicitud HTTP
        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(OPENAI_URL) // URL del endpoint
                .header("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        // Procesar la respuesta
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error en la solicitud: " + response);
            }

            // Procesar el cuerpo de la respuesta
            String responseBody = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseBody);

            // Obtener la calificación como texto desde la respuesta de la API
            String calificacionString = jsonResponse.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim();

            // Verificar si la respuesta es un número válido
            try {
                return Float.parseFloat(calificacionString);
            } catch (NumberFormatException e) {
                System.err.println("Respuesta inesperada de la API: " + calificacionString);
                throw new IOException("La API devolvió un valor no numérico: " + calificacionString, e);
            }
        }
    }

    // /**
    // * Genera un prompt para evaluar una idea.
    // *
    // * @param descripcionIdea La descripción de la idea.
    // * @return Un prompt para la API de OpenAI.
    // */
    // private String generarPromptCalificacion(String descripcionIdea) {
    // return "A continuación, se describe una idea. Evalúa su claridad, relevancia
    // y aplicabilidad. " +
    // "Devuelve únicamente una calificación numérica (float) entre 0 y 10. No
    // incluyas texto adicional:\n\n" +
    // "Descripción de la idea: " + descripcionIdea + "\n\n" +
    // "Calificación numérica:";
    // }

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

        if (proponente == null) {
            return "redirect:/login";
        }

        model.addAttribute("proponente", proponente);
        return "crearIdea";
    }

    // POST para recibir los datos del formulario de subir idea (formulario1)
    @PostMapping("/subirIdea")
    public String procesarIdea(@RequestParam("nombreIdea") String nombreIdea, HttpSession session) {
        // Guardar la idea en la sesión temporalmente
        session.setAttribute("nombreIdea", nombreIdea);
        session.setAttribute("estadoImplementada", false);

        // Redirigir a la siguiente pantalla (por ejemplo, subir proponentes)
        return "redirect:/proponente/subirProponentes";
    }

    // GET para mostrar la vista de ingresar la descripción del problema
    @GetMapping("/descripcionProblema")
    public String subirDescripcion(HttpSession session, Model model) {

        Proponente proponente = (Proponente) session.getAttribute("proponente");

        if (proponente == null) {
            return "redirect:/login";
        }

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
    public String subirSolucion(HttpSession session, Model model) {
        Proponente proponente = (Proponente) session.getAttribute("proponente");

        if (proponente == null) {
            return "redirect:/login";
        }

        model.addAttribute("proponente", proponente);
        return "descripcionSolucion";
    }

    // POST para recibir la descripción de la solución
    @PostMapping("/descripcionSolucion")
    public String recibirDescripcionSolucion(
            @RequestParam("descripcionSolucion") String descripcionSolucion,
            HttpSession session, Model model) throws IOException {

        if (descripcionSolucion == null || descripcionSolucion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción de la solución no puede estar vacía.");
        }

        session.setAttribute("descripcionSolucion", descripcionSolucion);

        List<String> descriptions = ideaService.findAllDescriptions();
        IdeaSimilarDTO ideaSimilar = ideaService.calculateAllSimilarities(descriptions, descripcionSolucion);

        if (ideaSimilar.getSimilaridad() > 40.0) {
            log.info("La idea es similar a otras existentes. Mayor similitud: " + ideaSimilar.getSimilaridad() + "%");
            model.addAttribute("ideaSimilar", ideaSimilar);
            return "porcentajeCoincidencia";
        }

        log.info("La idea no es similar a ninguna existente. Pasó con un " + ideaSimilar.getSimilaridad()
                + "% de similitud.");

        String nombreIdea = (String) session.getAttribute("nombreIdea");
        String descripcionProblema = (String) session.getAttribute("descripcionProblema");
        boolean estadoImplementada = (boolean) session.getAttribute("estadoImplementada");
        Date fechaImplementacion = (Date) session.getAttribute("fechaImplementacion");
        @SuppressWarnings("unchecked")
        List<Proponente> proponentes = (List<Proponente>) session.getAttribute("proponentes");

        if (nombreIdea == null || proponentes == null) {
            throw new IllegalStateException("Faltan datos necesarios en la sesión.");
        }

        List<Proponente> proponentesGestionados = proponentes.stream()
                .map(p -> proponenteService.findById(p.getId()))
                .toList();

        Idea idea = new Idea();
        idea.setNombreIdea(nombreIdea);
        idea.setDescripcion(descripcionSolucion);
        idea.setSituacionDetectada(descripcionProblema);
        idea.setProponentes(proponentesGestionados);
        idea.setFechaCreacion(new Date(System.currentTimeMillis()));
        idea.setEstadoImplementada(estadoImplementada);
        if (estadoImplementada) {
            idea.setFechaImplementacion(fechaImplementacion);
            idea.setEstado("En Desarrollo");
        } else {
            idea.setEstado("Propuesta");
        }

        Proponente proponente = (Proponente) session.getAttribute("proponente");
        idea.setResponsable(proponente != null ? proponente.getNombre() : "Desconocido");
        idea.setCalificacion(generarCalificacionSugerida(descripcionSolucion));
        idea.setGerencia(gerenciaService.findByNombre(clasificarIdea(descripcionSolucion)));

        ideaService.save(idea);

        session.removeAttribute("nombreIdea");
        session.removeAttribute("descripcionProblema");
        session.removeAttribute("descripcionSolucion");
        session.removeAttribute("proponentes");

        return "redirect:/proponente";
    }

    // GET para mostrar la vista de subir reto
    @GetMapping("/subirReto")
    public String subirReto(HttpSession session, Model model) {

        Proponente proponente = (Proponente) session.getAttribute("proponente");

        if (proponente == null) {
            return "redirect:/login";
        }

        model.addAttribute("proponente", proponente);
        return "crearReto";
    }

    // POST para procesar el reto del formulario
    @PostMapping("/subirReto")
    public String procesarReto(@RequestParam("nombreReto") String nombreReto, HttpSession session) {
        session.setAttribute("nombreIdea", nombreReto);
        session.setAttribute("estadoImplementada", true);
        return "redirect:/proponente/fechaImplementacion"; // Ir a la siguiente pantalla
    }

    @GetMapping("/fechaImplementacion")
    public String fechaImplementacion(HttpSession session, Model model) {

        Proponente proponente = (Proponente) session.getAttribute("proponente");

        if (proponente == null) {
            return "redirect:/login";
        }

        model.addAttribute("proponente", proponente);
        return "fechaImplementacion";
    }

    @PostMapping("/fechaImplementacion")
    public String procesarFechaImplementacion(@RequestParam("fechaImplementacion") Date fechaImplementacion,
            HttpSession session) {
        session.setAttribute("fechaImplementacion", fechaImplementacion);
        return "redirect:/proponente/subirProponentes"; // Ir a la siguiente pantalla
    }

    // GET para mostrar la vista de ingresar proponentes
    @GetMapping("/subirProponentes")
    public String subirProponentes(Model model, HttpSession session) {

        Proponente proponente = (Proponente) session.getAttribute("proponente");

        if (proponente == null) {
            return "redirect:/login";
        }

        model.addAttribute("proponente", proponente);
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

    @GetMapping("/ideas")
    public String verIdeaString(Model model, HttpSession session) {
        Proponente proponente = (Proponente) session.getAttribute("proponente");

        if (proponente == null) {
            return "redirect:/login";
        }

        model.addAttribute("proponente", proponente);
        model.addAttribute("ideas",
                ideaService.findIdeasByProponenteIdAndEstadoImplementadaIsFalse(proponente.getId()));

        return "ideasProponente";
    }

    @GetMapping("/perfil")
    public String verPerfil(Model model, HttpSession session) {
        Proponente proponente = (Proponente) session.getAttribute("proponente");

        if (proponente == null) {
            return "redirect:/login";
        }

        model.addAttribute("proponente", proponente);
        return "perfilProponente";
    }

    @GetMapping("/retos")
    public String verRetoString(Model model, HttpSession session) {
        Proponente proponente = (Proponente) session.getAttribute("proponente");

        if (proponente == null) {
            return "redirect:/login";
        }

        model.addAttribute("proponente", proponente);
        model.addAttribute("ideas", ideaService.findIdeasByProponenteIdAndEstadoImplementadaIsTrue(proponente.getId()));
        return "retosProponente";
    }

}
