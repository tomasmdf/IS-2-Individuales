package com.colegio.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.colegio.dto.AlumnoRequest;
import com.colegio.dto.AlumnoResponse;
import com.colegio.exception.RecursoNoEncontradoException;
import com.colegio.exception.RegistroDuplicadoException;
import com.colegio.mapper.AlumnoMapper;
import com.colegio.model.entity.Alumno;
import com.colegio.model.entity.Aula;
import com.colegio.model.entity.Grado;
import com.colegio.repository.AlumnoRepository;
import com.colegio.repository.AulaRepository;
import com.colegio.repository.GradoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlumnoService {

    private final AlumnoRepository alumnoRepository;
    private final GradoRepository gradoRepository;
    private final AulaRepository aulaRepository;
    private final AlumnoMapper alumnoMapper;

    @Transactional(readOnly = true)
    public List<AlumnoResponse> listar() {
        return alumnoRepository.findAll().stream().map(alumnoMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AlumnoResponse obtener(Long id) {
        return alumnoMapper.toResponse(buscarOFallar(id));
    }

    @Transactional
    public AlumnoResponse crear(AlumnoRequest request) {
        if (alumnoRepository.existsByDni(request.dni())) {
            throw new RegistroDuplicadoException("Ya existe un alumno con el DNI " + request.dni());
        }
        Grado grado = gradoRepository.findById(request.gradoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el grado con id " + request.gradoId()));
        Aula aula = aulaRepository.findById(request.aulaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el aula con id " + request.aulaId()));

        Alumno alumno = Alumno.builder()
                .nombre(request.nombre()).apellido(request.apellido()).dni(request.dni())
                .sexo(request.sexo()).fechaNacimiento(request.fechaNacimiento())
                .grado(grado).aula(aula)
                .build();
        return alumnoMapper.toResponse(alumnoRepository.save(alumno));
    }

    @Transactional
    public AlumnoResponse actualizar(Long id, AlumnoRequest request) {
        Alumno alumno = buscarOFallar(id);
        if (!alumno.getDni().equals(request.dni()) && alumnoRepository.existsByDni(request.dni())) {
            throw new RegistroDuplicadoException("Ya existe un alumno con el DNI " + request.dni());
        }
        Grado grado = gradoRepository.findById(request.gradoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el grado con id " + request.gradoId()));
        Aula aula = aulaRepository.findById(request.aulaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el aula con id " + request.aulaId()));

        alumno.setNombre(request.nombre());
        alumno.setApellido(request.apellido());
        alumno.setDni(request.dni());
        alumno.setSexo(request.sexo());
        alumno.setFechaNacimiento(request.fechaNacimiento());
        alumno.setGrado(grado);
        alumno.setAula(aula);
        return alumnoMapper.toResponse(alumnoRepository.save(alumno));
    }

    @Transactional
    public void eliminar(Long id) {
        alumnoRepository.delete(buscarOFallar(id));
    }

    private Alumno buscarOFallar(Long id) {
        return alumnoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el alumno con id " + id));
    }
}
