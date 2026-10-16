package com.ejercicio.sistemaregistro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * =============================================================================
 * ENTIDAD "Usuario" — CAPA MODELO (M de MVC) / MAPEO ORM
 * =============================================================================
 * Esta clase representa la tabla "usuarios" en la base de datos MySQL.
 * Gracias al ORM (Object-Relational Mapping) provisto por JPA/Hibernate,
 * NO escribimos SQL manualmente: Hibernate traduce esta clase Java a
 * sentencias CREATE TABLE / INSERT / UPDATE / SELECT automáticamente.
 *
 * ANOTACIONES JPA UTILIZADAS:
 * ---------------------------------------------------------------------------
 * @Entity            : marca la clase como una entidad gestionada por JPA,
 *                       es decir, un objeto que se persiste en una tabla.
 * @Table(name=...)   : indica el nombre exacto de la tabla en la base.
 * @Id                : marca el campo que es la clave primaria (Primary Key).
 * @GeneratedValue     : indica que el valor del ID lo genera la base de datos
 *                       automáticamente (autoincremental, IDENTITY en MySQL).
 * @Column             : permite configurar detalles de la columna (nombre,
 *                       si admite nulos, si es única, longitud, etc.)
 *
 * ANOTACIONES LOMBOK (reducen código repetitivo/boilerplate):
 * ---------------------------------------------------------------------------
 * @Data               : genera automáticamente getters, setters, toString(),
 *                       equals() y hashCode() para todos los campos.
 * @NoArgsConstructor   : genera un constructor vacío (requerido por JPA/Hibernate,
 *                       ya que instancia las entidades por reflexión).
 * @AllArgsConstructor  : genera un constructor con todos los campos.
 *
 * REGLA DE NEGOCIO DEL ENUNCIADO:
 * ---------------------------------------------------------------------------
 * - El "usuario" del sistema para loguearse es el CORREO PERSONAL (correoPersonal).
 * - Si el usuario se equivoca de clave 3 veces, la cuenta se bloquea
 *   (ver campos intentosFallidos y bloqueado, gestionados desde UsuarioServiceImpl).
 * =============================================================================
 */
@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    /** Clave primaria autogenerada por la base de datos (autoincremental). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre de la persona (dato personal solicitado en el registro). */
    @Column(nullable = false, length = 100)
    private String nombre;

    /** Apellido de la persona (dato personal solicitado en el registro). */
    @Column(nullable = false, length = 100)
    private String apellido;

    /**
     * Documento de identidad. Se marca como único (unique = true) porque
     * no puede haber dos personas registradas con el mismo documento.
     */
    @Column(nullable = false, unique = true, length = 20)
    private String documento;

    /** Fecha de nacimiento de la persona. */
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    /**
     * Correo personal: además de ser un dato de contacto, ES el "usuario"
     * con el que la persona inicia sesión en el sistema (según el enunciado).
     * Por eso se marca como único.
     */
    @Column(name = "correo_personal", nullable = false, unique = true, length = 150)
    private String correoPersonal;

    /**
     * Clave (contraseña) del usuario. NUNCA se guarda en texto plano:
     * se almacena "hasheada" con BCrypt (ver UsuarioServiceImpl), que es
     * un algoritmo de hash unidireccional pensado para contraseñas.
     */
    @Column(nullable = false, length = 200)
    private String clave;

    /**
     * Contador de intentos fallidos de login consecutivos.
     * Cada vez que el usuario ingresa una clave incorrecta se incrementa en 1.
     * Se reinicia a 0 cuando el login es exitoso.
     */
    @Column(name = "intentos_fallidos", nullable = false)
    private Integer intentosFallidos = 0;

    /**
     * Indica si la cuenta está bloqueada. Se pone en "true" automáticamente
     * cuando intentosFallidos llega al máximo permitido (3, ver
     * application.properties -> app.seguridad.max-intentos-fallidos).
     * Un usuario bloqueado no puede iniciar sesión aunque ingrese la clave
     * correcta, hasta que un administrador lo desbloquee (regla típica de
     * este tipo de sistemas).
     */
    @Column(nullable = false)
    private Boolean bloqueado = false;

    /** Fecha y hora en que la persona se registró en el sistema (auditoría). */
    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    /**
     * Método de ciclo de vida de JPA: se ejecuta automáticamente ANTES de
     * que la entidad se inserte por primera vez en la base (@PrePersist).
     * Se usa para completar la fecha de registro sin depender de que el
     * controlador/servicio se acuerde de setearla.
     */
    @PrePersist
    public void prePersist() {
        this.fechaRegistro = LocalDateTime.now();
        if (this.intentosFallidos == null) this.intentosFallidos = 0;
        if (this.bloqueado == null) this.bloqueado = false;
    }
}
