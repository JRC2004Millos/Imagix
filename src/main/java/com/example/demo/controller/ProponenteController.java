package com.example.demo.controller;

import java.sql.Date;
import java.util.List;
import java.util.ArrayList;

import org.apache.el.stream.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.service.IdeaService;
import com.example.demo.service.ProponenteService;
import com.example.demo.model.Idea;
import com.example.demo.model.Proponente;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/proponente")
public class ProponenteController {

    @Autowired
    private ProponenteService proponenteService;

    @Autowired
    private IdeaService ideaService;

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
    public String subirIdea() {
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
    public String subirDescripcion() {
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
            HttpSession session) {

        // Guardar la descripción de la solución en la sesión
        session.setAttribute("descripcionSolucion", descripcionSolucion);

        // Recuperar los datos de la idea desde la sesión
        String nombreIdea = (String) session.getAttribute("nombreIdea");
        String descripcionProblema = (String) session.getAttribute("descripcionProblema");
        List<Proponente> proponentes = (List<Proponente>) session.getAttribute("proponentes");

        // Asegúrate de que cada proponente esté asociado a la sesión actual.
        List<Proponente> proponentesGestionados = new ArrayList<>();
        for (Proponente proponente : proponentes) {
            Proponente proponenteGestionado = proponenteService.findById(proponente.getId());
            proponentesGestionados.add(proponenteGestionado);
        }

        // Crear y guardar la idea utilizando un servicio (IdeaService)
        Idea idea = new Idea();
        idea.setNombreIdea(nombreIdea);
        idea.setDescripcion(descripcionSolucion);
        idea.setSituacionDetectada(descripcionProblema);
        idea.setProponentes(proponentesGestionados); // Usar la lista gestionada
        idea.setFechaCreacion(new Date(System.currentTimeMillis()));
        idea.setEstado("Pendiente");
        idea.setEstadoImplementada(false);
        // Recuperar el objeto proponente de la sesión y convertirlo a su tipo correcto
        Proponente proponente = (Proponente) session.getAttribute("proponente");

        // Verificar que no sea null y obtener el nombre
        if (proponente != null) {
            idea.setResponsable(proponente.getNombre());
        } else {
            // Manejo del caso donde no haya proponente en la sesión
            idea.setResponsable("Desconocido");
        }
        idea.setCalificacion(0);
        idea.setComentario("");
        idea.setEstadoCalificacion("Pendiente");
        idea.setFechaAprobacion(null);

        // Llamar al servicio para guardar la idea
        ideaService.save(idea);

        System.out.println("Se ha guardado la idea: " + idea.getNombreIdea());

        // Verificar y mostrar los proponentes asociados
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
    public String subirProponentes(Model model) {
        model.addAttribute("proponentes", proponenteService.findAll());
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
