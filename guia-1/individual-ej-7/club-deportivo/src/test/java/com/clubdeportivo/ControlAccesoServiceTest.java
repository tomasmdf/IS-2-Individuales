package com.clubdeportivo;

import com.clubdeportivo.dto.PersonaAccesoDTO;
import com.clubdeportivo.dto.RegistroAccesoDTO;
import com.clubdeportivo.dto.RegistroAccesoRequestDTO;
import com.clubdeportivo.mapper.RegistroAccesoMapper;
import com.clubdeportivo.model.Familiar;
import com.clubdeportivo.model.RegistroAcceso;
import com.clubdeportivo.model.Socio;
import com.clubdeportivo.model.enums.EstadoCuota;
import com.clubdeportivo.model.enums.Parentesco;
import com.clubdeportivo.model.enums.TipoAcceso;
import com.clubdeportivo.repository.FamiliarRepository;
import com.clubdeportivo.repository.RegistroAccesoRepository;
import com.clubdeportivo.repository.SocioRepository;
import com.clubdeportivo.service.SocioService;
import com.clubdeportivo.service.impl.ControlAccesoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ========================================================================================
 * PRUEBAS UNITARIAS: ControlAccesoServiceTest
 * ========================================================================================
 * Valida la lógica de cotejo facial, verificación de cuota perimetral y registro de entradas y salidas.
 * ========================================================================================
 */
@ExtendWith(MockitoExtension.class)
class ControlAccesoServiceTest {

    @Mock
    private SocioRepository socioRepository;

    @Mock
    private FamiliarRepository familiarRepository;

    @Mock
    private RegistroAccesoRepository registroAccesoRepository;

    @Mock
    private SocioService socioService;

    @Mock
    private RegistroAccesoMapper registroAccesoMapper;

    @InjectMocks
    private ControlAccesoServiceImpl controlAccesoService;

    private Socio socio;
    private Familiar familiar;

    @BeforeEach
    void setUp() {
        socio = new Socio();
        socio.setId(10L);
        socio.setDni("30111222");
        socio.setNombre("Mariano");
        socio.setApellido("López");
        socio.setFotoRostro("rostro-mariano.jpg");
        socio.setActivo(true);

        familiar = new Familiar();
        familiar.setId(50L);
        familiar.setSocio(socio);
        familiar.setDni("48222333");
        familiar.setNombre("Lucía");
        familiar.setApellido("López");
        familiar.setParentesco(Parentesco.HIJO);
        familiar.setFechaNacimiento(LocalDate.of(2014, 2, 10));
        familiar.setFotoRostro("rostro-lucia.jpg");
        familiar.setActivo(true);
    }

    @Test
    @DisplayName("Debe encontrar al socio titular por DNI y retornar su foto y estado de cuota")
    void testBuscarSocioTitularPorDni() {
        when(socioRepository.findByDni("30111222")).thenReturn(Optional.of(socio));
        when(socioService.calcularEstadoCuota(10L)).thenReturn(EstadoCuota.AL_DIA);
        when(registroAccesoRepository.findTopByDniPersonaOrderByFechaHoraDesc("30111222")).thenReturn(Optional.empty());

        PersonaAccesoDTO persona = controlAccesoService.buscarPersonaPorDni("30111222");

        assertNotNull(persona);
        assertEquals("30111222", persona.getDni());
        assertTrue(persona.isEsSocioTitular());
        assertEquals("rostro-mariano.jpg", persona.getFotoRostro());
        assertEquals(EstadoCuota.AL_DIA, persona.getEstadoCuota());
        assertTrue(persona.isHabilitadoAcceso());
        assertEquals(TipoAcceso.ENTRADA, persona.getSugerenciaProximoAcceso());
    }

