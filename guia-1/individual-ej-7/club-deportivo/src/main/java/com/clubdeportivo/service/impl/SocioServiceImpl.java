package com.clubdeportivo.service.impl;

import com.clubdeportivo.dto.SocioDTO;
import com.clubdeportivo.dto.SocioFormDTO;
import com.clubdeportivo.mapper.SocioMapper;
import com.clubdeportivo.model.Socio;
import com.clubdeportivo.model.enums.EstadoCuota;
import com.clubdeportivo.repository.PagoCuotaRepository;
import com.clubdeportivo.repository.SocioRepository;
import com.clubdeportivo.service.FileStorageService;
import com.clubdeportivo.service.SocioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ========================================================================================
 * IMPLEMENTACIÓN DEL SERVICIO: SocioServiceImpl
 * ========================================================================================
 * Contiene la lógica transaccional de negocio para los socios del club.
 *
 * Anotaciones utilizadas:
 * - @Service: Indica que es un componente de servicio en la capa de negocio. Spring administra
 *   su ciclo de vida como un Singleton e intercepta llamadas para aplicar gestión transaccional.
 * - @Transactional(readOnly = true): Optimiza las lecturas de base de datos desactivando la
 *   detección de cambios sucios (dirty checking) de Hibernate, reduciendo el consumo de CPU y memoria.
 * - @Transactional: Habilita el control transaccional ACID en métodos de escritura. Si ocurre una
 *   excepción no comprobada (RuntimeException), se realiza un ROLLBACK automático.
 * ========================================================================================
 */
@Service
@Transactional(readOnly = true)
public class SocioServiceImpl implements SocioService {

    private final SocioRepository socioRepository;
    private final PagoCuotaRepository pagoCuotaRepository;
    private final SocioMapper socioMapper;
    private final FileStorageService fileStorageService;

