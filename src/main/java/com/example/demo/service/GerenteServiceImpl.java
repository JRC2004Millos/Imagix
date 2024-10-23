package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Gerente;
import com.example.demo.model.Idea;
import com.example.demo.repository.GerenteRepository;
import com.example.demo.repository.IdeaRepository;

import java.util.List;

@Service
public class GerenteServiceImpl implements GerenteService {

    @Autowired
    private GerenteRepository gerenteRepository;

    @Autowired
    private IdeaRepository ideaRepository;

    @Override
    public Gerente findById(Long id) {
        return gerenteRepository.findById(id).orElse(null);
    }

    @Override
    public Gerente findByEmail(String email) {
        return gerenteRepository.findByEmail(email);
    }

    

    @Override
    public void aprobarIdea(Long ideaId, Long gerenteId) {
        Idea idea = ideaRepository.findById(ideaId).orElse(null);
        if (idea != null && idea.getEstado().equals("En revisión por gerente")) {
            idea.setEstado("Aprobada");
            ideaRepository.save(idea);
        }
    }
    

  
    public void rechazarIdea(Long ideaId, Long gerenteId) {
        Idea idea = ideaRepository.findById(ideaId).orElse(null);
        if (idea != null && idea.getEstado().equals("En revisión por gerente")) {
            idea.setEstado("Rechazada");
            ideaRepository.save(idea);
        }
    }
   
}
