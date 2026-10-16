package com.clubdeportivo;

import com.clubdeportivo.dto.PersonaAccesoDTO;
import com.clubdeportivo.dto.RegistroAccesoDTO;
import com.clubdeportivo.dto.RegistroAccesoRequestDTO;
import com.clubdeportivo.model.enums.EstadoCuota;
import com.clubdeportivo.model.enums.TipoAcceso;
import com.clubdeportivo.service.ControlAccesoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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
 * PRUEBAS DE CONTROLADOR MVC: ControlAccesoControllerTest
 * ========================================================================================
 * Verifica el comportamiento de los endpoints HTTP con MockMvc y Spring Security.
 * ========================================================================================
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ControlAccesoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ControlAccesoService controlAccesoService;

    @Test
    @WithMockUser(username = "recepcion", roles = {"RECEPCIONISTA"})
    @DisplayName("GET /accesos/control debe retornar la vista HTML con el modelo del puesto de control")
    void testPantallaControl() throws Exception {
        when(controlAccesoService.obtenerUltimosAccesos()).thenReturn(Collections.emptyList());
        when(controlAccesoService.contarEntradasHoy()).thenReturn(15L);
        when(controlAccesoService.contarSalidasHoy()).thenReturn(5L);
        when(controlAccesoService.calcularPersonasActualmenteDentro()).thenReturn(10L);

        mockMvc.perform(get("/accesos/control"))
                .andExpect(status().isOk())
                .andExpect(view().name("accesos/control"))
                .andExpect(model().attributeExists("ultimosAccesos"))
                .andExpect(model().attribute("entradasHoy", 15L));
    }

    @Test
    @WithMockUser(username = "recepcion", roles = {"RECEPCIONISTA"})
    @DisplayName("GET /accesos/buscar-dni debe responder JSON con fotografía y estado de cuota")
    void testBuscarDniAjax() throws Exception {
        PersonaAccesoDTO persona = new PersonaAccesoDTO();
        persona.setDni("32456789");
        persona.setNombreCompleto("Carlos Gómez");
        persona.setFotoRostro("avatar-1.png");
        persona.setEstadoCuota(EstadoCuota.AL_DIA);
        persona.setHabilitadoAcceso(true);

        when(controlAccesoService.buscarPersonaPorDni("32456789")).thenReturn(persona);

        mockMvc.perform(get("/accesos/buscar-dni")
                        .param("dni", "32456789")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dni").value("32456789"))
                .andExpect(jsonPath("$.nombreCompleto").value("Carlos Gómez"))
                .andExpect(jsonPath("$.fotoRostro").value("avatar-1.png"))
                .andExpect(jsonPath("$.estadoCuota").value("AL_DIA"));
    }

    @Test
    @WithMockUser(username = "recepcion", roles = {"RECEPCIONISTA"})
    @DisplayName("POST /accesos/registrar debe guardar el movimiento y redirigir con mensaje flash")
    void testRegistrarAccesoPost() throws Exception {
        RegistroAccesoDTO dto = new RegistroAccesoDTO();
        dto.setDniPersona("32456789");
        dto.setNombreCompletoPersona("Carlos Gómez");
        dto.setTipoAcceso(TipoAcceso.ENTRADA);
        dto.setFechaHora(java.time.LocalDateTime.now());

        when(controlAccesoService.registrarAcceso(any(RegistroAccesoRequestDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/accesos/registrar")
                        .with(csrf())
                        .param("dniPersona", "32456789")
                        .param("tipoAcceso", "ENTRADA")
                        .param("puntoAcceso", "Molinete #1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/accesos/control"))
                .andExpect(flash().attributeExists("mensajeExito"));
    }
}