    public SocioServiceImpl(SocioRepository socioRepository,
                            PagoCuotaRepository pagoCuotaRepository,
                            SocioMapper socioMapper,
                            FileStorageService fileStorageService) {
        this.socioRepository = socioRepository;
        this.pagoCuotaRepository = pagoCuotaRepository;
        this.socioMapper = socioMapper;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public List<SocioDTO> listarTodosActivos() {
        return socioRepository.findByActivoTrueOrderByApellidoAscNombreAsc().stream()
                .map(s -> socioMapper.toDTO(s, calcularEstadoCuota(s.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<SocioDTO> buscarSocios(String criterio) {
        if (criterio == null || criterio.trim().isBlank()) {
            return listarTodosActivos();
        }
        return socioRepository.buscarPorCriterio(criterio.trim()).stream()
                .map(s -> socioMapper.toDTO(s, calcularEstadoCuota(s.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public SocioDTO obtenerPorId(Long id) {
        Socio socio = socioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el socio con ID: " + id));
        return socioMapper.toDTO(socio, calcularEstadoCuota(socio.getId()));
    }

    @Override
    public SocioFormDTO obtenerFormularioPorId(Long id) {
        Socio socio = socioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el socio con ID: " + id));
        return socioMapper.toFormDTO(socio);
    }

    @Override
    @Transactional
    public SocioDTO registrarSocio(SocioFormDTO formDTO) {
        // Valida unicidad de DNI
        if (socioRepository.existsByDni(formDTO.getDni())) {
            throw new IllegalArgumentException("Ya existe un socio registrado con el DNI " + formDTO.getDni());
        }

        // Procesa y almacena la fotografía facial requerida
        String nombreFoto = null;
        if (formDTO.getFotoArchivo() != null && !formDTO.getFotoArchivo().isEmpty()) {
            nombreFoto = fileStorageService.almacenarArchivo(formDTO.getFotoArchivo());
        }

        Socio socio = socioMapper.toEntity(formDTO, nombreFoto);

        // Si se registra como socio familiar vinculado a otro socio titular
        if (formDTO.getTipoSocio() == com.clubdeportivo.model.enums.TipoSocio.FAMILIAR) {
            if (formDTO.getSocioTitularId() == null) {
                throw new IllegalArgumentException("Debe seleccionar el socio titular al cual vincular este socio familiar.");
            }
            Socio titular = socioRepository.findById(formDTO.getSocioTitularId())
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró el socio titular seleccionado con ID: " + formDTO.getSocioTitularId()));
            if (titular.getTipoSocio() == com.clubdeportivo.model.enums.TipoSocio.FAMILIAR) {
                throw new IllegalArgumentException("Un socio familiar solo puede depender de un socio titular.");
            }
            socio.setSocioTitular(titular);
            socio.setTipoSocio(com.clubdeportivo.model.enums.TipoSocio.FAMILIAR);
            socio.setParentesco(formDTO.getParentesco());
        } else {
            socio.setTipoSocio(com.clubdeportivo.model.enums.TipoSocio.TITULAR);
            socio.setSocioTitular(null);
            socio.setParentesco(null);
        }

        // Genera un número de socio correlativo e inequívoco (ej: SOC-2026-00042)
        long totalRegistrados = socioRepository.count() + 1;
        String numeroSocioGenerado = String.format("SOC-%d-%04d", LocalDate.now().getYear(), totalRegistrados);
        socio.setNumeroSocio(numeroSocioGenerado);
        socio.setFechaAlta(LocalDate.now());

        Socio socioGuardado = socioRepository.save(socio);
        return socioMapper.toDTO(socioGuardado, calcularEstadoCuota(socioGuardado.getId()));
    }

    @Override
    @Transactional
    public SocioDTO actualizarSocio(SocioFormDTO formDTO) {
        Socio socio = socioRepository.findById(formDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("No existe el socio a modificar"));

        // Si se cargó una nueva imagen de rostro, se almacena y se elimina la anterior
        String nuevaFoto = null;
        if (formDTO.getFotoArchivo() != null && !formDTO.getFotoArchivo().isEmpty()) {
            nuevaFoto = fileStorageService.almacenarArchivo(formDTO.getFotoArchivo());
            if (socio.getFotoRostro() != null) {
                fileStorageService.eliminarArchivo(socio.getFotoRostro());
            }
        }

        socioMapper.updateEntityFromForm(socio, formDTO, nuevaFoto);

        // Actualización de vinculación titular / familiar
        if (formDTO.getTipoSocio() == com.clubdeportivo.model.enums.TipoSocio.FAMILIAR) {
            if (formDTO.getSocioTitularId() == null) {
                throw new IllegalArgumentException("Debe seleccionar el socio titular al cual vincular este socio familiar.");
            }
            if (formDTO.getSocioTitularId().equals(socio.getId())) {
                throw new IllegalArgumentException("Un socio no puede ser titular de sí mismo.");
            }
            Socio titular = socioRepository.findById(formDTO.getSocioTitularId())
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró el socio titular seleccionado con ID: " + formDTO.getSocioTitularId()));
            if (titular.getTipoSocio() == com.clubdeportivo.model.enums.TipoSocio.FAMILIAR) {
                throw new IllegalArgumentException("Un socio familiar solo puede depender de un socio titular.");
            }
            socio.setTipoSocio(com.clubdeportivo.model.enums.TipoSocio.FAMILIAR);
            socio.setSocioTitular(titular);
            socio.setParentesco(formDTO.getParentesco());
        } else {
            socio.setTipoSocio(com.clubdeportivo.model.enums.TipoSocio.TITULAR);
            socio.setSocioTitular(null);
            socio.setParentesco(null);
        }

        Socio socioActualizado = socioRepository.save(socio);

        return socioMapper.toDTO(socioActualizado, calcularEstadoCuota(socioActualizado.getId()));
    }

    @Override
    @Transactional
    public void darDeBaja(Long id) {
        Socio socio = socioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el socio con ID: " + id));
        // Soft delete: no borramos el registro histórico para mantener la trazabilidad de accesos y pagos
        socio.setActivo(false);
        socioRepository.save(socio);
    }

    @Override
    public EstadoCuota calcularEstadoCuota(Long socioId) {
        // Si el socio es de tipo FAMILIAR, el estado financiero de su membresía depende del titular de su familia
        Socio socio = socioRepository.findById(socioId).orElse(null);
        if (socio != null && socio.getTipoSocio() == com.clubdeportivo.model.enums.TipoSocio.FAMILIAR && socio.getSocioTitular() != null) {
            return calcularEstadoCuota(socio.getSocioTitular().getId());
        }

        LocalDate hoy = LocalDate.now();
        int anioActual = hoy.getYear();
        int mesActual = hoy.getMonthValue();

        // 1. Verifica si el mes en curso está pagado
        boolean pagoMesActual = pagoCuotaRepository.existsBySocioIdAndPeriodoAnioAndPeriodoMes(socioId, anioActual, mesActual);
        if (pagoMesActual) {
            return EstadoCuota.AL_DIA;
        }

        // 2. Si el mes actual no está pagado, verifica el mes anterior
        LocalDate mesAnterior = hoy.minusMonths(1);
        boolean pagoMesAnterior = pagoCuotaRepository.existsBySocioIdAndPeriodoAnioAndPeriodoMes(
                socioId, mesAnterior.getYear(), mesAnterior.getMonthValue());

        // Si pagó el anterior pero no el actual, se encuentra en "ADEUDA" (aviso amistoso en el molinete)
        // Si adeuda dos o más meses, pasa a "VENCIDA" (requiere regularización)
        return pagoMesAnterior ? EstadoCuota.ADEUDA : EstadoCuota.VENCIDA;
    }

    @Override
    public long contarSociosActivos() {
        return socioRepository.countByActivoTrue();
    }

    @Override
    public List<SocioDTO> listarSociosTitularesActivos() {
        return listarSociosTitularesActivos(null);
    }

    @Override
    public List<SocioDTO> listarSociosTitularesActivos(Long excluirId) {
        List<Socio> titulares = socioRepository.findSociosTitularesActivos(excluirId);
        // Mecanismo de contingencia: si no encuentra titulares mediante la consulta pero existen socios activos sin titular
        if (titulares == null || titulares.isEmpty()) {
            titulares = socioRepository.findByActivoTrueOrderByApellidoAscNombreAsc().stream()
                    .filter(s -> s.getSocioTitular() == null && (excluirId == null || !s.getId().equals(excluirId)))
                    .collect(Collectors.toList());
        }
        return titulares.stream()
                .map(s -> socioMapper.toDTO(s, calcularEstadoCuota(s.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<SocioDTO> listarSociosFamiliaresPorTitular(Long titularId) {
        return socioRepository.findBySocioTitularIdAndActivoTrue(titularId).stream()
                .map(s -> socioMapper.toDTO(s, calcularEstadoCuota(s.getId())))
                .collect(Collectors.toList());
    }
}
