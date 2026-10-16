package com.colegio.model.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.colegio.model.enums.Rol;
import com.colegio.model.enums.Sexo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * =============================================================================
 * ENTIDAD: Docente
 * =============================================================================
 * Representa tanto al profesor del colegio como al USUARIO que inicia sesión
 * en el sistema (requisito del enunciado). El nombre de usuario ES el correo
 * personal ("correo" tiene columna UNIQUE y es el "username" que usa Spring
 * Security -> ver security.DocenteUserDetails y security.CustomUserDetailsService).
 *
 * @Entity + @Table: marca la clase como entidad JPA/Hibernate y fija el
 *   nombre de la tabla ("docentes") -> mapeo objeto-relacional (ORM): cada
 *   instancia de esta clase Java es una fila de esa tabla, sin escribir SQL.
 *
 * La contraseña NUNCA se guarda en texto plano: "passwordHash" almacena el
 * resultado de BCryptPasswordEncoder (ver security.SecurityConfig).
 * =============================================================================
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"passwordHash", "asignaciones"}) // nunca loguear el hash ni cargar colecciones perezosas al loguear
@EqualsAndHashCode(callSuper = false, of = "id")
@Entity
@Table(name = "docentes")
public class Docente extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Enumerated(EnumType.STRING) // guarda el texto "MASCULINO"/"FEMENINO", no el índice numérico
    @Column(nullable = false, length = 20)
    private Sexo sexo;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    /** Username de acceso: correo personal del docente. Único en toda la tabla. */
    @Column(nullable = false, unique = true, length = 150)
    private String correo;

    /** BCrypt hash de la contraseña (nunca texto plano). */
    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Rol rol = Rol.DOCENTE;

    /** Habilita/inhabilita el acceso sin borrar al docente (baja lógica). */
    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;

    /**
     * Materias que dicta este docente, en qué aula (relación inversa 1:N).
     * mappedBy="docente" -> la relación "dueña" (con la FK) vive en Asignacion.
     * FetchType por defecto de @OneToMany es LAZY: no se trae de la base de
     * datos hasta que se accede explícitamente (evita cargar de más).
     */
    @OneToMany(mappedBy = "docente")
    @Builder.Default
    private List<Asignacion> asignaciones = new ArrayList<>();
}
