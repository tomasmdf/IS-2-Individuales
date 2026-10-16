package com.clubdeportivo.model;

import com.clubdeportivo.model.enums.EstadoCuota;
import com.clubdeportivo.model.enums.TipoAcceso;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * ========================================================================================
 * ENTIDAD JPA: RegistroAcceso
 * ========================================================================================
 * Registra cada evento de ingreso (entrada) o egreso (salida) a las instalaciones del club.
 *
 * Requerimiento de consigna:
 * "Al ingresar al club el sistema registra el horario de entrada, lo mismo sucede en caso de salida.
 * El sistema guarda además de los datos principales una imagen con el rostro de cada persona."
 *
 * Anotaciones utilizadas:
 * - @Entity & @Table: Mapeo de la tabla "registros_acceso" con índices por fecha y DNI para
 *   optimizar las consultas de reportería y validación perimetral en tiempo real.
 * - @Enumerated(EnumType.STRING): Almacenamiento seguro del tipo de acceso (ENTRADA / SALIDA)
 *   y del estado financiero de la cuota al momento del evento (AL_DIA, ADEUDA, etc.).
 * ========================================================================================
 */
@Entity
@Table(name = "registros_acceso", indexes = {
        @Index(name = "idx_acceso_fecha_hora", columnList = "fecha_hora"),
        @Index(name = "idx_acceso_dni", columnList = "dni_persona"),
        @Index(name = "idx_acceso_socio", columnList = "socio_id")
})
public class RegistroAcceso extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_acceso", nullable = false, length = 20)
    private TipoAcceso tipoAcceso;

    @Column(name = "dni_persona", nullable = false, length = 15)
    private String dniPersona;

    @Column(name = "nombre_completo_persona", nullable = false, length = 150)
    private String nombreCompletoPersona;

    /**
     * Fotografía del rostro de la persona asociada a este acceso para cotejo visual del guardia.
     */
    @Column(name = "foto_rostro", length = 255)
    private String fotoRostro;

    @Column(name = "es_socio_titular", nullable = false)
    private boolean esSocioTitular;

    @Column(name = "socio_id", nullable = false)
    private Long socioId;

    @Column(name = "familiar_id")
    private Long familiarId;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_cuota_momento", nullable = false, length = 30)
    private EstadoCuota estadoCuotaMomento;

    @Column(name = "punto_acceso", length = 80)
    private String puntoAcceso;

    @Column(name = "observaciones", length = 255)
    private String observaciones;

    // ====================================================================================
    // CONSTRUCTORES
    // ====================================================================================

    public RegistroAcceso() {
        this.fechaHora = LocalDateTime.now();
        this.puntoAcceso = "Molinete Principal";
    }

    public RegistroAcceso(TipoAcceso tipoAcceso, String dniPersona, String nombreCompletoPersona,
                          String fotoRostro, boolean esSocioTitular, Long socioId, Long familiarId,
                          EstadoCuota estadoCuotaMomento, String puntoAcceso, String observaciones) {
        this.fechaHora = LocalDateTime.now();
        this.tipoAcceso = tipoAcceso;
        this.dniPersona = dniPersona;
        this.nombreCompletoPersona = nombreCompletoPersona;
        this.fotoRostro = fotoRostro;
        this.esSocioTitular = esSocioTitular;
        this.socioId = socioId;
        this.familiarId = familiarId;
        this.estadoCuotaMomento = estadoCuotaMomento;
        this.puntoAcceso = (puntoAcceso != null ? puntoAcceso : "Molinete Principal");
        this.observaciones = observaciones;
    }

    // ====================================================================================
    // GETTERS Y SETTERS
    // ====================================================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public TipoAcceso getTipoAcceso() {
        return tipoAcceso;
    }

    public void setTipoAcceso(TipoAcceso tipoAcceso) {
        this.tipoAcceso = tipoAcceso;
    }

    public String getDniPersona() {
        return dniPersona;
    }

    public void setDniPersona(String dniPersona) {
        this.dniPersona = dniPersona;
    }

    public String getNombreCompletoPersona() {
        return nombreCompletoPersona;
    }

    public void setNombreCompletoPersona(String nombreCompletoPersona) {
        this.nombreCompletoPersona = nombreCompletoPersona;
    }

    public String getFotoRostro() {
        return fotoRostro;
    }

    public void setFotoRostro(String fotoRostro) {
        this.fotoRostro = fotoRostro;
    }

    public boolean isEsSocioTitular() {
        return esSocioTitular;
    }

    public void setEsSocioTitular(boolean esSocioTitular) {
        this.esSocioTitular = esSocioTitular;
    }

    public Long getSocioId() {
        return socioId;
    }

    public void setSocioId(Long socioId) {
        this.socioId = socioId;
    }

    public Long getFamiliarId() {
        return familiarId;
    }

    public void setFamiliarId(Long familiarId) {
        this.familiarId = familiarId;
    }

    public EstadoCuota getEstadoCuotaMomento() {
        return estadoCuotaMomento;
    }

    public void setEstadoCuotaMomento(EstadoCuota estadoCuotaMomento) {
        this.estadoCuotaMomento = estadoCuotaMomento;
    }

    public String getPuntoAcceso() {
        return puntoAcceso;
    }

    public void setPuntoAcceso(String puntoAcceso) {
        this.puntoAcceso = puntoAcceso;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
