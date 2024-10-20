package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.service.PromotorService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/promotor")
public class PromotorController {

    @Autowired
    private PromotorService promotorService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        if (session.getAttribute("email") == null) {
            return "redirect:/login";
        }
        return "promotor/dashboard";
    }

}