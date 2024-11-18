package com.example.demo.service;

import com.example.demo.model.Admin;

public interface AdminService {
    Admin findById(Long id);

    Admin findByUsuario(String usuario);
}
