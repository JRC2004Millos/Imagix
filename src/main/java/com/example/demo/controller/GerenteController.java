package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

        // Buscar ideas por gerencia y estado "Propuesta"
        List<Idea> ideas = ideaService.findByGerenciaIdAndEstado(gerencia.getId(), "Propuesta");
        
        // Añadir datos al modelo para la vista Thymeleaf
        model.addAttribute("gerente", gerente);
        model.addAttribute("ideas", ideas);
        model.addAttribute("search", search);
        return "mostrarIdeas"; // Renderiza la vista 'mostrarIdeas.html'
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
        return "detalleIdea";
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
    @ResponseBody
    public ResponseEntity<Map<String, Object>> dashboardGerencia(@RequestParam(value = "startDate") String startDate,
                                                        @RequestParam(value = "endDate") String endDate,
                                                        HttpSession session) throws ParseException {

        Gerente gerente = (Gerente) session.getAttribute("gerente");
        if (gerente == null) {
            return null;  // Si no hay sesión activa, puedes retornar un valor apropiado
        }

        // Parsear las fechas proporcionadas por el usuario
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startOfMonth = sdf.parse(startDate);
        Date endOfMonth = sdf.parse(endDate);

        // Obtener las ideas dentro del rango de fechas proporcionado
        List<Idea> ideasPorMes = ideaService.findIdeasBetweenDates(startOfMonth, endOfMonth);

        // Agrupar las ideas por mes
        List<IdeaGroupedByMonthDTO> groupedIdeas = ideasPorMes.stream()
            .collect(Collectors.groupingBy(idea -> getMonthFromDate(idea.getFechaCreacion())))
            .entrySet().stream()
            .map(entry -> new IdeaGroupedByMonthDTO(entry.getKey(), entry.getValue().size()))
            .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("groupedIdeas", groupedIdeas);
        
        return ResponseEntity.ok(response);
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
