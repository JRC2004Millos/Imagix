package com.example.demo.service;

import org.hibernate.annotations.DialectOverride.OverridesAnnotation;
import org.springframework.stereotype.Service;

import com.example.demo.model.Proponente;

@Service
public interface ProponenteService {
    public Proponente findById(Long id);
    public Proponente findByEmail(String email);
}
