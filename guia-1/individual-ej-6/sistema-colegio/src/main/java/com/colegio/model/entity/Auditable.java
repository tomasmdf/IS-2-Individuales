package com.colegio.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

/**
 * =============================================================================
 * AUDITORÍA DE ENTIDADES (JPA Auditing)
 * =============================================================================
 * @MappedSuperclass: NO es una entidad ni genera su propia tabla; sus campos
 *   se "heredan" e incluyen como columnas en cada entidad hija (Docente,
 *   Alumno, Grado, Aula, Materia, Asignacion, Nota extienden esta clase).
 *
 * @EntityListeners(AuditingEntityListener.class): registra un "listener" de
 *   ciclo de vida de JPA que intercepta los eventos @PrePersist (antes del
 *   INSERT) y @PreUpdate (antes del UPDATE) para completar automáticamente
 *   estos 4 campos, sin que el programador tenga que asignarlos a mano.
 *
 *   - @CreatedDate     -> fecha/hora del INSERT.
 *   - @LastModifiedDate-> fecha/hora del último UPDATE.
 *   - @CreatedBy       -> usuario (correo) que creó el registro.
 *   - @LastModifiedBy  -> usuario (correo) que hizo el último cambio.
 *
 * El "quién" (CreatedBy/LastModifiedBy) lo resuelve el Bean AuditorAware<String>
 * definido en com.colegio.config.AuditoriaConfig, que consulta el usuario
 * autenticado en el SecurityContext de Spring Security en ese momento.
 * =============================================================================
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable implements Serializable {

    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @CreatedBy
    @Column(name = "creado_por", length = 150, updatable = false)
    private String creadoPor;

    @LastModifiedBy
    @Column(name = "modificado_por", length = 150)
    private String modificadoPor;
}
