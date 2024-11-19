package com.example.demo.controller;

import com.example.demo.model.Gerencia;
import com.example.demo.model.Idea;
import com.example.demo.model.Promotor;
import com.example.demo.service.IdeaService;
import jakarta.servlet.http.HttpSession;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Date;
import java.util.Arrays;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/promotor")
@CrossOrigin("http://localhost:8100")
public class PromotorController {

    // @Autowired
    // private PromotorService promotorService;

    @Autowired
    private IdeaService ideaService;

    // Lista de ideas del promotor
    @GetMapping
    public String listaIdeas(Model model, HttpSession session) {
        Promotor promotor = (Promotor) session.getAttribute("promotor");

        if (promotor == null) {
            return "redirect:/login"; // Redirige si no hay sesión activa
        }

        List<Idea> ideas = ideaService.findByGerenciaId(promotor.getGerencia().getId());
        model.addAttribute("ideas", ideas);

        model.addAttribute("promotor", promotor); // Pasar el promotor al modelo
        return "listaIdeas"; // Mostrar la vista listaIdeas.html
    }

    @GetMapping("/ideas")
    public String mostrarTablaIdeas(
            @RequestParam(required = false) String search,
            Model model,
            HttpSession session) {

        // Verificar si el usuario tiene sesión activa
        Promotor promotor = (Promotor) session.getAttribute("promotor");
        if (promotor == null) {
            return "redirect:/login"; // Redirige al login si no hay sesión
        }

        // Obtener la gerencia del promotor
        Gerencia gerencia = promotor.getGerencia();
        if (gerencia == null) {
            model.addAttribute("error", "El promotor no tiene una gerencia asignada.");
            return "error"; // Muestra una página de error o maneja el error
        }

        // Buscar ideas por gerencia, estado "Propuesta" y por nombre si se proporciona
        // búsqueda
        List<Idea> ideas;

        // ideas = ideaService.findByGerenciaIdAndEstado(gerencia.getId(), "Propuesta");
        ideas = ideaService.findByFechaAprobacionIsNullAndGerenciaId(gerencia.getId());

        // Añadir datos al modelo para la vista Thymeleaf
        model.addAttribute("promotor", promotor);
        model.addAttribute("ideas", ideas);
        model.addAttribute("search", search);

        return "mostrarIdeas"; // Renderiza la vista 'mostrarIdeas.html'
    }

    @GetMapping("/idea/{id}")
    public String informacionIdea(@PathVariable("id") Long id, Model model, HttpSession session) {

        Idea idea = ideaService.findById(id);
        model.addAttribute("idea", idea);

        List<String> estados = Arrays.asList("Banco de Ideas", "Devolver al Proponente", "Documentada para Comité",
                "En Desarrollo", "Enviada a Cliente Interno", "Enviada a IMAGIX", "Exito Innovador",
                "Exito Innovador de Alto Impacto", "Filtro para Comité", "Mejor Idea", "No aplica para el programa",
                " No viable para implementar", "Propuesta");

        Promotor promotor = (Promotor) session.getAttribute("promotor");
        model.addAttribute("promotor", promotor);
        model.addAttribute("estados", estados);

        return "detalleIdea";
    }

    /*
     * /
     * 
     * @PutMapping("idea/{id}")
     * public String actualizarCalificacion(@PathVariable ("id") Long
     * id, @RequestBody Idea nuevaIdea, Model model) {
     * Idea ideaExistente = ideaService.findById(id);
     * if(ideaExistente == null){
     * throw new RuntimeException("Idea no encontrada con id" + id);
     * }
     * 
     * ideaExistente.setCalificacion(nuevaIdea.getCalificacion());
     * ideaService.save(ideaExistente);
     * 
     * model.addAttribute("idea", ideaExistente);
     * return "detalleIdea";
     * }
     */

    @PostMapping("/idea/{id}")
    public String guardarCambiosIdea(
            @PathVariable("id") Long id,
            @RequestParam("estado") String estado,
            @RequestParam("nuevoComentario") String nuevoComentario,
            @RequestParam(value = "nuevaCalificacion", required = false) Double nuevaCalificacion,
            @RequestParam("accion") String accion) {

        // Buscar la idea por su ID
        Idea idea = ideaService.findById(id);
        if (idea == null) {
            throw new IllegalArgumentException("Idea no encontrada con ID: " + id);
        }

        // Actualizar los campos de la idea
        idea.setEstado(estado);
        idea.setComentario(nuevoComentario);
        if (nuevaCalificacion != null) {
            idea.setCalificacion(nuevaCalificacion.floatValue());
        }

        // Guardar los cambios
        ideaService.save(idea);

        if ("aceptar".equals(accion)) {
            idea.setFechaAprobacion(new Date(System.currentTimeMillis()));

        } else if ("rechazar".equals(accion)) {

        }

        // Redirigir nuevamente a la página de detalles
        return "redirect:/promotor/idea/" + id;
    }

    @PutMapping("idea/{id}/estado")
    @ResponseBody
    public String actualizarEstado(@PathVariable("id") Long id, @RequestBody Idea nuevaIdea) {
        Idea ideaExistente = ideaService.findById(id);

        if (ideaExistente == null) {
            return "Idea no encontrada";
        }

        ideaExistente.setEstado(nuevaIdea.getEstado());
        ideaService.save(ideaExistente);
        return "Estado actualizado correctamente";
    }

}