    @Test
    @DisplayName("Debe encontrar a un familiar dependiente y vincular el estado de cuota del titular")
    void testBuscarFamiliarPorDni() {
        when(socioRepository.findByDni("48222333")).thenReturn(Optional.empty());
        when(familiarRepository.findByDni("48222333")).thenReturn(Optional.of(familiar));
        when(socioService.calcularEstadoCuota(10L)).thenReturn(EstadoCuota.ADEUDA);
        when(registroAccesoRepository.findTopByDniPersonaOrderByFechaHoraDesc("48222333")).thenReturn(Optional.empty());

        PersonaAccesoDTO persona = controlAccesoService.buscarPersonaPorDni("48222333");

        assertNotNull(persona);
        assertEquals("48222333", persona.getDni());
        assertFalse(persona.isEsSocioTitular());
        assertEquals("rostro-lucia.jpg", persona.getFotoRostro());
        assertEquals(EstadoCuota.ADEUDA, persona.getEstadoCuota());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el DNI ingresado no pertenece a ningún socio o familiar")
    void testBuscarDniInexistente() {
        when(socioRepository.findByDni("99999999")).thenReturn(Optional.empty());
        when(familiarRepository.findByDni("99999999")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            controlAccesoService.buscarPersonaPorDni("99999999");
        });
    }

    @Test
    @DisplayName("Debe registrar la entrada con fotografía facial y estado de cuota")
    void testRegistrarAccesoEntrada() {
        when(socioRepository.findByDni("30111222")).thenReturn(Optional.of(socio));
        when(socioService.calcularEstadoCuota(10L)).thenReturn(EstadoCuota.AL_DIA);
        when(registroAccesoRepository.findTopByDniPersonaOrderByFechaHoraDesc("30111222")).thenReturn(Optional.empty());

        RegistroAcceso registroPersistido = new RegistroAcceso();
        registroPersistido.setId(1L);
        registroPersistido.setTipoAcceso(TipoAcceso.ENTRADA);
        registroPersistido.setDniPersona("30111222");

        when(registroAccesoRepository.save(any(RegistroAcceso.class))).thenReturn(registroPersistido);

        RegistroAccesoDTO dtoRespuesta = new RegistroAccesoDTO();
        dtoRespuesta.setId(1L);
        dtoRespuesta.setTipoAcceso(TipoAcceso.ENTRADA);
        when(registroAccesoMapper.toDTO(registroPersistido)).thenReturn(dtoRespuesta);

        RegistroAccesoRequestDTO request = new RegistroAccesoRequestDTO("30111222", TipoAcceso.ENTRADA);
        RegistroAccesoDTO resultado = controlAccesoService.registrarAcceso(request);

        assertNotNull(resultado);
        assertEquals(TipoAcceso.ENTRADA, resultado.getTipoAcceso());
        verify(registroAccesoRepository, times(1)).save(any(RegistroAcceso.class));
    }

