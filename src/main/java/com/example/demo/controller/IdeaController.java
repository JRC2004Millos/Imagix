package com.example.demo.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.model.Idea;
import com.example.demo.service.IdeaService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/ideas")
public class IdeaController {

    @Autowired
    private IdeaService ideaService;

    @GetMapping("/ver/{id}")
    public String verDetalleIdea(@PathVariable("id") Long id, Model model, HttpSession session) {
        model.addAttribute("idea", ideaService.findById(id));
        return "idea";
    }

    @GetMapping("/editar/{id}")
    public String editarIdea(@PathVariable("id") Long id, Model model, HttpSession session) {

        model.addAttribute("idea", ideaService.findById(id));
        return "editarIdea";
    }

    @PostMapping("/guardar")
    public String editarIdea(@ModelAttribute("idea") Idea idea) {
        Idea ideaExistente = ideaService.findById(idea.getId());

        if (ideaExistente == null) {
            return "redirect:/ideas/ver/" + idea.getId();
        }

        // Actualizar los campos que vienen del formulario
        ideaExistente.setNombreIdea(idea.getNombreIdea());
        ideaExistente.setDescripcion(idea.getDescripcion());
        ideaExistente.setEstado(idea.getEstado());

        // Otros campos no incluidos en el formulario permanecen inalterados

        // Guardar la entidad actualizada
        ideaService.save(ideaExistente);
        return "redirect:/ideas/ver/" + idea.getId();
    }

}
