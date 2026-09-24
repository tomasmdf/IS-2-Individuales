package com.clubdeportivo.service.impl;

import com.clubdeportivo.dto.PersonaAccesoDTO;
import com.clubdeportivo.dto.RegistroAccesoDTO;
import com.clubdeportivo.dto.RegistroAccesoRequestDTO;
import com.clubdeportivo.mapper.RegistroAccesoMapper;
import com.clubdeportivo.model.Familiar;
import com.clubdeportivo.model.RegistroAcceso;
import com.clubdeportivo.model.Socio;
import com.clubdeportivo.model.enums.EstadoCuota;
import com.clubdeportivo.model.enums.TipoAcceso;
import com.clubdeportivo.repository.FamiliarRepository;
import com.clubdeportivo.repository.RegistroAccesoRepository;
import com.clubdeportivo.repository.SocioRepository;
import com.clubdeportivo.service.ControlAccesoService;
import com.clubdeportivo.service.SocioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ========================================================================================
 * IMPLEMENTACIÓN DEL SERVICIO: ControlAccesoServiceImpl
 * ========================================================================================
 * Lógica transaccional para la validación perimetral, cotejo fotográfico facial y registro
 * de entradas y salidas de socios y familiares.
 * ========================================================================================
 */
@Service
@Transactional(readOnly = true)
public class ControlAccesoServiceImpl implements ControlAccesoService {

    private final SocioRepository socioRepository;
    private final FamiliarRepository familiarRepository;
    private final RegistroAccesoRepository registroAccesoRepository;
    private final SocioService socioService;
    private final RegistroAccesoMapper registroAccesoMapper;

    public ControlAccesoServiceImpl(SocioRepository socioRepository,
                                    FamiliarRepository familiarRepository,
                                    RegistroAccesoRepository registroAccesoRepository,
                                    SocioService socioService,
                                    RegistroAccesoMapper registroAccesoMapper) {
        this.socioRepository = socioRepository;
        this.familiarRepository = familiarRepository;
        this.registroAccesoRepository = registroAccesoRepository;
        this.socioService = socioService;
        this.registroAccesoMapper = registroAccesoMapper;
    }

    @Override
    public PersonaAccesoDTO buscarPersonaPorDni(String dni) {
        if (dni == null || dni.trim().isBlank()) {
            throw new IllegalArgumentException("Debe ingresar un número de DNI para la búsqueda");
        }
        String dniLimpio = dni.trim();

        // 1. Intenta encontrar como Socio (Titular o Familiar con membresía)
        Optional<Socio> optSocio = socioRepository.findByDni(dniLimpio);
        if (optSocio.isPresent()) {
            Socio socio = optSocio.get();
            if (!socio.isActivo()) {
                throw new IllegalStateException("El socio asociado a este DNI está dado de baja");
            }
            if (socio.getTipoSocio() == com.clubdeportivo.model.enums.TipoSocio.FAMILIAR 
                    && socio.getSocioTitular() != null 
                    && !socio.getSocioTitular().isActivo()) {
                throw new IllegalStateException("El socio titular del cual depende este socio familiar está dado de baja");
            }

            EstadoCuota estadoCuota = socioService.calcularEstadoCuota(socio.getId());
            boolean esTitular = (socio.getTipoSocio() == com.clubdeportivo.model.enums.TipoSocio.TITULAR);
            String rolDesc = esTitular 
                    ? "Socio Titular (Carnet: " + socio.getNumeroSocio() + ")"
                    : "Socio Familiar (" + (socio.getParentesco() != null ? socio.getParentesco().getDescripcion() : "Familiar") + " - Carnet: " + socio.getNumeroSocio() + ")";
            String titularNombre = esTitular 
                    ? socio.getNombreCompleto() 
                    : (socio.getSocioTitular() != null ? socio.getSocioTitular().getNombreCompleto() : "N/A");

            return construirPersonaDTO(
                    socio.getDni(),
                    socio.getNombreCompleto(),
                    esTitular,
                    rolDesc,
                    titularNombre,
                    socio.getId(),
                    null,
                    socio.getFotoRostro(),
                    estadoCuota
            );
        }

        // 2. Si no es titular ni socio familiar, busca como Integrante del Grupo Familiar dependiente
        Optional<Familiar> optFamiliar = familiarRepository.findByDni(dniLimpio);
        if (optFamiliar.isPresent()) {
            Familiar familiar = optFamiliar.get();
            if (!familiar.isActivo()) {
                throw new IllegalStateException("El familiar asociado a este DNI está dado de baja");
            }
            Socio titular = familiar.getSocio();
            if (!titular.isActivo()) {
                throw new IllegalStateException("El socio titular del grupo familiar está dado de baja");
            }
            EstadoCuota estadoCuota = socioService.calcularEstadoCuota(titular.getId());
            return construirPersonaDTO(
                    familiar.getDni(),
                    familiar.getNombreCompleto(),
                    false,
                    "Grupo Familiar: " + familiar.getParentesco().getDescripcion(),
                    titular.getNombreCompleto(),
                    titular.getId(),
                    familiar.getId(),
                    familiar.getFotoRostro(),
                    estadoCuota
            );
        }

        throw new IllegalArgumentException("No se encontró ningún socio ni familiar registrado con el DNI: " + dniLimpio);
    }

