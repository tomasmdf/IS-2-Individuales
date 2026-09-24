package com.clubdeportivo;

import com.clubdeportivo.dto.SocioDTO;
import com.clubdeportivo.dto.SocioFormDTO;
import com.clubdeportivo.model.enums.EstadoCuota;
import com.clubdeportivo.service.FamiliarService;
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

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ========================================================================================
 * PRUEBAS DE CONTROLADOR MVC: SocioControllerTest
 * ========================================================================================
 * Valida los endpoints de listado, ficha detallada con grupo familiar y alta de socios.
 * ========================================================================================
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SocioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SocioService socioService;

    @MockBean
    private FamiliarService familiarService;

    @MockBean
    private PagoCuotaService pagoCuotaService;

    @Test
    @WithMockUser(username = "recepcion", roles = {"RECEPCIONISTA"})
    @DisplayName("GET /socios debe listar el padrón de socios")
    void testListarSocios() throws Exception {
        when(socioService.buscarSocios(null)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/socios"))
                .andExpect(status().isOk())
                .andExpect(view().name("socios/list"))
                .andExpect(model().attributeExists("socios"));
    }

    @Test
    @WithMockUser(username = "recepcion", roles = {"RECEPCIONISTA"})
    @DisplayName("GET /socios/detalle/{id} debe mostrar la ficha del socio con su grupo familiar")
    void testDetalleSocio() throws Exception {
        SocioDTO socio = new SocioDTO();
        socio.setId(1L);
        socio.setNombreCompleto("Carlos Gómez");
        socio.setEstadoCuota(EstadoCuota.AL_DIA);

        when(socioService.obtenerPorId(1L)).thenReturn(socio);
        when(familiarService.listarPorSocio(1L)).thenReturn(Collections.emptyList());
        when(socioService.listarSociosFamiliaresPorTitular(1L)).thenReturn(Collections.emptyList());
        when(pagoCuotaService.listarPorSocio(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/socios/detalle/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("socios/detalle"))
                .andExpect(model().attributeExists("socio"))
                .andExpect(model().attributeExists("familiares"))
                .andExpect(model().attributeExists("sociosFamiliares"))
                .andExpect(model().attributeExists("pagos"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("POST /socios/guardar debe procesar alta de socio y redirigir")
    void testGuardarSocioNuevo() throws Exception {
        SocioDTO guardado = new SocioDTO();
        guardado.setId(1L);

        when(socioService.registrarSocio(any(SocioFormDTO.class))).thenReturn(guardado);

        mockMvc.perform(post("/socios/guardar")
                        .with(csrf())
                        .param("dni", "35123456")
                        .param("nombre", "Laura")
                        .param("apellido", "Martínez")
                        .param("email", "laura@test.com")
                        .param("fechaNacimiento", "1990-05-15"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/socios"))
                .andExpect(flash().attributeExists("mensajeExito"));
    }
}
