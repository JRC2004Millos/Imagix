package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.model.Idea;

@Service
public interface IdeaService {

    Idea save(Idea idea);

    Idea findById(Long id);

    List<Idea> findAll();

}
