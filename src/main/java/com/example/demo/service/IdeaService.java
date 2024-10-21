package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Idea;

@Service
public interface IdeaService {

    @Transactional
    public void save(Idea idea);

    public Idea findById(Long id);

    public List<Idea> findAll();

}
