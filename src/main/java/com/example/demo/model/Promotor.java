package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Promotor {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String email;

    private String clave;

    private String nivel;

    @ManyToOne
    @JoinColumn(name = "cargo_id")
    private Cargo cargo;

    @ManyToOne
    @JoinColumn(name = "gerencia_id")
    private Gerencia gerencia;

    @ManyToOne
    @JoinColumn(name = "regional_id")
    private Regional regional;

    public Promotor() {
    }

    public Promotor(String name, Cargo cargo, Gerencia gerencia, Regional regional) {
        this.name = name;
        this.cargo = cargo;
        this.gerencia = gerencia;
        this.regional = regional;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public Gerencia getGerencia() {
        return gerencia;
    }

    public void setGerencia(Gerencia gerencia) {
        this.gerencia = gerencia;
    }

    public Regional getRegional() {
        return regional;
    }

    public void setRegional(Regional regional) {
        this.regional = regional;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

}
