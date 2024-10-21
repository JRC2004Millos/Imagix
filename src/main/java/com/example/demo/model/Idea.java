package com.example.demo.model;

import java.sql.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Idea {

    @Id
    @GeneratedValue
    Long id;

    String estado;
    String responsable;
    Date fechaCreacion;
    Date fechaAprobacion;
    String nombreIdea;
    String situacionDetectada;
    String descripcion;
    Boolean estadoImplementada;
    float calificacion;
    String estadoCalificacion;
    String comentario;

    @ManyToOne
    @JoinColumn(name = "gerencia_id")
    private Gerencia gerencia;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<Proponente> proponentes;

    public Idea() {
    }

    public Idea(String estado, String responsable, Date fechaCreacion, Date fechaAprobacion,
            String nombreIdea, String situacionDetectada, String descripcion, Boolean estadoImplementada,
            float calificacion, String estadoCalificacion, String comentario, Gerencia gerencia,
            List<Proponente> proponentes) {
        this.estado = estado;
        this.responsable = responsable;
        this.fechaCreacion = fechaCreacion;
        this.fechaAprobacion = fechaAprobacion;
        this.nombreIdea = nombreIdea;
        this.situacionDetectada = situacionDetectada;
        this.descripcion = descripcion;
        this.estadoImplementada = estadoImplementada;
        this.calificacion = calificacion;
        this.estadoCalificacion = estadoCalificacion;
        this.comentario = comentario;
        this.gerencia = gerencia;
        this.proponentes = proponentes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Date getFechaAprobacion() {
        return fechaAprobacion;
    }

    public void setFechaAprobacion(Date fechaAprobacion) {
        this.fechaAprobacion = fechaAprobacion;
    }

    public String getNombreIdea() {
        return nombreIdea;
    }

    public void setNombreIdea(String nombreIdea) {
        this.nombreIdea = nombreIdea;
    }

    public String getSituacionDetectada() {
        return situacionDetectada;
    }

    public void setSituacionDetectada(String situacionDetectada) {
        this.situacionDetectada = situacionDetectada;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getEstadoImplementada() {
        return estadoImplementada;
    }

    public void setEstadoImplementada(Boolean estadoImplementada) {
        this.estadoImplementada = estadoImplementada;
    }

    public float getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(float calificacion) {
        this.calificacion = calificacion;
    }

    public String getEstadoCalificacion() {
        return estadoCalificacion;
    }

    public void setEstadoCalificacion(String estadoCalificacion) {
        this.estadoCalificacion = estadoCalificacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Gerencia getGerencia() {
        return gerencia;
    }

    public void setGerencia(Gerencia gerencia) {
        this.gerencia = gerencia;
    }

    public List<Proponente> getProponentes() {
        return proponentes;
    }

    public void setProponentes(List<Proponente> proponentes) {
        this.proponentes = proponentes;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

}
