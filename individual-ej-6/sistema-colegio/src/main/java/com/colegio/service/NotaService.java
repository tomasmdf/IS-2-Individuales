package com.colegio.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.colegio.dto.AlumnoResponse;
import com.colegio.dto.NotaRequest;
import com.colegio.dto.NotaResponse;
import com.colegio.exception.RecursoNoEncontradoException;
import com.colegio.mapper.AlumnoMapper;
import com.colegio.mapper.NotaMapper;
import com.colegio.model.entity.Alumno;
import com.colegio.model.entity.Asignacion;
import com.colegio.model.entity.Nota;
import com.colegio.repository.AlumnoRepository;
import com.colegio.repository.AsignacionRepository;
import com.colegio.repository.NotaRepository;

import lombok.RequiredArgsConstructor;

/**
 * Carga y consulta de notas. Un docente sólo puede cargar/editar notas de
 * SUS PROPIAS asignaciones (materia que dicta en un aula concreta); esta
 * regla de autorización "fina" (por dato, no sólo por URL) se valida acá,
 * en la capa de servicio, comparando el correo del docente autenticado
 * contra el dueño de la asignación. El rol ADMIN está exceptuado.
 */
@Service
@RequiredArgsConstructor
public class NotaService {

    private final NotaRepository notaRepository;
    private final AsignacionRepository asignacionRepository;
    private final AlumnoRepository alumnoRepository;
    private final NotaMapper notaMapper;
    private final AlumnoMapper alumnoMapper;

    @Transactional(readOnly = true)
    public List<NotaResponse> listarPorAsignacion(Long asignacionId) {
        return notaRepository.findByAsignacionIdOrderByAlumnoApellidoAsc(asignacionId)
                .stream().map(notaMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<NotaResponse> listarPorAlumno(Long alumnoId) {
        return notaRepository.findByAlumnoIdOrderByPeriodoAsc(alumnoId)
                .stream().map(notaMapper::toResponse).toList();
    }

    /** Alumnos del aula de una asignación (para armar la planilla de carga). */
    @Transactional(readOnly = true)
    public List<AlumnoResponse> alumnosDeAsignacion(Long asignacionId) {
        return alumnoRepository.findAlumnosDeAsignacion(asignacionId).stream().map(alumnoMapper::toResponse).toList();
    }

    /**
     * Crea la nota si no existe para (alumno, asignación, período), o la
     * actualiza si ya existía (UPSERT), evitando duplicados gracias a la
     * restricción UNIQUE de la entidad Nota.
     */
    @Transactional
    public NotaResponse guardar(NotaRequest request, String correoDocenteAutenticado, boolean esAdmin) {
        Asignacion asignacion = asignacionRepository.findById(request.asignacionId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la asignación con id " + request.asignacionId()));

        if (!esAdmin && !asignacion.getDocente().getCorreo().equalsIgnoreCase(correoDocenteAutenticado)) {
            throw new AccessDeniedException("No puede cargar notas de una asignación que no le pertenece");
        }

        Alumno alumno = alumnoRepository.findById(request.alumnoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el alumno con id " + request.alumnoId()));

        Nota nota = notaRepository.findByAlumnoIdAndAsignacionIdAndPeriodo(alumno.getId(), asignacion.getId(), request.periodo())
                .orElseGet(() -> Nota.builder().alumno(alumno).asignacion(asignacion).periodo(request.periodo()).build());

        nota.setValor(request.valor());
        nota.setObservaciones(request.observaciones());
        nota.setFechaCarga(LocalDate.now());

        return notaMapper.toResponse(notaRepository.save(nota));
    }

    @Transactional
    public void eliminar(Long id, String correoDocenteAutenticado, boolean esAdmin) {
        Nota nota = notaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la nota con id " + id));
        if (!esAdmin && !nota.getAsignacion().getDocente().getCorreo().equalsIgnoreCase(correoDocenteAutenticado)) {
            throw new AccessDeniedException("No puede eliminar una nota de una asignación que no le pertenece");
        }
        notaRepository.delete(nota);
    }
}
