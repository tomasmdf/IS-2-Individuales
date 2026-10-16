package com.colegio.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.colegio.dto.MateriaRequest;
import com.colegio.dto.MateriaResponse;
import com.colegio.exception.RecursoNoEncontradoException;
import com.colegio.exception.RegistroDuplicadoException;
import com.colegio.mapper.MateriaMapper;
import com.colegio.model.entity.Materia;
import com.colegio.repository.MateriaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MateriaService {

    private final MateriaRepository materiaRepository;
    private final MateriaMapper materiaMapper;

    @Transactional(readOnly = true)
    public List<MateriaResponse> listar() {
        return materiaRepository.findAll().stream().map(materiaMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MateriaResponse obtener(Long id) {
        return materiaMapper.toResponse(buscarOFallar(id));
    }

    @Transactional
    public MateriaResponse crear(MateriaRequest request) {
        if (materiaRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new RegistroDuplicadoException("Ya existe la materia \"" + request.nombre() + "\"");
        }
        Materia materia = Materia.builder().nombre(request.nombre()).descripcion(request.descripcion()).build();
        return materiaMapper.toResponse(materiaRepository.save(materia));
    }

    @Transactional
    public MateriaResponse actualizar(Long id, MateriaRequest request) {
        Materia materia = buscarOFallar(id);
        materia.setNombre(request.nombre());
        materia.setDescripcion(request.descripcion());
        return materiaMapper.toResponse(materiaRepository.save(materia));
    }

    @Transactional
    public void eliminar(Long id) {
        materiaRepository.delete(buscarOFallar(id));
    }

    private Materia buscarOFallar(Long id) {
        return materiaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la materia con id " + id));
    }
}
