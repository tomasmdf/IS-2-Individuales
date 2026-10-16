package com.clubdeportivo.dto;

import com.clubdeportivo.model.enums.Parentesco;
import java.time.LocalDate;
import java.time.Period;

/**
 * ========================================================================================
 * DTO DE RESPUESTA: FamiliarDTO
 * ========================================================================================
 * Transporta los datos de un familiar hacia las vistas Thymeleaf.
 * Desacopla la relación bidireccional con el socio titular para prevenir desbordes de memoria.
 * ========================================================================================
 */
public class FamiliarDTO {

    private Long id;
    private Long socioId;
    private String socioNombreCompleto;
    private String socioDni;
    private String dni;
    private String nombre;
    private String apellido;
    private String nombreCompleto;
    private Parentesco parentesco;
    private String parentescoDescripcion;
    private LocalDate fechaNacimiento;
    private int edad;
    private String fotoRostro;
    private boolean activo;

    public FamiliarDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSocioId() {
        return socioId;
    }

    public void setSocioId(Long socioId) {
        this.socioId = socioId;
    }

    public String getSocioNombreCompleto() {
        return socioNombreCompleto;
    }

    public void setSocioNombreCompleto(String socioNombreCompleto) {
        this.socioNombreCompleto = socioNombreCompleto;
    }

    public String getSocioDni() {
        return socioDni;
    }

    public void setSocioDni(String socioDni) {
        this.socioDni = socioDni;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public Parentesco getParentesco() {
        return parentesco;
    }

    public void setParentesco(Parentesco parentesco) {
        this.parentesco = parentesco;
        this.parentescoDescripcion = (parentesco != null ? parentesco.getDescripcion() : "");
    }

    public String getParentescoDescripcion() {
        return parentescoDescripcion;
    }

    public void setParentescoDescripcion(String parentescoDescripcion) {
        this.parentescoDescripcion = parentescoDescripcion;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
        if (fechaNacimiento != null) {
            this.edad = Period.between(fechaNacimiento, LocalDate.now()).getYears();
        }
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getFotoRostro() {
        return fotoRostro;
    }

    public void setFotoRostro(String fotoRostro) {
        this.fotoRostro = fotoRostro;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