    private PersonaAccesoDTO construirPersonaDTO(String dni, String nombreCompleto, boolean esTitular,
                                                 String rolDesc, String titularNombre, Long socioId,
                                                 Long familiarId, String fotoRostro, EstadoCuota estadoCuota) {
        PersonaAccesoDTO dto = new PersonaAccesoDTO();
        dto.setDni(dni);
        dto.setNombreCompleto(nombreCompleto);
        dto.setEsSocioTitular(esTitular);
        dto.setRolDescripcion(rolDesc);
        dto.setSocioTitularNombre(titularNombre);
        dto.setSocioId(socioId);
        dto.setFamiliarId(familiarId);
        dto.setFotoRostro(fotoRostro);
        dto.setEstadoCuota(estadoCuota);

        // Reglas de negocio de habilitación según la situación de la cuota familiar
        if (estadoCuota == EstadoCuota.AL_DIA) {
            dto.setHabilitadoAcceso(true);
            dto.setMensajeHabilitacion("ACCESO PERMITIDO - Cuota al día");
        } else if (estadoCuota == EstadoCuota.ADEUDA) {
            dto.setHabilitadoAcceso(true);
            dto.setMensajeHabilitacion("ACCESO CON AVISO - Adeuda cuota del mes en curso");
        } else {
            dto.setHabilitadoAcceso(false);
            dto.setMensajeHabilitacion("ACCESO RESTRINGIDO - Cuotas vencidas pendientes de pago");
        }

        // Determina el estado actual de presencia consultando el último movimiento
        Optional<RegistroAcceso> ultimo = registroAccesoRepository.findTopByDniPersonaOrderByFechaHoraDesc(dni);
        if (ultimo.isPresent()) {
            RegistroAcceso ult = ultimo.get();
            boolean adentro = (ult.getTipoAcceso() == TipoAcceso.ENTRADA);
            dto.setActualmenteAdentro(adentro);
            dto.setSugerenciaProximoAcceso(adentro ? TipoAcceso.SALIDA : TipoAcceso.ENTRADA);
            dto.setFechaUltimoAcceso(ult.getFechaHora());
        } else {
            // Primer acceso histórico de la persona
            dto.setActualmenteAdentro(false);
            dto.setSugerenciaProximoAcceso(TipoAcceso.ENTRADA);
            dto.setFechaUltimoAcceso(null);
        }

        return dto;
    }

