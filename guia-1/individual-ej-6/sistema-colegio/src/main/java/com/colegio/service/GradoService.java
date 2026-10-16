package com.colegio.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.colegio.dto.GradoRequest;
import com.colegio.dto.GradoResponse;
import com.colegio.exception.RecursoNoEncontradoException;
import com.colegio.exception.RegistroDuplicadoException;
import com.colegio.mapper.GradoMapper;
import com.colegio.model.entity.Grado;
import com.colegio.repository.GradoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GradoService {

    private final GradoRepository gradoRepository;
    private final GradoMapper gradoMapper;

    @Transactional(readOnly = true)
    public List<GradoResponse> listar() {
        return gradoRepository.findAll().stream().map(gradoMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public GradoResponse obtener(Long id) {
        return gradoMapper.toResponse(buscarOFallar(id));
    }

    @Transactional
    public GradoResponse crear(GradoRequest request) {
        if (gradoRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new RegistroDuplicadoException("Ya existe un grado llamado \"" + request.nombre() + "\"");
        }
        Grado grado = Grado.builder().nombre(request.nombre()).nivel(request.nivel()).build();
        return gradoMapper.toResponse(gradoRepository.save(grado));
    }

    @Transactional
    public GradoResponse actualizar(Long id, GradoRequest request) {
        Grado grado = buscarOFallar(id);
        grado.setNombre(request.nombre());
        grado.setNivel(request.nivel());
        return gradoMapper.toResponse(gradoRepository.save(grado));
    }

    @Transactional
    public void eliminar(Long id) {
        gradoRepository.delete(buscarOFallar(id));
    }

    private Grado buscarOFallar(Long id) {
        return gradoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el grado con id " + id));
    }
}
