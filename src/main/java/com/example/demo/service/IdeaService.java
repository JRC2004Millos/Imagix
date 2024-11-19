package com.example.demo.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Gerencia;
import com.example.demo.model.Idea;

@Service
public interface IdeaService {

    @Transactional
    public void save(Idea idea);

    public Idea findById(Long id);

    public List<Idea> findAll();

    public List<Idea> findByNombreIdeaContainingIgnoreCase(String search);

    public List<Idea> findIdeasByProponenteId(Long proponenteId);

    public List<Idea> findByFechaAprobacionIsNullAndGerenciaId(Long gerenciaId);

    List<Idea> findByGerenciaAndEstado(Gerencia gerencia, String estado);

    List<Idea> findByGerenciaIdAndEstado(Long gerenciaId, String estado);

    List<Idea> findByGerenciaId(Long gerenciaId);
    // Método para filtrar las ideas entre dos fechas
    public List<Idea> findIdeasBetweenDates(Date startDate, Date endDate);
}
