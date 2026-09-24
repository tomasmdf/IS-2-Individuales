package com.empresa.compras.entity;

import com.empresa.compras.enums.RolUsuario;
import jakarta.persistence.*;
import lombok.*;

/**
 * ============================================================================
 * Entity: Usuario
 * ============================================================================
 * Representa a las personas que acceden al sistema mediante usuario y
 * contraseña (requisito de autenticacion del enunciado).
 *
 * Anotaciones JPA (ORM):
 *  @Entity                -> le indica a Hibernate que esta clase se mapea
 *                             a una tabla de la base de datos.
 *  @Table(name="usuarios") -> nombre explicito de la tabla.
 *  @Id / @GeneratedValue   -> clave primaria autoincremental.
 *  @Column                 -> restricciones de columna (unico, obligatorio, longitud).
 *  @Enumerated(EnumType.STRING) -> persiste el enum como texto legible
 *                             ("ADMIN"/"OPERADOR") en lugar de un indice numerico.
 *
 * La contraseña NUNCA se guarda en texto plano: se encripta con BCrypt en
 * la capa de Service (UsuarioServiceImpl) antes de persistir.
 * ============================================================================
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    /** Hash BCrypt de la contraseña (nunca texto plano). */
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "nombre_completo", nullable = false, length = 120)
    private String nombreCompleto;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false, length = 20)
    private RolUsuario rol;

    /** Baja logica: un usuario inactivo no puede iniciar sesion. */
    @Column(name = "activo", nullable = false)
    private boolean activo;
}
