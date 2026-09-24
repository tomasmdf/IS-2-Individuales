package com.empresa.compras.repository;

import com.empresa.compras.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    Optional<Proveedor> findByCuit(String cuit);

    boolean existsByCuit(String cuit);

    List<Proveedor> findByActivoTrue();
}
