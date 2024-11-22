package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.model.Gerente;
import com.example.demo.model.Idea;
import com.example.demo.model.IdeaGroupedByMonthDTO;
import com.example.demo.service.GerenteService;
import com.example.demo.service.IdeaService;
import jakarta.servlet.http.HttpSession;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.demo.model.Gerencia;

@Controller
@RequestMapping("/gerente")
public class GerenteController {

    @Autowired
    private GerenteService gerenteService;

    @Autowired
    private IdeaService ideaService;

    // Lista de ideas del gerente
    @GetMapping
    public String listaIdeas(Model model, HttpSession session) {
        Gerente gerente = (Gerente) session.getAttribute("gerente");
        if (gerente == null) {
            return "redirect:/login";  // Redirige si no hay sesión activa
        }
        model.addAttribute("gerente", gerente);  // Pasar el gerente al modelo
        return "inicialGerente";  // Mostrar la vista listaIdeas.html
    }

    /*    @GetMapping("/ideas")
    public String mostrarTablaIdeas(@RequestParam(required = false) String search, Model model, HttpSession session) {
        // Verificar si el usuario tiene sesión activa
        Gerente gerente = (Gerente) session.getAttribute("gerente");
        if (gerente == null) {
            return "redirect:/login"; // Redirige al login si no hay sesión
        }
        // Obtener la gerencia del gerente
        Gerencia gerencia = gerente.getGerencia();
        if (gerencia == null) {
            model.addAttribute("error", "El gerente no tiene una gerencia asignada.");
            return "error"; // Muestra una página de error o maneja el error
        }

        // Buscar ideas por gerencia y estado "Propuesta"
        List<Idea> ideas = ideaService.findByGerenciaIdAndEstado(gerencia.getId(), "Propuesta");
        
        // Añadir datos al modelo para la vista Thymeleaf
        model.addAttribute("gerente", gerente);
        model.addAttribute("ideas", ideas);
        model.addAttribute("search", search);
        return "mostrarIdeasGerente"; // Renderiza la vista 'mostrarIdeas.html'
    }
*/

@GetMapping("/ideas")
public String mostrarTablaIdeas(@RequestParam(required = false) String search, Model model, HttpSession session) {
    // Verificar si el usuario tiene sesión activa
    Gerente gerente = (Gerente) session.getAttribute("gerente");
    if (gerente == null) {
        return "redirect:/login"; // Redirige al login si no hay sesión
    }

    // Obtener la gerencia del gerente
    Gerencia gerencia = gerente.getGerencia();
    if (gerencia == null) {
        model.addAttribute("error", "El gerente no tiene una gerencia asignada.");
        return "error"; // Muestra una página de error o maneja el error
    }

    // Filtrar las ideas que pertenecen a la gerencia del gerente
    // y que tengan una fecha de aprobación no nula
    List<Idea> ideas = ideaService.findByGerenciaIdAndEstadoAndFechaAprobacionNotNull(gerencia.getId(), "Exito Innovador");

    // Añadir datos al modelo para la vista Thymeleaf
    model.addAttribute("gerente", gerente);
    model.addAttribute("ideas", ideas);
    model.addAttribute("search", search);

    return "mostrarIdeasGerente"; // Renderiza la vista 'mostrarIdeas.html'
}

    @GetMapping("/idea/{id}")
    public String informacionIdea(@PathVariable("id") Long id, Model model, HttpSession session) {
        Idea idea = ideaService.findById(id);
        if (idea == null) {
            model.addAttribute("error", "La idea no existe.");
            return "error";
        }
        model.addAttribute("idea", idea);

        Gerente gerente = (Gerente) session.getAttribute("gerente");
        model.addAttribute("gerente", gerente);
        return "detalleIdeaGerente";
    }

