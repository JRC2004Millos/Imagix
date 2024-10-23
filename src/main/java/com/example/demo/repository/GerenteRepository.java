package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Gerente;

@Repository
public interface GerenteRepository extends JpaRepository<Gerente, Long> {
    Gerente findByEmail(String email);
}
