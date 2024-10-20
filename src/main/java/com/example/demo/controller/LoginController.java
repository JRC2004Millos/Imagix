package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Promotor;
import com.example.demo.model.Proponente;
import com.example.demo.service.PromotorService;
import com.example.demo.service.ProponenteService;

import jakarta.servlet.http.HttpSession;

import java.util.List;


@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private PromotorService promotorService;
    @Autowired
    private ProponenteService proponenteService;

    @GetMapping
    public String login(){
       return "login";
    }

    @PostMapping("confirmacion")
    public String sesionIniciada(@RequestParam("email") String email, @RequestParam("password") String password,HttpSession session, Model model) {
        try {
            Promotor promotor = promotorService.findByEmail(email);
            if (promotor != null) {
                if (promotor.getClave().equals(password)) {
                    session.setAttribute("promotor", promotor);
                    return "redirect:/promotor/dashboard";
                } else {
                    model.addAttribute("errorMessage", "Contraseña incorrecta.");
                    return "redirect:/login";
                }
            } 
            Proponente proponente = proponenteService.findByEmail(email);
            if (proponente != null) {
                if (proponente.getClave().equals(password)) {
                    session.setAttribute("proponente", proponente);
                    return "redirect:/proponente/dashboard"; 
                } else {
                    model.addAttribute("errorMessage", "Contraseña incorrecta.");
                    return "redirect:/login";
                }
            }
            model.addAttribute("errorMessage", "Usuario no encontrado.");
            return "redirect:/login";
        } catch (NullPointerException e) {
            model.addAttribute("errorMessage", "Usuario no encontrado.");
            return "redirect:/login";
        }
    }

}
