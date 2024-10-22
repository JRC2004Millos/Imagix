package com.example.demo.controller;

import com.example.demo.model.Promotor;
import com.example.demo.model.Proponente;
import com.example.demo.service.PromotorService;
import com.example.demo.service.ProponenteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private PromotorService promotorService;

    @Autowired
    private ProponenteService proponenteService;

    @GetMapping
    public String login() {
        return "login";
    }

    @PostMapping("/confirmacion")
    public String sesionIniciada(@RequestParam("email") String email, 
                                 @RequestParam("password") String password,
                                 HttpSession session, Model model) {
        Promotor promotor = promotorService.findByEmail(email);

        if (promotor != null && promotor.getClave().equals(password)) {
            session.setAttribute("promotor", promotor);  // Guardar en sesión
            return "redirect:/promotor";  // Redirigir al promotor
        }

        Proponente proponente = proponenteService.findByEmail(email);
        if (proponente != null && proponente.getClave().equals(password)) {
            session.setAttribute("proponente", proponente);  // Guardar en sesión
            return "redirect:/proponente";  // Redirigir al proponente
        }
        model.addAttribute("errorMessage", "Usuario o contraseña incorrecta.");
        return "login";
    
    }
    
}
