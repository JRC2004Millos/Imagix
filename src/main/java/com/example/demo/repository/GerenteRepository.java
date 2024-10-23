package com.example.demo.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Cargo;
import com.example.demo.model.Gerente;
import com.example.demo.model.Regional;
import java.util.List;
@Repository
public interface GerenteRepository extends JpaRepository<Gerente, Long> {
    
     // Encuentra un gerente por su email
    Gerente findByEmail(String email);

    

}
