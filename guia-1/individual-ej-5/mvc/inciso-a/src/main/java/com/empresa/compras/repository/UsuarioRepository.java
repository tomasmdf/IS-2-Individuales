package com.empresa.compras.repository;

import com.empresa.compras.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository (capa de acceso a datos - ORM).
 * Al extender JpaRepository<Usuario, Long> obtenemos automaticamente los
 * metodos CRUD (save, findById, findAll, deleteById, etc.) implementados
 * por Spring Data JPA sobre Hibernate, sin escribir SQL.
 *
 * Los metodos "findByUsername"/"existsByUsername" se generan automaticamente
 * por Spring Data a partir del nombre del metodo (Query Derivation).
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);
}
