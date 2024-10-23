package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Gerente;
import com.example.demo.model.Idea;
import com.example.demo.service.GerenteService;
import com.example.demo.service.IdeaService;

import jakarta.servlet.http.HttpSession;

import java.util.List;

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
        return "listaIdeas";  // Mostrar la vista listaIdeas.html
    }

    @GetMapping("/ideas")
    public String mostrarTablaIdeas(
            @RequestParam(required = false) String search, 
            Model model, 
            HttpSession session) {

        // Verificar si el usuario tiene sesión activa
        Gerente gerente = (Gerente) session.getAttribute("gerente");
        if (gerente == null) {
            return "redirect:/login"; // Redirige al login si no hay sesión
        }

        // Buscar ideas por gerente
        List<Idea> ideas = ideaService.findByGerenteId(gerente.getId());
       
        // Añadir datos al modelo para la vista Thymeleaf
        model.addAttribute("gerente", gerente);
        model.addAttribute("ideas", ideas);
        model.addAttribute("search", search);

        return "mostrarIdeas"; // Renderiza la vista 'mostrarIdeas.html'
    }

    @GetMapping("/idea/{id}")
    public String informacionIdea(@PathVariable("id") Long id, Model model, HttpSession session) {
        Idea idea = ideaService.findById(id);
        model.addAttribute("idea", idea);

        Gerente gerente = (Gerente) session.getAttribute("gerente");
        model.addAttribute("gerente", gerente);
        return "detalleIdea";
    }

    @PostMapping("/aprobar/{id}")
    public String aprobarIdea(@PathVariable("id") Long ideaId, HttpSession session) {
        Gerente gerente = (Gerente) session.getAttribute("gerente");
        if (gerente != null) {
            gerenteService.aprobarIdea(ideaId, gerente.getId());
        }
        return "redirect:/gerente/ideas"; // Redirige a la lista de ideas
    }
}
