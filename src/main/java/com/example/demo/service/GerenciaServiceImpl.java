package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Gerencia;
import com.example.demo.repository.GerenciaRepository;

@Service
public class GerenciaServiceImpl implements GerenciaService {

    @Autowired
    private GerenciaRepository gerenciaRepository;

    @Override
    public Gerencia findById(Long id) {
        return gerenciaRepository.findById(id).get();
    }

    @Override
    public List<Gerencia> findAll() {
        return gerenciaRepository.findAll();
    }

    @Override
    public Gerencia findByNombre(String nombre) {
        return gerenciaRepository.findByNombre(nombre);
    }

}
