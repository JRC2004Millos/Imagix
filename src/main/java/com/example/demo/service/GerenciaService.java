package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.model.Gerencia;

@Service
public interface GerenciaService {

    public Gerencia findById(Long id);

    public Gerencia findByNombre(String nombre);

    public List<Gerencia> findAll();

}
