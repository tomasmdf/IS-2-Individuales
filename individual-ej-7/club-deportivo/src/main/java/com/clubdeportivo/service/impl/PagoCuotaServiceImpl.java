package com.clubdeportivo.service.impl;

import com.clubdeportivo.dto.PagoCuotaDTO;
import com.clubdeportivo.dto.PagoCuotaFormDTO;
import com.clubdeportivo.mapper.PagoCuotaMapper;
import com.clubdeportivo.model.PagoCuota;
import com.clubdeportivo.model.Socio;
import com.clubdeportivo.model.enums.MedioPago;
import com.clubdeportivo.repository.PagoCuotaRepository;
import com.clubdeportivo.repository.SocioRepository;
import com.clubdeportivo.service.PagoCuotaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * ========================================================================================
 * IMPLEMENTACIÓN DEL SERVICIO: PagoCuotaServiceImpl
 * ========================================================================================
 * Lógica transaccional para la cobranza de cuotas del club.
 *
 * Requerimiento de consigna:
 * "Se debe agregar la funcionalidad para poder registrar el pago de la cuota del club para cada
 * familia, donde el pago se puede realizar con distintos medios de pago (Efectivo, Transferencia,
 * Mercado Pago)."
 * ========================================================================================
 */
@Service
@Transactional(readOnly = true)
public class PagoCuotaServiceImpl implements PagoCuotaService {

    private final PagoCuotaRepository pagoCuotaRepository;
    private final SocioRepository socioRepository;
    private final PagoCuotaMapper pagoCuotaMapper;

    public PagoCuotaServiceImpl(PagoCuotaRepository pagoCuotaRepository,
                                SocioRepository socioRepository,
                                PagoCuotaMapper pagoCuotaMapper) {
        this.pagoCuotaRepository = pagoCuotaRepository;
        this.socioRepository = socioRepository;
        this.pagoCuotaMapper = pagoCuotaMapper;
    }

    @Override
    @Transactional
    public PagoCuotaDTO registrarPago(PagoCuotaFormDTO formDTO) {
        Socio socio = socioRepository.findById(formDTO.getSocioId())
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el socio titular con ID: " + formDTO.getSocioId()));

        if (!socio.isActivo()) {
            throw new IllegalStateException("No se puede registrar un pago a un socio dado de baja");
        }

        // Verifica si la familia ya pagó la cuota de ese período para prevenir pagos duplicados
        boolean yaPagado = pagoCuotaRepository.existsBySocioIdAndPeriodoAnioAndPeriodoMes(
                formDTO.getSocioId(), formDTO.getPeriodoAnio(), formDTO.getPeriodoMes());
        if (yaPagado) {
            throw new IllegalStateException(String.format("La cuota de %02d/%d ya se encuentra abonada para este socio",
                    formDTO.getPeriodoMes(), formDTO.getPeriodoAnio()));
        }

        // Genera un código de recibo / comprobante único y formal
        String numeroComprobante = String.format("REC-%d%02d-%s",
                formDTO.getPeriodoAnio(),
                formDTO.getPeriodoMes(),
                UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        PagoCuota pago = pagoCuotaMapper.toEntity(formDTO, socio, numeroComprobante);
        pago.setFechaPago(LocalDateTime.now());

        PagoCuota guardado = pagoCuotaRepository.save(pago);
        return pagoCuotaMapper.toDTO(guardado);
    }

    @Override
    public List<PagoCuotaDTO> listarTodos() {
        return pagoCuotaRepository.findAllByOrderByFechaPagoDesc().stream()
                .map(pagoCuotaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PagoCuotaDTO> listarUltimosDiez() {
        return pagoCuotaRepository.findTop10ByOrderByFechaPagoDesc().stream()
                .map(pagoCuotaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PagoCuotaDTO> listarPorSocio(Long socioId) {
        return pagoCuotaRepository.findBySocioIdOrderByPeriodoAnioDescPeriodoMesDesc(socioId).stream()
                .map(pagoCuotaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PagoCuotaDTO obtenerPorId(Long id) {
        PagoCuota pago = pagoCuotaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el pago con ID: " + id));
        return pagoCuotaMapper.toDTO(pago);
    }

    @Override
    public PagoCuotaDTO obtenerPorComprobante(String comprobante) {
        PagoCuota pago = pagoCuotaRepository.findByNumeroComprobante(comprobante)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró ningún comprobante con código: " + comprobante));
        return pagoCuotaMapper.toDTO(pago);
    }

    @Override
    public BigDecimal calcularRecaudacionMesActual() {
        YearMonth mesActual = YearMonth.now();
        LocalDateTime inicioMes = mesActual.atDay(1).atStartOfDay();
        LocalDateTime finMes = mesActual.atEndOfMonth().atTime(23, 59, 59);

        return pagoCuotaRepository.sumarTotalRecaudadoEntreFechas(inicioMes, finMes);
    }

    @Override
    public BigDecimal calcularRecaudacionPorMedio(MedioPago medioPago) {
        return pagoCuotaRepository.sumarTotalPorMedioPago(medioPago);
    }
}
