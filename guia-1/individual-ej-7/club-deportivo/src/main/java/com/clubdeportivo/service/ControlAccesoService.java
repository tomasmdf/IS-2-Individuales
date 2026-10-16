package com.clubdeportivo.service;

import com.clubdeportivo.dto.PersonaAccesoDTO;
import com.clubdeportivo.dto.RegistroAccesoDTO;
import com.clubdeportivo.dto.RegistroAccesoRequestDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ========================================================================================
 * INTERFAZ DE SERVICIO: ControlAccesoService
 * ========================================================================================
 * Modela la lógica del control perimetral de accesos (entradas y salidas) al club deportivo.
 *
 * Requerimiento de consigna:
 * "Al ingresar al club el sistema registra el horario de entrada, lo mismo sucede en caso de salida.
 * El sistema guarda además de los datos principales una imagen con el rostro de cada persona."
 * ========================================================================================
 */
public interface ControlAccesoService {

    /**
     * Búsqueda en tiempo real por DNI para proyectar la foto del rostro en la pantalla del molinete,
     * validar la situación de la cuota familiar y sugerir si corresponde Entrada o Salida.
     */
    PersonaAccesoDTO buscarPersonaPorDni(String dni);

    /**
     * Registra el evento de Entrada o Salida con fecha/hora, foto facial y estado financiero.
     */
    RegistroAccesoDTO registrarAcceso(RegistroAccesoRequestDTO requestDTO);

    /**
     * Lista los últimos accesos registrados para el monitor en vivo.
     */
    List<RegistroAccesoDTO> obtenerUltimosAccesos();

    /**
     * Consulta con filtro de fechas para auditoría perimetral.
     */
    List<RegistroAccesoDTO> filtrarPorRango(LocalDateTime inicio, LocalDateTime fin);

    long contarAccesosHoy();

    long contarEntradasHoy();

    long contarSalidasHoy();

    long calcularPersonasActualmenteDentro();
}
