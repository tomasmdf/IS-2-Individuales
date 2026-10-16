package com.empresa.compras.service.impl;

import com.empresa.compras.dto.CategoriaDTO;
import com.empresa.compras.entity.Categoria;
import com.empresa.compras.exception.BusinessException;
import com.empresa.compras.exception.ResourceNotFoundException;
import com.empresa.compras.mapper.CategoriaMapper;
import com.empresa.compras.repository.CategoriaRepository;
import com.empresa.compras.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================================================
 * CategoriaServiceImpl
 * ============================================================================
 * Implementacion de la capa de SERVICIO para Categoria.
 *
 * @Service   -> marca la clase como un bean de la capa de negocio, gestionado
 *               por el contenedor de Spring (Inyeccion de Dependencias).
 * @Transactional -> cada metodo publico se ejecuta dentro de una transaccion
 *               JPA; si algo falla, Hibernate hace ROLLBACK automaticamente.
 * @RequiredArgsConstructor (Lombok) -> genera el constructor con los campos
 *               "final", que Spring usa para inyectar el Repository y el Mapper
 *               (inyeccion por constructor, la forma recomendada).
 *
 * IMPORTANTE: Aqui es donde viven las VALIDACIONES DE NEGOCIO (tal como pide
 * la consigna), NO en el Controller ni en la Entity.
 * ============================================================================
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDTO> listarTodas() {
        return categoriaRepository.findAll().stream()
                .map(categoriaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaDTO buscarPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la categoria con id " + id));
        return categoriaMapper.toDTO(categoria);
    }

    @Override
    public CategoriaDTO guardar(CategoriaDTO dto) {
        // --- Validaciones de negocio ---
        boolean esAlta = dto.getId() == null;
        categoriaRepository.findByNombreIgnoreCase(dto.getNombre().trim()).ifPresent(existente -> {
            if (esAlta || !existente.getId().equals(dto.getId())) {
                throw new BusinessException("Ya existe una categoria con el nombre '" + dto.getNombre() + "'");
            }
        });

        Categoria categoria = categoriaMapper.toEntity(dto);
        categoria = categoriaRepository.save(categoria);
        return categoriaMapper.toDTO(categoria);
    }

    @Override
    public void eliminar(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la categoria con id " + id));
        if (!categoria.getProductos().isEmpty()) {
            throw new BusinessException("No se puede eliminar la categoria porque tiene productos asociados");
        }
        categoriaRepository.delete(categoria);
    }
}
