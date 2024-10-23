package com.example.demo.service;

import java.util.List;

import com.example.demo.model.Gerente;
import com.example.demo.model.Idea;

public interface GerenteService {

    Gerente findById(Long id);

    Gerente findByEmail(String email);
    

    void aprobarIdea(Long ideaId, Long gerenteId);
    
    void rechazarIdea(Long ideaId, Long gerenteId);
}
