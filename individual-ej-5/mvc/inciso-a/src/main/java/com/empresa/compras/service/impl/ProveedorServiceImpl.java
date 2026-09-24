package com.empresa.compras.service.impl;

import com.empresa.compras.dto.ProveedorDTO;
import com.empresa.compras.entity.Proveedor;
import com.empresa.compras.exception.BusinessException;
import com.empresa.compras.exception.ResourceNotFoundException;
import com.empresa.compras.mapper.ProveedorMapper;
import com.empresa.compras.repository.ProveedorRepository;
import com.empresa.compras.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ProveedorMapper proveedorMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorDTO> listarTodos() {
        return proveedorRepository.findAll().stream()
                .map(proveedorMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorDTO> listarActivos() {
        return proveedorRepository.findByActivoTrue().stream()
                .map(proveedorMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorDTO buscarPorId(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el proveedor con id " + id));
        return proveedorMapper.toDTO(proveedor);
    }

    @Override
    public ProveedorDTO guardar(ProveedorDTO dto) {
        // --- Validaciones de negocio ---
        boolean esAlta = dto.getId() == null;

        if (dto.getCuit() == null || !dto.getCuit().matches("\\d{2}-?\\d{8}-?\\d{1}")) {
            throw new BusinessException("El CUIT debe tener el formato XX-XXXXXXXX-X");
        }

        proveedorRepository.findByCuit(dto.getCuit()).ifPresent(existente -> {
            if (esAlta || !existente.getId().equals(dto.getId())) {
                throw new BusinessException("Ya existe un proveedor registrado con el CUIT " + dto.getCuit());
            }
        });

        Proveedor proveedor = proveedorMapper.toEntity(dto);
        if (esAlta) {
            proveedor.setActivo(true);
        }
        proveedor = proveedorRepository.save(proveedor);
        return proveedorMapper.toDTO(proveedor);
    }

    @Override
    public void eliminar(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el proveedor con id " + id));
        if (!proveedor.getOrdenesCompra().isEmpty()) {
            // Regla de negocio: no se borra fisicamente un proveedor con historial de compras;
            // se da de baja logica para preservar la trazabilidad de las ordenes ya emitidas.
            proveedor.setActivo(false);
            proveedorRepository.save(proveedor);
            return;
        }
        proveedorRepository.delete(proveedor);
    }
}
