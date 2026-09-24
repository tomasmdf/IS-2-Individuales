package com.ejercicio.sistemaregistro.repository;

import com.ejercicio.sistemaregistro.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * =============================================================================
 * REPOSITORIO "UsuarioRepository" — CAPA DE ACCESO A DATOS (parte del Modelo/ORM)
 * =============================================================================
 * Esta interfaz es el corazón del ORM en Spring Data JPA.
 *
 * Al extender JpaRepository<Usuario, Long>:
 *   - "Usuario" es el tipo de la entidad que administra este repositorio.
 *   - "Long"    es el tipo de dato de la clave primaria (id) de esa entidad.
 *
 * Spring Data JPA, en tiempo de ejecución, genera automáticamente una
 * implementación de esta interfaz (no hace falta escribir código SQL ni
 * clases de implementación) que ya provee métodos como:
 *   - save(usuario)        -> INSERT o UPDATE según corresponda
 *   - findById(id)         -> SELECT ... WHERE id = ?
 *   - findAll()            -> SELECT * FROM usuarios
 *   - deleteById(id)       -> DELETE ... WHERE id = ?
 *   - existsById(id)       -> valida existencia
 *
 * Además, gracias a la convención de nombres de Spring Data JPA ("Query
 * Methods"), alcanza con DECLARAR la firma del método para que Spring
 * genere automáticamente la consulta SQL correspondiente, interpretando
 * el nombre del método:
 *
 *   findByCorreoPersonal(String correoPersonal)
 *     -> SELECT * FROM usuarios WHERE correo_personal = ?
 *
 *   existsByCorreoPersonal(String correoPersonal)
 *     -> SELECT COUNT(*) > 0 FROM usuarios WHERE correo_personal = ?
 *
 *   existsByDocumento(String documento)
 *     -> SELECT COUNT(*) > 0 FROM usuarios WHERE documento = ?
 *
 * Esta interfaz NO lleva la anotación @Repository de forma obligatoria
 * porque Spring Data JPA ya sabe, por herencia de JpaRepository, que debe
 * crear un bean de tipo "repositorio" y traducir sus excepciones de
 * persistencia automáticamente.
 * =============================================================================
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por su correo personal, que es el "usuario" (login)
     * del sistema según el enunciado. Devuelve Optional<Usuario> para
     * manejar de forma segura el caso en que no exista (evita NullPointerException).
     */
    Optional<Usuario> findByCorreoPersonal(String correoPersonal);

    /** Verifica si ya existe un usuario registrado con ese correo. */
    boolean existsByCorreoPersonal(String correoPersonal);

    /** Verifica si ya existe un usuario registrado con ese documento. */
    boolean existsByDocumento(String documento);
}
