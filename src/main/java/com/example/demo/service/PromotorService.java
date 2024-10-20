package com.example.demo.service;

import org.hibernate.annotations.DialectOverride.OverridesAnnotation;
import org.springframework.stereotype.Service;

import com.example.demo.model.Promotor;

@Service
public interface PromotorService {
    public Promotor findById(Long id);
    public Promotor findByEmail(String email);
}
