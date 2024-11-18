package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Admin;
import com.example.demo.repository.AdminRepository;

@Service
public class AdminServiceImpl implements AdminService{

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public Admin findById(Long id) {
        return adminRepository.findById(id).orElse(null);
    }

    @Override
    public Admin findByUsuario(String usuario) {
        return adminRepository.findByUsuario(usuario);
    }
    
}