    @Override
    @Transactional
    public RegistroAccesoDTO registrarAcceso(RegistroAccesoRequestDTO requestDTO) {
        PersonaAccesoDTO persona = buscarPersonaPorDni(requestDTO.getDniPersona());

        // ====================================================================================
        // CONTROL DE ACCESOS CONSECUTIVOS / ANTI-PASSBACK
        // Requerimiento: "Verificar que un mismo socio no puede ingresar 2 veces al club sin haber salido"
        // ====================================================================================
        Optional<RegistroAcceso> ultimoAccesoOpt = registroAccesoRepository.findTopByDniPersonaOrderByFechaHoraDesc(persona.getDni());

        if (requestDTO.getTipoAcceso() == TipoAcceso.ENTRADA) {
            if (ultimoAccesoOpt.isPresent() && ultimoAccesoOpt.get().getTipoAcceso() == TipoAcceso.ENTRADA) {
                String horaIngreso = ultimoAccesoOpt.get().getFechaHora().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
                throw new IllegalStateException(String.format(
                        "Acceso Denegado: %s ya se encuentra dentro de las instalaciones del club (ingreso previo a las %s). " +
                        "No puede ingresar dos veces consecutivas sin haber registrado su salida.",
                        persona.getNombreCompleto(), horaIngreso));
            }
        } else if (requestDTO.getTipoAcceso() == TipoAcceso.SALIDA) {
            if (ultimoAccesoOpt.isEmpty() || ultimoAccesoOpt.get().getTipoAcceso() == TipoAcceso.SALIDA) {
                throw new IllegalStateException(String.format(
                        "Acceso Denegado: %s no registra un ingreso previo activo al club. " +
                        "Debe registrar una ENTRADA antes de registrar una salida.",
                        persona.getNombreCompleto()));
            }
        }

        RegistroAcceso nuevoRegistro = new RegistroAcceso();
        nuevoRegistro.setFechaHora(LocalDateTime.now());
        nuevoRegistro.setTipoAcceso(requestDTO.getTipoAcceso());
        nuevoRegistro.setDniPersona(persona.getDni());
        nuevoRegistro.setNombreCompletoPersona(persona.getNombreCompleto());
        nuevoRegistro.setFotoRostro(persona.getFotoRostro());
        nuevoRegistro.setEsSocioTitular(persona.isEsSocioTitular());
        nuevoRegistro.setSocioId(persona.getSocioId());
        nuevoRegistro.setFamiliarId(persona.getFamiliarId());
        nuevoRegistro.setEstadoCuotaMomento(persona.getEstadoCuota());
        nuevoRegistro.setPuntoAcceso(requestDTO.getPuntoAcceso() != null ? requestDTO.getPuntoAcceso() : "Molinete Principal");
        nuevoRegistro.setObservaciones(requestDTO.getObservaciones());

        RegistroAcceso guardado = registroAccesoRepository.save(nuevoRegistro);
        return registroAccesoMapper.toDTO(guardado);
    }

    @Override
    public List<RegistroAccesoDTO> obtenerUltimosAccesos() {
        return registroAccesoRepository.findTop20ByOrderByFechaHoraDesc().stream()
                .map(registroAccesoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RegistroAccesoDTO> filtrarPorRango(LocalDateTime inicio, LocalDateTime fin) {
        return registroAccesoRepository.findByFechaHoraBetweenOrderByFechaHoraDesc(inicio, fin).stream()
                .map(registroAccesoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public long contarAccesosHoy() {
        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        LocalDateTime finHoy = LocalDate.now().atTime(LocalTime.MAX);
        return registroAccesoRepository.countByFechaHoraBetween(inicioHoy, finHoy);
    }

    @Override
    public long contarEntradasHoy() {
        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        LocalDateTime finHoy = LocalDate.now().atTime(LocalTime.MAX);
        return registroAccesoRepository.countByTipoAccesoAndFechaHoraBetween(TipoAcceso.ENTRADA, inicioHoy, finHoy);
    }

    @Override
    public long contarSalidasHoy() {
        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        LocalDateTime finHoy = LocalDate.now().atTime(LocalTime.MAX);
        return registroAccesoRepository.countByTipoAccesoAndFechaHoraBetween(TipoAcceso.SALIDA, inicioHoy, finHoy);
    }

    @Override
    public long calcularPersonasActualmenteDentro() {
        long dentro = contarEntradasHoy() - contarSalidasHoy();
        return Math.max(0, dentro);
    }
}
