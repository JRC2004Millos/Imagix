package com.example.demo.model;

public class IdeaSimilarDTO {

    String nombreIdea;
    double similaridad;
    String descripcion;
    String nombreProponente;
    String telefonoProponente;
    String correoProponente;

    public IdeaSimilarDTO(String nombreIdea, double similaridad, String nombreProponente, String telefonoProponente,
            String correoProponente, String descripcion) {
        this.nombreIdea = nombreIdea;
        this.similaridad = similaridad;
        this.nombreProponente = nombreProponente;
        this.telefonoProponente = telefonoProponente;
        this.correoProponente = correoProponente;
        this.descripcion = descripcion;
    }

    public String getNombreIdea() {
        return nombreIdea;
    }

    public void setNombreIdea(String nombreIdea) {
        this.nombreIdea = nombreIdea;
    }

    public double getSimilaridad() {
        return similaridad;
    }

    public void setSimilaridad(double similaridad) {
        this.similaridad = similaridad;
    }

    public String getNombreProponente() {
        return nombreProponente;
    }

    public void setNombreProponente(String nombreProponente) {
        this.nombreProponente = nombreProponente;
    }

    public String getTelefonoProponente() {
        return telefonoProponente;
    }

    public void setTelefonoProponente(String telefonoProponente) {
        this.telefonoProponente = telefonoProponente;
    }

    public String getCorreoProponente() {
        return correoProponente;
    }

    public void setCorreoProponente(String correoProponente) {
        this.correoProponente = correoProponente;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
