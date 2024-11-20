package com.example.demo.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Gerencia;
import com.example.demo.model.Idea;
import com.example.demo.model.IdeaSimilarDTO;
import com.example.demo.model.Proponente;
import com.example.demo.repository.IdeaRepository;

@Service
public class IdeaServiceImpl implements IdeaService {

    private static final Logger log = LoggerFactory.getLogger(IdeaService.class);

    @Autowired
    private IdeaRepository ideaRepository;

    @Override
    @Transactional
    public void save(Idea idea) {
        ideaRepository.save(idea);
    }

    @Override
    public Idea findById(Long id) {
        return ideaRepository.findById(id).get();
    }

    @Override
    public List<Idea> findAll() {
        return ideaRepository.findAll();
    }

    @Override
    public List<Idea> findByNombreIdeaContainingIgnoreCase(String search) {
        return ideaRepository.findByNombreIdeaContainingIgnoreCase(search);
    }

    @Override
    public List<Idea> findByGerenciaAndEstado(Gerencia gerencia, String estado) {
        return ideaRepository.findByGerenciaAndEstado(gerencia, estado);
    }

    @Override
    public List<Idea> findByGerenciaIdAndEstado(Long gerenciaId, String estado) {
        return ideaRepository.findByGerenciaIdAndEstado(gerenciaId, estado);
    }

    @Override
    public List<Idea> findIdeasByProponenteId(Long proponenteId) {
        return ideaRepository.findIdeasByProponenteId(proponenteId);
    }

    @Override
    public List<Idea> findByFechaAprobacionIsNullAndGerenciaId(Long gerenciaId) {
        return ideaRepository.findByFechaAprobacionIsNullAndGerenciaId(gerenciaId);
    }

    @Override
    public List<Idea> findByGerenciaId(Long gerenciaId) {
        return ideaRepository.findByGerenciaId(gerenciaId);
    }

    @Override
    public List<Idea> findIdeasBetweenDates(Date startDate, Date endDate) {
        return ideaRepository.findByFechaCreacionBetween(startDate, endDate);
    }

    @Override
    public List<Idea> findIdeasByProponenteIdAndEstadoImplementadaIsFalse(Long proponenteId) {
        return ideaRepository.findIdeasByProponenteIdAndEstadoImplementadaIsFalse(proponenteId);
    }

    @Override
    public List<Idea> findIdeasByProponenteIdAndEstadoImplementadaIsTrue(Long proponenteId) {
        return ideaRepository.findIdeasByProponenteIdAndEstadoImplementadaIsTrue(proponenteId);
    }

    @Override
    public List<String> findAllDescriptions() {
        return ideaRepository.findAllDescriptions();
    }

    /**
     * Calcula la similitud entre la descripción de una nueva idea y las
     * descripciones existentes.
     *
     * @param descripciones    Lista de descripciones existentes.
     * @param nuevaDescripcion La nueva descripción a comparar.
     * @return El mayor porcentaje de similitud encontrado.
     */
    @Override
    public IdeaSimilarDTO calculateAllSimilarities(List<String> descriptions, String nuevaDescripcion) {
        if (descriptions == null || descriptions.isEmpty() || nuevaDescripcion == null || nuevaDescripcion.isEmpty()) {
            throw new IllegalArgumentException("Las descripciones y la nueva descripción no deben estar vacías.");
        }

        LevenshteinDistance levenshtein = new LevenshteinDistance();
        double highestSimilarity = 0.0;
        int indexMostSimilar = -1;

        for (int i = 0; i < descriptions.size(); i++) {
            String descripcionExistente = descriptions.get(i);

            if (descripcionExistente == null || descripcionExistente.isEmpty()) {
                continue;
            }

            // Calcular distancia y similitud
            int maxLen = Math.max(descripcionExistente.length(), nuevaDescripcion.length());
            int distance = levenshtein.apply(descripcionExistente, nuevaDescripcion);
            double similarity = ((double) (maxLen - distance) / maxLen) * 100;

            if (similarity > highestSimilarity) {
                highestSimilarity = similarity;
                indexMostSimilar = i;
            }
        }

        // Si no se encuentra una descripción similar, retornar null
        if (indexMostSimilar == -1) {
            return null;
        }

        // Recuperar información adicional para la descripción más similar
        Idea ideaMasSimilar = ideaRepository.findByDescripcion(descriptions.get(indexMostSimilar));
        Proponente proponente = ideaMasSimilar.getProponentes().isEmpty()
                ? null
                : ideaMasSimilar.getProponentes().get(0);

        // Crear y retornar el DTO
        return new IdeaSimilarDTO(
                ideaMasSimilar.getNombreIdea(),
                highestSimilarity,
                proponente != null ? proponente.getNombre() : "Desconocido",
                proponente != null ? proponente.getTelefono() : "Sin teléfono",
                proponente != null ? proponente.getEmail() : "Sin correo",
                ideaMasSimilar.getDescripcion());
    }

}
