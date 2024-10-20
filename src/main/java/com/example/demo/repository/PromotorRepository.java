package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Promotor;

@Repository
public interface PromotorRepository extends JpaRepository<Promotor, Long> {
    Promotor findByEmail(String correo);
}
