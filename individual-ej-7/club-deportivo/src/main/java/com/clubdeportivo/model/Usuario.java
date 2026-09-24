package com.clubdeportivo.model;

import com.clubdeportivo.model.enums.Rol;
import jakarta.persistence.*;

/**
 * ========================================================================================
 * ENTIDAD JPA: Usuario
 * ========================================================================================
 * Modela los usuarios con credenciales para iniciar sesión en la plataforma web.
 *
 * Anotaciones utilizadas:
 * - @Entity: Declara a esta clase como una entidad persistible administrada por Hibernate/JPA.
 * - @Table: Configura la tabla física en la base de datos relacional ("usuarios") y sus restricciones.
 * - @Id: Declara la clave primaria (Primary Key) de la entidad.
 * - @GeneratedValue(strategy = GenerationType.IDENTITY): Delega la generación del ID autoincremental
 *   a la columna AUTO_INCREMENT nativa del motor MySQL.
 * - @Enumerated(EnumType.STRING): Persiste el nombre del Enum como texto legible ('ROLE_ADMIN')
 *   en lugar de un ordinal numérico, evitando desincronizaciones si cambia el orden en el código.
 * ========================================================================================
 */
@Entity
@Table(name = "usuarios", uniqueConstraints = {
        @UniqueConstraint(name = "uk_usuario_username", columnNames = "username"),
        @UniqueConstraint(name = "uk_usuario_email", columnNames = "email")
})
public class Usuario extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    /**
     * Hash de la contraseña almacenado de forma segura mediante BCrypt.
     */
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "nombre_completo", nullable = false, length = 120)
    private String nombreCompleto;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false, length = 30)
    private Rol rol;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    // ====================================================================================
    // CONSTRUCTORES
    // ====================================================================================

    public Usuario() {
    }

    public Usuario(String username, String password, String nombreCompleto, String email, Rol rol) {
        this.username = username;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.rol = rol;
        this.activo = true;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
