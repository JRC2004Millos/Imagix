package com.example.demo.service;

import java.util.List;

import com.example.demo.model.Gerente;
import com.example.demo.model.Idea;

public interface GerenteService {

    Gerente findById(Long id);

    Gerente findByEmail(String email);
    
    List<Idea> findByGerenteId(Long gerenteId);

    void aprobarIdea(Long ideaId, Long gerenteId);
}
