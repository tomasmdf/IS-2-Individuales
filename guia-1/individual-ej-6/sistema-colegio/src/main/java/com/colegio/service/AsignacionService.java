package com.colegio.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.colegio.dto.AsignacionRequest;
import com.colegio.dto.AsignacionResponse;
import com.colegio.exception.RecursoNoEncontradoException;
import com.colegio.exception.RegistroDuplicadoException;
import com.colegio.mapper.AsignacionMapper;
import com.colegio.model.entity.Asignacion;
import com.colegio.model.entity.Aula;
import com.colegio.model.entity.Docente;
import com.colegio.model.entity.Materia;
import com.colegio.repository.AsignacionRepository;
import com.colegio.repository.AulaRepository;
import com.colegio.repository.DocenteRepository;
import com.colegio.repository.MateriaRepository;

import lombok.RequiredArgsConstructor;

/**
 * Gestiona la "carga horaria": qué docente dicta qué materia en qué aula.
 * Es el ABM que le da sentido a las Notas (ver NotaService).
 */
@Service
@RequiredArgsConstructor
public class AsignacionService {

    private final AsignacionRepository asignacionRepository;
    private final DocenteRepository docenteRepository;
    private final MateriaRepository materiaRepository;
    private final AulaRepository aulaRepository;
    private final AsignacionMapper asignacionMapper;

    @Transactional(readOnly = true)
    public List<AsignacionResponse> listar() {
        return asignacionRepository.findAll().stream().map(asignacionMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AsignacionResponse> listarPorDocente(String correoDocente) {
        return asignacionRepository.findByDocenteCorreoOrderByMateriaNombreAsc(correoDocente)
                .stream().map(asignacionMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AsignacionResponse obtener(Long id) {
        return asignacionMapper.toResponse(buscarOFallar(id));
    }

    @Transactional
    public AsignacionResponse crear(AsignacionRequest request) {
        Docente docente = docenteRepository.findById(request.docenteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el docente con id " + request.docenteId()));
        Materia materia = materiaRepository.findById(request.materiaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la materia con id " + request.materiaId()));
        Aula aula = aulaRepository.findById(request.aulaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el aula con id " + request.aulaId()));

        if (asignacionRepository.existsByDocenteIdAndMateriaIdAndAulaId(docente.getId(), materia.getId(), aula.getId())) {
            throw new RegistroDuplicadoException("Esa asignación (docente + materia + aula) ya existe");
        }

        Asignacion asignacion = Asignacion.builder().docente(docente).materia(materia).aula(aula).build();
        return asignacionMapper.toResponse(asignacionRepository.save(asignacion));
    }

    @Transactional
    public void eliminar(Long id) {
        asignacionRepository.delete(buscarOFallar(id));
    }

    private Asignacion buscarOFallar(Long id) {
        return asignacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la asignación con id " + id));
    }
}
