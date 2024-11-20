package com.example.demo.model;

import org.apache.commons.text.similarity.LevenshteinDistance;

public class TextSimilarity {

    public static double calculateSimilarity(String a, String b) {
        if (a == null || b == null) {
            return 0.0; // Manejo de valores nulos
        }

        LevenshteinDistance levenshtein = new LevenshteinDistance();
        int distance = levenshtein.apply(a, b); // Distancia entre textos
        int maxLength = Math.max(a.length(), b.length());

        if (maxLength == 0) {
            return 100.0; // Ambos textos están vacíos, 100% similares
        }

        // Convertir la distancia en un porcentaje de similitud
        return (1 - ((double) distance / maxLength)) * 100;
    }
}
