package com.colegio.service;

import java.security.SecureRandom;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.colegio.dto.CambiarPasswordRequest;
import com.colegio.dto.DocenteRequest;
import com.colegio.dto.DocenteResponse;
import com.colegio.event.DocenteRegistradoEvent;
import com.colegio.event.PasswordRestablecidaEvent;
import com.colegio.exception.PasswordActualIncorrectaException;
import com.colegio.exception.RecursoNoEncontradoException;
import com.colegio.exception.RegistroDuplicadoException;
import com.colegio.mapper.DocenteMapper;
import com.colegio.model.entity.Docente;
import com.colegio.model.enums.Rol;
import com.colegio.repository.DocenteRepository;

import lombok.RequiredArgsConstructor;

/**
 * =============================================================================
 * CAPA SERVICE: reglas de negocio del Docente (registro, ABM, cambio de
 * contraseña). Es la única capa que orquesta Repository + Mapper + reglas de
 * validación de negocio (más allá de lo que ya valida Bean Validation en el
 * DTO) y la única marcada @Transactional: aquí "viven" las transacciones.
 * =============================================================================
 */
@Service
@RequiredArgsConstructor
public class DocenteService {

    private final DocenteRepository docenteRepository;
    private final DocenteMapper docenteMapper;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    private static final String CARACTERES_PASSWORD = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Transactional(readOnly = true)
    public List<DocenteResponse> listar() {
        return docenteRepository.findAll().stream().map(docenteMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public DocenteResponse obtener(Long id) {
        return docenteMapper.toResponse(buscarOFallar(id));
    }

    /**
     * Alta de un nuevo docente:
     *   1) valida que el correo no esté ya usado (regla de negocio, además
     *      del UNIQUE de la base de datos, para poder mostrar un mensaje
     *      claro en la pantalla en vez de un error SQL crudo);
     *   2) genera una contraseña inicial aleatoria y la guarda con hash BCrypt;
     *   3) publica DocenteRegistradoEvent, que dispara el envío del correo
     *      de bienvenida (ver event.DocenteRegistradoListener) con la
     *      contraseña SIN cifrar (única vez que existe en memoria, nunca en
     *      la base de datos).
     */
    @Transactional
    public DocenteResponse registrar(DocenteRequest request) {
        if (docenteRepository.existsByCorreo(request.correo())) {
            throw new RegistroDuplicadoException("Ya existe un docente registrado con el correo " + request.correo());
        }

        String passwordGenerada = generarPasswordAleatoria();

        Docente docente = Docente.builder()
                .nombre(request.nombre())
                .apellido(request.apellido())
                .sexo(request.sexo())
                .fechaNacimiento(request.fechaNacimiento())
                .correo(request.correo())
                .passwordHash(passwordEncoder.encode(passwordGenerada))
                .rol(Rol.DOCENTE)
                .activo(true)
                .build();

        Docente guardado = docenteRepository.save(docente);
        eventPublisher.publishEvent(new DocenteRegistradoEvent(guardado, passwordGenerada));
        return docenteMapper.toResponse(guardado);
    }

    @Transactional
    public DocenteResponse actualizar(Long id, DocenteRequest request) {
        Docente docente = buscarOFallar(id);
        if (!docente.getCorreo().equalsIgnoreCase(request.correo())
                && docenteRepository.existsByCorreo(request.correo())) {
            throw new RegistroDuplicadoException("Ya existe un docente registrado con el correo " + request.correo());
        }
        docente.setNombre(request.nombre());
        docente.setApellido(request.apellido());
        docente.setSexo(request.sexo());
        docente.setFechaNacimiento(request.fechaNacimiento());
        docente.setCorreo(request.correo());
        // save() no es estrictamente necesario aquí gracias al "dirty checking" de
        // Hibernate (la entidad ya está gestionada dentro de la transacción), pero se
        // llama explícitamente por claridad y para devolver la instancia actualizada.
        return docenteMapper.toResponse(docenteRepository.save(docente));
    }

    @Transactional
    public void alternarActivo(Long id) {
        Docente docente = buscarOFallar(id);
        docente.setActivo(!docente.isActivo());
        docenteRepository.save(docente);
    }

    /**
     * Cambio de contraseña de un docente autenticado.
     * "correoUsuarioActual" es el username tomado del SecurityContext (nunca
     * se confía en un id que pudiera venir manipulado desde el formulario).
     */
    @Transactional
    public void cambiarPassword(String correoUsuarioActual, CambiarPasswordRequest request) {
        Docente docente = docenteRepository.findByCorreo(correoUsuarioActual)
                .orElseThrow(() -> new RecursoNoEncontradoException("Docente no encontrado"));

        if (!passwordEncoder.matches(request.passwordActual(), docente.getPasswordHash())) {
            throw new PasswordActualIncorrectaException("La contraseña actual ingresada es incorrecta");
        }
        docente.setPasswordHash(passwordEncoder.encode(request.passwordNueva()));
        docenteRepository.save(docente);
    }

    /**
     * Acción de ADMINISTRADOR: genera una NUEVA contraseña aleatoria para un
     * docente ya existente y se la reenvía por correo (plantilla
     * email/restablecimiento.html). Pensado para el caso en que el docente
     * perdió el correo de bienvenida original o nunca le llegó: como la
     * contraseña sólo se guarda con hash BCrypt (nunca en texto plano),
     * NO existe forma de "recuperar" la anterior, sólo de generar una nueva.
     */
    @Transactional
    public void restablecerPassword(Long id) {
        Docente docente = buscarOFallar(id);
        String nuevaPassword = generarPasswordAleatoria();
        docente.setPasswordHash(passwordEncoder.encode(nuevaPassword));
        docenteRepository.save(docente);
        eventPublisher.publishEvent(new PasswordRestablecidaEvent(docente, nuevaPassword));
    }

    private Docente buscarOFallar(Long id) {
        return docenteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el docente con id " + id));
    }

    private String generarPasswordAleatoria() {
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(CARACTERES_PASSWORD.charAt(RANDOM.nextInt(CARACTERES_PASSWORD.length())));
        }
        return sb.toString();
    }
}
