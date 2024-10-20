package com.example.demo.service;

import org.hibernate.annotations.DialectOverride.OverridesAnnotation;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.model.Promotor;
import com.example.demo.model.Regional;
import com.example.demo.repository.PromotorRepository;

@Service
public class PromotorServiceImpl implements PromotorService{
    @Autowired
    private PromotorRepository promotorRepository;

    @Override
    public Promotor findById(Long id) {
        return promotorRepository.findById(id).get();
    }
    @Override
    public Promotor findByEmail(String email){
        return promotorRepository.findByEmail(email);
    }
}
