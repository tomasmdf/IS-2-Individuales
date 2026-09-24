package com.colegio.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.colegio.dto.AulaRequest;
import com.colegio.dto.AulaResponse;
import com.colegio.exception.RecursoNoEncontradoException;
import com.colegio.exception.RegistroDuplicadoException;
import com.colegio.mapper.AulaMapper;
import com.colegio.model.entity.Aula;
import com.colegio.model.entity.Grado;
import com.colegio.repository.AulaRepository;
import com.colegio.repository.GradoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AulaService {

    private final AulaRepository aulaRepository;
    private final GradoRepository gradoRepository;
    private final AulaMapper aulaMapper;

    @Transactional(readOnly = true)
    public List<AulaResponse> listar() {
        return aulaRepository.findAll().stream().map(aulaMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AulaResponse> listarPorGrado(Long gradoId) {
        return aulaRepository.findByGradoIdOrderByDivisionAsc(gradoId).stream().map(aulaMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AulaResponse obtener(Long id) {
        return aulaMapper.toResponse(buscarOFallar(id));
    }

    @Transactional
    public AulaResponse crear(AulaRequest request) {
        Grado grado = gradoRepository.findById(request.gradoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el grado con id " + request.gradoId()));
        if (aulaRepository.existsByGradoIdAndDivisionIgnoreCase(request.gradoId(), request.division())) {
            throw new RegistroDuplicadoException(
                    "El grado \"" + grado.getNombre() + "\" ya tiene un aula \"" + request.division() + "\"");
        }
        Aula aula = Aula.builder().grado(grado).division(request.division()).capacidad(request.capacidad()).build();
        return aulaMapper.toResponse(aulaRepository.save(aula));
    }

    @Transactional
    public AulaResponse actualizar(Long id, AulaRequest request) {
        Aula aula = buscarOFallar(id);
        Grado grado = gradoRepository.findById(request.gradoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el grado con id " + request.gradoId()));
        aula.setGrado(grado);
        aula.setDivision(request.division());
        aula.setCapacidad(request.capacidad());
        return aulaMapper.toResponse(aulaRepository.save(aula));
    }

    @Transactional
    public void eliminar(Long id) {
        aulaRepository.delete(buscarOFallar(id));
    }

    private Aula buscarOFallar(Long id) {
        return aulaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el aula con id " + id));
    }
}
