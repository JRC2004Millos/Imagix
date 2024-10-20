package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.model.Idea;
import com.example.demo.repository.IdeaRepository;

public class IdeaServiceImpl implements IdeaService {

    @Autowired
    private IdeaRepository ideaRepository;

    @Override
    public Idea save(Idea idea) {
        ideaRepository.save(idea);
        return idea;
    }

    @Override
    public Idea findById(Long id) {
        return ideaRepository.findById(id).get();
    }

    @Override
    public List<Idea> findAll() {
        return ideaRepository.findAll();
    }

}
