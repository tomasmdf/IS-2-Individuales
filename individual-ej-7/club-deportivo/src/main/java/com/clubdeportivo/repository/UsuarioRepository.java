package com.clubdeportivo.repository;

import com.clubdeportivo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ========================================================================================
 * REPOSITORIO JPA: UsuarioRepository
 * ========================================================================================
 * Gestiona las operaciones CRUD y consultas derivadas sobre la tabla de usuarios.
 *
 * Anotaciones utilizadas:
 * - @Repository: Marca la interfaz como componente de persistencia dentro del contenedor Spring.
 *   Habilita la traducción automática de excepciones específicas de JDBC/Hibernate a la
 *   jerarquía de excepciones DataAccessException de Spring.
 *
 * - JpaRepository<Usuario, Long>: Provee automáticamente métodos estándar como save(),
 *   findById(), findAll(), deleteById(), count(), con paginación y ordenamiento.
 * ========================================================================================
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Búsqueda por nombre de usuario (utilizada por Spring Security en la autenticación).
     */
    Optional<Usuario> findByUsername(String username);

    /**
     * Verifica existencia para validaciones de alta y edición.
     */
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
