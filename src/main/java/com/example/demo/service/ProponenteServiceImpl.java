package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Proponente;
import com.example.demo.repository.PromotorRepository;
import com.example.demo.repository.ProponenteRepository;

@Service
public class ProponenteServiceImpl implements ProponenteService{
    @Autowired
    private ProponenteRepository proponenteRepository;

    @Override
    public Proponente findById(Long id) {
        return proponenteRepository.findById(id).get();
    }
    @Override
    public Proponente findByEmail(String correo){
        return proponenteRepository.findByEmail(correo);
    }
}
