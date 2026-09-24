package com.clubdeportivo.model;

import com.clubdeportivo.model.enums.Parentesco;
import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * ========================================================================================
 * ENTIDAD JPA: Familiar (Miembro del Grupo Familiar)
 * ========================================================================================
 * Modela a cada integrante dependiente que conforma el grupo familiar de un socio titular.
 * Posee su propia identificación (DNI), fotografía del rostro para control de acceso,
 * y se beneficia de la cuota social abonada por el titular.
 *
 * Anotaciones utilizadas:
 * - @ManyToOne(fetch = FetchType.LAZY):
 *   Relación muchos-a-uno con la entidad Socio.
 *   FetchType.LAZY difiere la carga del socio titular en memoria hasta que sea explícitamente
 *   requerido por el código, optimizando sensiblemente las consultas SQL (evita el problema N+1).
 * - @JoinColumn(name = "socio_id", nullable = false):
 *   Define la columna física que actúa como Clave Foránea (Foreign Key) hacia la tabla "socios".
 * ========================================================================================
 */
@Entity
@Table(name = "familiares", indexes = {
        @Index(name = "idx_familiar_dni", columnList = "dni", unique = true),
        @Index(name = "idx_familiar_socio", columnList = "socio_id")
})
public class Familiar extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Socio titular del cual depende este familiar.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "socio_id", nullable = false, foreignKey = @ForeignKey(name = "fk_familiar_socio"))
    private Socio socio;

    @Column(name = "dni", nullable = false, length = 15, unique = true)
    private String dni;

    @Column(name = "nombre", nullable = false, length = 80)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 80)
    private String apellido;

    @Enumerated(EnumType.STRING)
    @Column(name = "parentesco", nullable = false, length = 30)
    private Parentesco parentesco;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    /**
     * Fotografía facial para control de accesos perimetrales.
     */
    @Column(name = "foto_rostro", length = 255)
    private String fotoRostro;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    // ====================================================================================
    // CONSTRUCTORES
    // ====================================================================================

    public Familiar() {
        this.activo = true;
    }

    public Familiar(Socio socio, String dni, String nombre, String apellido, Parentesco parentesco, LocalDate fechaNacimiento) {
        this.socio = socio;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.parentesco = parentesco;
        this.fechaNacimiento = fechaNacimiento;
        this.activo = true;
    }

    // ====================================================================================
    // MÉTODOS DE CONVENIENCIA
    // ====================================================================================

    public String getNombreCompleto() {
        return (apellido != null ? apellido.toUpperCase() : "") + ", " + (nombre != null ? nombre : "");
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

    public Socio getSocio() {
        return socio;
    }

    public void setSocio(Socio socio) {
        this.socio = socio;
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

    public Parentesco getParentesco() {
        return parentesco;
    }

    public void setParentesco(Parentesco parentesco) {
        this.parentesco = parentesco;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
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
