package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.model.Idea;
import com.example.demo.service.IdeaService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/retos")
public class RetoController {

    @Autowired
    private IdeaService ideaService;

    @GetMapping("/ver/{id}")
    public String verDetalleIdea(@PathVariable("id") Long id, Model model, HttpSession session) {
        model.addAttribute("idea", ideaService.findById(id));
        return "reto";
    }

    @GetMapping("/editar/{id}")
    public String editarIdea(@PathVariable("id") Long id, Model model, HttpSession session) {

        model.addAttribute("idea", ideaService.findById(id));
        return "editarReto";
    }

    @PostMapping("/guardar")
    public String editarIdea(@ModelAttribute("idea") Idea idea) {
        Idea ideaExistente = ideaService.findById(idea.getId());

        if (ideaExistente == null) {
            return "redirect:/retos/ver/" + idea.getId();
        }

        // Actualizar los campos que vienen del formulario
        ideaExistente.setNombreIdea(idea.getNombreIdea());
        ideaExistente.setDescripcion(idea.getDescripcion());
        ideaExistente.setSituacionDetectada(idea.getSituacionDetectada());

        // Otros campos no incluidos en el formulario permanecen inalterados

        // Guardar la entidad actualizada
        ideaService.save(ideaExistente);
        return "redirect:/retos/ver/" + idea.getId();
    }
}
