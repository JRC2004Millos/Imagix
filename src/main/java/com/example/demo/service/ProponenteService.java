package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.model.Proponente;

@Service
public interface ProponenteService {

    public Proponente findById(Long id);

    public Proponente findByEmail(String email);

    public List<Proponente> findAll();
}
