package com.clubdeportivo;

import com.clubdeportivo.dto.PagoCuotaDTO;
import com.clubdeportivo.dto.PagoCuotaFormDTO;
import com.clubdeportivo.mapper.PagoCuotaMapper;
import com.clubdeportivo.model.PagoCuota;
import com.clubdeportivo.model.Socio;
import com.clubdeportivo.model.enums.MedioPago;
import com.clubdeportivo.repository.PagoCuotaRepository;
import com.clubdeportivo.repository.SocioRepository;
import com.clubdeportivo.service.impl.PagoCuotaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * ========================================================================================
 * PRUEBAS UNITARIAS: PagoCuotaServiceTest
 * ========================================================================================
 * Valida la lógica de cobranzas de cuotas sociales para el grupo familiar con distintos
 * medios de pago (Efectivo, Transferencia, Mercado Pago).
 * ========================================================================================
 */
@ExtendWith(MockitoExtension.class)
class PagoCuotaServiceTest {

    @Mock
    private PagoCuotaRepository pagoCuotaRepository;

    @Mock
    private SocioRepository socioRepository;

    @Mock
    private PagoCuotaMapper pagoCuotaMapper;

    @InjectMocks
    private PagoCuotaServiceImpl pagoCuotaService;

    private Socio socio;
    private PagoCuotaFormDTO formDTO;

    @BeforeEach
    void setUp() {
        socio = new Socio();
        socio.setId(1L);
        socio.setNombre("Carlos");
        socio.setApellido("Gómez");
        socio.setActivo(true);

        formDTO = new PagoCuotaFormDTO();
        formDTO.setSocioId(1L);
        formDTO.setPeriodoMes(9);
        formDTO.setPeriodoAnio(2026);
        formDTO.setMonto(new BigDecimal("15000.00"));
        formDTO.setMedioPago(MedioPago.MERCADO_PAGO);
        formDTO.setObservaciones("Pago QR Mercado Pago");
    }

    @Test
    @DisplayName("Debe registrar exitosamente un pago con Mercado Pago para la familia")
    void testRegistrarPagoMercadoPago() {
        when(socioRepository.findById(1L)).thenReturn(Optional.of(socio));
        when(pagoCuotaRepository.existsBySocioIdAndPeriodoAnioAndPeriodoMes(1L, 2026, 9)).thenReturn(false);

        PagoCuota pagoEntidad = new PagoCuota();
        pagoEntidad.setId(100L);
        pagoEntidad.setMedioPago(MedioPago.MERCADO_PAGO);

        when(pagoCuotaMapper.toEntity(eq(formDTO), eq(socio), anyString())).thenReturn(pagoEntidad);
        when(pagoCuotaRepository.save(any(PagoCuota.class))).thenReturn(pagoEntidad);

        PagoCuotaDTO dtoEsperado = new PagoCuotaDTO();
        dtoEsperado.setId(100L);
        dtoEsperado.setMedioPago(MedioPago.MERCADO_PAGO);
        dtoEsperado.setNumeroComprobante("REC-202609-ABC1234");
        when(pagoCuotaMapper.toDTO(pagoEntidad)).thenReturn(dtoEsperado);

        PagoCuotaDTO resultado = pagoCuotaService.registrarPago(formDTO);

        assertNotNull(resultado);
        assertEquals(MedioPago.MERCADO_PAGO, resultado.getMedioPago());
        verify(pagoCuotaRepository, times(1)).save(any(PagoCuota.class));
    }

    @Test
    @DisplayName("Debe rechazar el pago si el período ya fue abonado previamente")
    void testRechazarPagoDuplicado() {
        when(socioRepository.findById(1L)).thenReturn(Optional.of(socio));
        when(pagoCuotaRepository.existsBySocioIdAndPeriodoAnioAndPeriodoMes(1L, 2026, 9)).thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            pagoCuotaService.registrarPago(formDTO);
        });

        assertTrue(ex.getMessage().contains("ya se encuentra abonada"));
        verify(pagoCuotaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe admitir cobro en Efectivo y Transferencia Bancaria")
    void testRegistrarPagoEfectivoYTransferencia() {
        // Prueba con Transferencia
        formDTO.setMedioPago(MedioPago.TRANSFERENCIA);
        when(socioRepository.findById(1L)).thenReturn(Optional.of(socio));
        when(pagoCuotaRepository.existsBySocioIdAndPeriodoAnioAndPeriodoMes(1L, 2026, 9)).thenReturn(false);

        PagoCuota pagoTr = new PagoCuota();
        when(pagoCuotaMapper.toEntity(eq(formDTO), eq(socio), anyString())).thenReturn(pagoTr);
        when(pagoCuotaRepository.save(any(PagoCuota.class))).thenReturn(pagoTr);

        PagoCuotaDTO dtoTr = new PagoCuotaDTO();
        dtoTr.setMedioPago(MedioPago.TRANSFERENCIA);
        when(pagoCuotaMapper.toDTO(pagoTr)).thenReturn(dtoTr);

        PagoCuotaDTO resTr = pagoCuotaService.registrarPago(formDTO);
        assertEquals(MedioPago.TRANSFERENCIA, resTr.getMedioPago());
    }
}
