package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Proponente;

public interface ProponenteRepository extends JpaRepository<Proponente, Long>  {
    Proponente findByEmail(String correo);
    Proponente findByNombre(String nombre);
}
