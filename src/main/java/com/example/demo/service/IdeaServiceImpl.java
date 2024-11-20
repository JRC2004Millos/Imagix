package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Gerencia;
import com.example.demo.model.Idea;
import com.example.demo.repository.IdeaRepository;

@Service
public class IdeaServiceImpl implements IdeaService {

    @Autowired
    private IdeaRepository ideaRepository;

    @Override
    @Transactional
    public void save(Idea idea) {
        ideaRepository.save(idea);
    }

    @Override
    public Idea findById(Long id) {
        return ideaRepository.findById(id).get();
    }

    @Override
    public List<Idea> findAll() {
        return ideaRepository.findAll();
    }

    @Override
    public List<Idea> findByNombreIdeaContainingIgnoreCase(String search) {
        return ideaRepository.findByNombreIdeaContainingIgnoreCase(search);
    }

    @Override
    public List<Idea> findByGerenciaAndEstado(Gerencia gerencia, String estado) {
        return ideaRepository.findByGerenciaAndEstado(gerencia, estado);
    }

    @Override
    public List<Idea> findByGerenciaIdAndEstado(Long gerenciaId, String estado) {
        return ideaRepository.findByGerenciaIdAndEstado(gerenciaId, estado);
    }

    @Override
    public List<Idea> findIdeasByProponenteId(Long proponenteId) {
        return ideaRepository.findIdeasByProponenteId(proponenteId);
    }
}
