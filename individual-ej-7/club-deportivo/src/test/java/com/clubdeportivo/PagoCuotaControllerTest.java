package com.clubdeportivo;

import com.clubdeportivo.dto.PagoCuotaDTO;
import com.clubdeportivo.dto.PagoCuotaFormDTO;
import com.clubdeportivo.dto.SocioDTO;
import com.clubdeportivo.model.enums.MedioPago;
import com.clubdeportivo.service.PagoCuotaService;
import com.clubdeportivo.service.SocioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ========================================================================================
 * PRUEBAS DE CONTROLADOR MVC: PagoCuotaControllerTest
 * ========================================================================================
 * Valida los endpoints de cobranzas familiares, renderizado de formularios y emisión de recibos.
 * ========================================================================================
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PagoCuotaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PagoCuotaService pagoCuotaService;

    @MockBean
    private SocioService socioService;

    @Test
    @WithMockUser(username = "recepcion", roles = {"RECEPCIONISTA"})
    @DisplayName("GET /pagos debe mostrar la lista de cobranzas y la recaudación acumulada")
    void testListarPagos() throws Exception {
        when(pagoCuotaService.listarTodos()).thenReturn(Collections.emptyList());
        when(pagoCuotaService.calcularRecaudacionMesActual()).thenReturn(new BigDecimal("150000.00"));

        mockMvc.perform(get("/pagos"))
                .andExpect(status().isOk())
                .andExpect(view().name("pagos/list"))
                .andExpect(model().attributeExists("pagos"))
                .andExpect(model().attributeExists("recaudacionMes"));
    }

    @Test
    @WithMockUser(username = "recepcion", roles = {"RECEPCIONISTA"})
    @DisplayName("GET /pagos/nuevo debe mostrar el formulario con medios de pago")
    void testNuevoPagoFormulario() throws Exception {
        when(socioService.listarTodosActivos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/pagos/nuevo"))
                .andExpect(status().isOk())
                .andExpect(view().name("pagos/form"))
                .andExpect(model().attributeExists("pagoForm"))
                .andExpect(model().attributeExists("mediosPago"));
    }

    @Test
    @WithMockUser(username = "recepcion", roles = {"RECEPCIONISTA"})
    @DisplayName("POST /pagos/guardar debe procesar el pago y redirigir al recibo oficial")
    void testGuardarPagoExitoso() throws Exception {
        PagoCuotaDTO dto = new PagoCuotaDTO();
        dto.setId(10L);
        dto.setNumeroComprobante("REC-202609-TEST");
        dto.setMedioPago(MedioPago.MERCADO_PAGO);

        when(pagoCuotaService.registrarPago(any(PagoCuotaFormDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/pagos/guardar")
                        .with(csrf())
                        .param("socioId", "1")
                        .param("periodoMes", "9")
                        .param("periodoAnio", "2026")
                        .param("monto", "15000.00")
                        .param("medioPago", "MERCADO_PAGO")
                        .param("observaciones", "Pago QR"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pagos/comprobante/10"))
                .andExpect(flash().attributeExists("mensajeExito"));
    }
}
