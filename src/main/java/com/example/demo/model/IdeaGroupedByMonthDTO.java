package com.example.demo.model;

public class IdeaGroupedByMonthDTO {
    private int mes;
    private int cantidadIdeas;

    public IdeaGroupedByMonthDTO(int mes, int cantidadIdeas) {
        this.mes = mes;
        this.cantidadIdeas = cantidadIdeas;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getCantidadIdeas() {
        return cantidadIdeas;
    }

    public void setCantidadIdeas(int cantidadIdeas) {
        this.cantidadIdeas = cantidadIdeas;
    }
}

