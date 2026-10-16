package com.clubdeportivo.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * ========================================================================================
 * SUPERCLASE DE AUDITORÍA: AuditableEntity
 * ========================================================================================
 * Proporciona campos de auditoría transversales a todas las entidades de la base de datos:
 * - Quién creó el registro (@CreatedBy)
 * - Cuándo se creó (@CreatedDate)
 * - Quién realizó la última modificación (@LastModifiedBy)
 * - Cuándo se modificó por última vez (@LastModifiedDate)
 *
 * Anotaciones utilizadas:
 * - @MappedSuperclass:
 *   Indica a JPA/Hibernate que esta clase no posee una tabla propia en la base de datos,
 *   sino que sus campos y metadatos se heredarán y mapearán en las tablas de las entidades hijas
 *   (Socio, Familiar, PagoCuota, etc.).
 *
 * - @EntityListeners(AuditingEntityListener.class):
 *   Vincula el interceptor de ciclo de vida de Spring Data JPA. Escucha los eventos
 *   PrePersist y PreUpdate para inyectar automáticamente el usuario autenticado y la fecha/hora.
 *
 * - @CreatedDate y @LastModifiedDate:
 *   Asignan automáticamente marcas temporales en el momento del INSERT y UPDATE respectivamente.
 *
 * - @CreatedBy y @LastModifiedBy:
 *   Asignan el nombre del usuario autenticado obtenido mediante la interfaz AuditorAware.
 * ========================================================================================
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableEntity {

    /**
     * Fecha y hora exacta de creación del registro en el sistema.
     * updatable = false garantiza que una vez persistido, este valor no se sobreescriba en futuros UPDATE.
     */
    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Fecha y hora de la modificación más reciente del registro.
     */
    @LastModifiedDate
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    /**
     * Nombre del usuario autenticado que originó la creación del registro.
     */
    @CreatedBy
    @Column(name = "creado_por", updatable = false, length = 100)
    private String creadoPor;

    /**
     * Nombre del usuario autenticado que realizó la última actualización del registro.
     */
    @LastModifiedBy
    @Column(name = "modificado_por", length = 100)
    private String modificadoPor;

    // ====================================================================================
    // GETTERS Y SETTERS
    // ====================================================================================

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public String getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(String creadoPor) {
        this.creadoPor = creadoPor;
    }

    public String getModificadoPor() {
        return modificadoPor;
    }

    public void setModificadoPor(String modificadoPor) {
        this.modificadoPor = modificadoPor;
    }
}
