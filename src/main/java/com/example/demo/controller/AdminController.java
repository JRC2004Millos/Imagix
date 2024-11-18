package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.model.Admin;
import com.example.demo.service.AdminService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // Lista de ideas del admin
    @GetMapping
    public String listaIdeas(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/login";  // Redirige si no hay sesión activa
        }
        model.addAttribute("admin", admin);  // Pasar el admin al modelo
        return "inicialAdmin";  // Mostrar la vista listaIdeas.html
    }
}
