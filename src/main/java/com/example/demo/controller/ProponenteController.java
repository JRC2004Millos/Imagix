package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.service.ProponenteService;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/proponente")
public class ProponenteController {

    @Autowired
    private ProponenteService proponenteService;

    @GetMapping
    public String dashboard() {
        return "empleadoDecision";
    }

    @GetMapping("/subirIdea")
    public String subirIdea() {
        return "crearIdea";
    }

    @GetMapping("/subirReto")
    public String subirReto() {
        return "crearReto";
    }

    @GetMapping("/subirProponentes")
    public String subirProponenteString() {
        return "ingresarProponentes";
    }

    @GetMapping("/descripcionProblema")
    public String subirDescripcion() {
        return "descripcionProblema";
    }

    @GetMapping("/descripcionSolucion")
    public String subirSolucion() {
        return "descripcionSolucion";
    }

    @GetMapping("/solucion")
    public String solucion() {
        return "solucionEmpleado";
    }

    @GetMapping("/reto")
    public String reto() {
        return "retosEmpleado";
    }

}