    @Test
    @DisplayName("Debe rechazar un segundo ingreso si el socio ya se encuentra dentro del club (Anti-Passback)")
    void testRegistrarDobleEntradaRechazada() {
        when(socioRepository.findByDni("30111222")).thenReturn(Optional.of(socio));
        when(socioService.calcularEstadoCuota(10L)).thenReturn(EstadoCuota.AL_DIA);

        RegistroAcceso ingresoPrevio = new RegistroAcceso();
        ingresoPrevio.setId(99L);
        ingresoPrevio.setTipoAcceso(TipoAcceso.ENTRADA);
        ingresoPrevio.setDniPersona("30111222");
        ingresoPrevio.setFechaHora(java.time.LocalDateTime.now().minusHours(1));

        when(registroAccesoRepository.findTopByDniPersonaOrderByFechaHoraDesc("30111222"))
                .thenReturn(Optional.of(ingresoPrevio));

        RegistroAccesoRequestDTO request = new RegistroAccesoRequestDTO("30111222", TipoAcceso.ENTRADA);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            controlAccesoService.registrarAcceso(request);
        });

        assertTrue(ex.getMessage().contains("ya se encuentra dentro de las instalaciones"));
        verify(registroAccesoRepository, never()).save(any(RegistroAcceso.class));
    }

    @Test
    @DisplayName("Debe rechazar registrar salida si el socio no tiene un ingreso activo previo")
    void testRegistrarSalidaSinIngresoPrevioRechazada() {
        when(socioRepository.findByDni("30111222")).thenReturn(Optional.of(socio));
        when(socioService.calcularEstadoCuota(10L)).thenReturn(EstadoCuota.AL_DIA);
        when(registroAccesoRepository.findTopByDniPersonaOrderByFechaHoraDesc("30111222")).thenReturn(Optional.empty());

        RegistroAccesoRequestDTO request = new RegistroAccesoRequestDTO("30111222", TipoAcceso.SALIDA);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            controlAccesoService.registrarAcceso(request);
        });

        assertTrue(ex.getMessage().contains("no registra un ingreso previo"));
        verify(registroAccesoRepository, never()).save(any(RegistroAcceso.class));
    }

    @Test
    @DisplayName("Debe permitir salida si el socio se encuentra dentro del club")
    void testRegistrarSalidaValida() {
        when(socioRepository.findByDni("30111222")).thenReturn(Optional.of(socio));
        when(socioService.calcularEstadoCuota(10L)).thenReturn(EstadoCuota.AL_DIA);

        RegistroAcceso ingresoPrevio = new RegistroAcceso();
        ingresoPrevio.setId(99L);
        ingresoPrevio.setTipoAcceso(TipoAcceso.ENTRADA);
        ingresoPrevio.setDniPersona("30111222");
        ingresoPrevio.setFechaHora(java.time.LocalDateTime.now().minusHours(2));

        when(registroAccesoRepository.findTopByDniPersonaOrderByFechaHoraDesc("30111222"))
                .thenReturn(Optional.of(ingresoPrevio));

        RegistroAcceso salidaPersistida = new RegistroAcceso();
        salidaPersistida.setId(100L);
        salidaPersistida.setTipoAcceso(TipoAcceso.SALIDA);
        salidaPersistida.setDniPersona("30111222");

        when(registroAccesoRepository.save(any(RegistroAcceso.class))).thenReturn(salidaPersistida);

        RegistroAccesoDTO dtoRespuesta = new RegistroAccesoDTO();
        dtoRespuesta.setId(100L);
        dtoRespuesta.setTipoAcceso(TipoAcceso.SALIDA);
        when(registroAccesoMapper.toDTO(salidaPersistida)).thenReturn(dtoRespuesta);

        RegistroAccesoRequestDTO request = new RegistroAccesoRequestDTO("30111222", TipoAcceso.SALIDA);
        RegistroAccesoDTO resultado = controlAccesoService.registrarAcceso(request);

        assertNotNull(resultado);
        assertEquals(TipoAcceso.SALIDA, resultado.getTipoAcceso());
        verify(registroAccesoRepository, times(1)).save(any(RegistroAcceso.class));
    }

    @Test
    @DisplayName("Debe buscar correctamente a un socio familiar dependiente de otro socio")
    void testBuscarSocioFamiliarPorDni() {
        Socio socioFamiliar = new Socio();
        socioFamiliar.setId(20L);
        socioFamiliar.setDni("35999888");
        socioFamiliar.setNombre("Ana");
        socioFamiliar.setApellido("López");
        socioFamiliar.setNumeroSocio("SOC-2026-0002");
        socioFamiliar.setTipoSocio(com.clubdeportivo.model.enums.TipoSocio.FAMILIAR);
        socioFamiliar.setSocioTitular(socio);
        socioFamiliar.setParentesco(Parentesco.CONYUGE);
        socioFamiliar.setActivo(true);

        when(socioRepository.findByDni("35999888")).thenReturn(Optional.of(socioFamiliar));
        when(socioService.calcularEstadoCuota(20L)).thenReturn(EstadoCuota.AL_DIA);
        when(registroAccesoRepository.findTopByDniPersonaOrderByFechaHoraDesc("35999888")).thenReturn(Optional.empty());

        PersonaAccesoDTO persona = controlAccesoService.buscarPersonaPorDni("35999888");

        assertNotNull(persona);
        assertEquals("35999888", persona.getDni());
        assertFalse(persona.isEsSocioTitular());
        assertTrue(persona.getRolDescripcion().contains("Socio Familiar"));
        assertEquals(socio.getNombreCompleto(), persona.getSocioTitularNombre());
        assertEquals(EstadoCuota.AL_DIA, persona.getEstadoCuota());
    }
}
