package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Gerencia;
import com.example.demo.model.Idea;

@Repository
public interface IdeaRepository extends JpaRepository<Idea, Long> {
    public List<Idea> findByNombreIdeaContainingIgnoreCase(String search);

    List<Idea> findByGerenciaAndEstado(Gerencia gerencia, String estado);

    List<Idea> findByGerenciaIdAndEstado(Long gerenciaId, String estado);

}
