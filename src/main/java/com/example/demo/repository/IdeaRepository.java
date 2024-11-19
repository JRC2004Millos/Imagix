package com.example.demo.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Gerencia;
import com.example.demo.model.Idea;

@Repository
public interface IdeaRepository extends JpaRepository<Idea, Long> {
    public List<Idea> findByNombreIdeaContainingIgnoreCase(String search);

    List<Idea> findByGerenciaAndEstado(Gerencia gerencia, String estado);

    List<Idea> findByGerenciaIdAndEstado(Long gerenciaId, String estado);

    @Query("SELECT i FROM Idea i JOIN i.proponentes p WHERE p.id = :proponenteId")
    List<Idea> findIdeasByProponenteId(@Param("proponenteId") Long proponenteId);

    List<Idea> findByFechaAprobacionIsNullAndGerenciaId(Long gerenciaId);

    List<Idea> findByGerenciaId(Long gerenciaId);

    boolean existsByNombreIdeaAndGerencia(String nombreIdea, Gerencia gerencia);
    // Método para encontrar ideas entre dos fechas
    List<Idea> findByFechaCreacionBetween(Date startDate, Date endDate);

}