    // Aprobar una idea
    @PostMapping("/aprobar/{id}")
    public String aprobarIdea(@PathVariable("id") Long ideaId, HttpSession session, Model model) {
        Gerente gerente = (Gerente) session.getAttribute("gerente");
        if (gerente == null) {
            return "redirect:/login";
        }

        Idea idea = ideaService.findById(ideaId);
        if (idea == null || !idea.getEstado().equals("En revisión por gerente")) {
            model.addAttribute("error", "Idea no válida para aprobación.");
            return "error";
        }

        gerenteService.aprobarIdea(ideaId, gerente.getId());
        model.addAttribute("message", "La idea ha sido aprobada exitosamente.");
        return "redirect:/gerente/ideas"; // Redirige a la lista de ideas
    }

    // Rechazar una idea
    @PostMapping("/rechazar/{id}")
    public String rechazarIdea(@PathVariable("id") Long ideaId, HttpSession session, Model model) {
        Gerente gerente = (Gerente) session.getAttribute("gerente");
        if (gerente == null) {
            return "redirect:/login";
        }

        Idea idea = ideaService.findById(ideaId);
        if (idea == null || !idea.getEstado().equals("En revisión por gerente")) {
            model.addAttribute("error", "Idea no válida para rechazo.");
            return "error";
        }

        // Cambiar el estado de la idea a "Rechazada"
        idea.setEstado("Rechazada");
        ideaService.save(idea);
        model.addAttribute("message", "La idea ha sido rechazada.");
        return "redirect:/gerente/ideas"; // Redirige a la lista de ideas
    }

    @GetMapping("/dashboard")
    public String dashboardGerencia(Model model, HttpSession session) throws ParseException {

        Gerente gerente = (Gerente) session.getAttribute("gerente");
        if (gerente == null) {
            return "redirect:/login"; // Redirige si no hay sesión activa
        }

        // Obtener el nombre de la gerencia
        String nombreGerencia = gerente.getGerencia().getNombre();
        model.addAttribute("nombreGerencia", nombreGerencia);
        model.addAttribute("gerente", gerente);
        
        return "dashboardGerente";  // Mostrar la vista HTML
    }

    @GetMapping("/dashboardF")
    public ResponseEntity<List<IdeaGroupedByMonthDTO>> dashboardGerencia(@RequestParam(value = "year") String year,
                                                                        HttpSession session) throws ParseException {

        // Verificar si el gerente está en sesión
        Gerente gerente = (Gerente) session.getAttribute("gerente");
        if (gerente == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Si no hay sesión activa, retornar un error 401
        }

        // Parsear el año proporcionado por el usuario
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = sdf.parse(year + "-01-01"); // Primer día del año
        Date endDate = sdf.parse(year + "-12-31");   // Último día del año

        // Imprimir las fechas de inicio y fin
        System.out.println("Fecha de inicio: " + startDate);
        System.out.println("Fecha de fin: " + endDate);


        // Obtener las ideas dentro del rango de fechas proporcionado
        List<Idea> ideasPorAno = ideaService.findIdeasByYearAndGerencia(startDate, endDate, gerente.getGerencia());

        // Agrupar las ideas por mes
        List<IdeaGroupedByMonthDTO> groupedIdeas = ideasPorAno.stream()
                .collect(Collectors.groupingBy(idea -> getMonthFromDate(idea.getFechaCreacion())))
                .entrySet().stream()
                .map(entry -> new IdeaGroupedByMonthDTO(entry.getKey(), entry.getValue().size()))
                .collect(Collectors.toList());

        // Retornar las ideas agrupadas como JSON
        return ResponseEntity.ok(groupedIdeas);
    }



    @GetMapping("/ideasMes")
    public String getIdeasPorMes(Model model) {
        return null;
    }

    // Función para obtener el mes de una fecha (puedes adaptarlo si usas otro tipo de fecha)
    private int getMonthFromDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH) + 1;  // Los meses en Calendar son 0-indexed, por eso sumamos 1
    }

}