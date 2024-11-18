package com.example.demo.service;

import com.example.demo.model.Gerente;

public interface GerenteService {

    Gerente findById(Long id);

    Gerente findByEmail(String email);

    void aprobarIdea(Long ideaId, Long gerenteId);

    void rechazarIdea(Long ideaId, Long gerenteId);
}
