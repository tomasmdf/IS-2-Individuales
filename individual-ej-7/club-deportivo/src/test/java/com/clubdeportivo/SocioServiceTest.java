package com.clubdeportivo;

import com.clubdeportivo.dto.SocioDTO;
import com.clubdeportivo.dto.SocioFormDTO;
import com.clubdeportivo.mapper.SocioMapper;
import com.clubdeportivo.model.Socio;
import com.clubdeportivo.model.enums.EstadoCuota;
import com.clubdeportivo.repository.PagoCuotaRepository;
import com.clubdeportivo.repository.SocioRepository;
import com.clubdeportivo.service.FileStorageService;
import com.clubdeportivo.service.impl.SocioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ========================================================================================
 * PRUEBAS UNITARIAS: SocioServiceTest
 * ========================================================================================
 * Valida la lógica de negocio del servicio de socios utilizando JUnit 5 y Mockito.
 *
 * Casos cubiertos:
 * 1. Registro exitoso de socio titular con subida de fotografía facial.
 * 2. Detección y rechazo de DNIs duplicados.
 * 3. Cálculo dinámico de la situación de la cuota familiar (Al Día, Adeuda, Vencida).
 * 4. Baja lógica (soft-delete) preservando la integridad histórica.
 * ========================================================================================
 */
@ExtendWith(MockitoExtension.class)
class SocioServiceTest {

    @Mock
    private SocioRepository socioRepository;

    @Mock
    private PagoCuotaRepository pagoCuotaRepository;

    @Mock
    private SocioMapper socioMapper;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private SocioServiceImpl socioService;

    private SocioFormDTO formDTO;
    private Socio socio;
    private SocioDTO socioDTO;

    @BeforeEach
    void setUp() {
        formDTO = new SocioFormDTO();
        formDTO.setDni("32456789");
        formDTO.setNombre("Carlos");
        formDTO.setApellido("Gómez");
        formDTO.setEmail("carlos.gomez@test.com");
        formDTO.setFechaNacimiento(LocalDate.of(1988, 5, 20));

        socio = new Socio();
        socio.setId(1L);
        socio.setDni("32456789");
        socio.setNombre("Carlos");
        socio.setApellido("Gómez");
        socio.setNumeroSocio("SOC-2026-0001");
        socio.setActivo(true);

        socioDTO = new SocioDTO();
        socioDTO.setId(1L);
        socioDTO.setDni("32456789");
        socioDTO.setNombreCompleto("GÓMEZ, Carlos");
        socioDTO.setNumeroSocio("SOC-2026-0001");
        socioDTO.setActivo(true);
    }

    @Test
    @DisplayName("Debe registrar un nuevo socio titular con fotografía de rostro y generar número de carnet")
    void testRegistrarSocioExitoso() {
        MockMultipartFile mockFile = new MockMultipartFile("fotoArchivo", "rostro.jpg", "image/jpeg", "bytes".getBytes());
        formDTO.setFotoArchivo(mockFile);

        when(socioRepository.existsByDni("32456789")).thenReturn(false);
        when(fileStorageService.almacenarArchivo(mockFile)).thenReturn("uuid-rostro.jpg");
        when(socioMapper.toEntity(formDTO, "uuid-rostro.jpg")).thenReturn(socio);
        when(socioRepository.count()).thenReturn(0L);
        when(socioRepository.save(any(Socio.class))).thenReturn(socio);
        when(socioRepository.findById(1L)).thenReturn(Optional.of(socio));
        when(socioMapper.toDTO(eq(socio), any(EstadoCuota.class))).thenReturn(socioDTO);

        SocioDTO resultado = socioService.registrarSocio(formDTO);

        assertNotNull(resultado);
        assertEquals("32456789", resultado.getDni());
        verify(fileStorageService, times(1)).almacenarArchivo(mockFile);
        verify(socioRepository, times(1)).save(any(Socio.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si ya existe un socio registrado con el mismo DNI")
    void testRegistrarSocioDniDuplicado() {
        when(socioRepository.existsByDni("32456789")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            socioService.registrarSocio(formDTO);
        });

        assertTrue(ex.getMessage().contains("Ya existe un socio registrado"));
        verify(socioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe registrar exitosamente un socio familiar vinculado a otro socio titular")
    void testRegistrarSocioFamiliarExitoso() {
        SocioFormDTO familiarForm = new SocioFormDTO();
        familiarForm.setDni("38111222");
        familiarForm.setNombre("Lucía");
        familiarForm.setApellido("Gómez");
        familiarForm.setTipoSocio(com.clubdeportivo.model.enums.TipoSocio.FAMILIAR);
        familiarForm.setSocioTitularId(1L);
        familiarForm.setParentesco(com.clubdeportivo.model.enums.Parentesco.HIJO);
        familiarForm.setFechaNacimiento(LocalDate.of(2005, 8, 12));

        Socio socioFamiliar = new Socio();
        socioFamiliar.setId(2L);
        socioFamiliar.setDni("38111222");
        socioFamiliar.setNombre("Lucía");
        socioFamiliar.setApellido("Gómez");

        SocioDTO familiarDTO = new SocioDTO();
        familiarDTO.setId(2L);
        familiarDTO.setDni("38111222");
        familiarDTO.setTipoSocio(com.clubdeportivo.model.enums.TipoSocio.FAMILIAR);
        familiarDTO.setSocioTitularId(1L);
        familiarDTO.setSocioTitularNombreCompleto("GÓMEZ, Carlos");

        when(socioRepository.existsByDni("38111222")).thenReturn(false);
        when(socioMapper.toEntity(familiarForm, null)).thenReturn(socioFamiliar);
        when(socioRepository.findById(1L)).thenReturn(Optional.of(socio));
        when(socioRepository.count()).thenReturn(1L);
        when(socioRepository.save(any(Socio.class))).thenReturn(socioFamiliar);
        when(socioRepository.findById(2L)).thenReturn(Optional.of(socioFamiliar));
        when(socioMapper.toDTO(eq(socioFamiliar), any(EstadoCuota.class))).thenReturn(familiarDTO);

        SocioDTO resultado = socioService.registrarSocio(familiarForm);

        assertNotNull(resultado);
        assertEquals(com.clubdeportivo.model.enums.TipoSocio.FAMILIAR, resultado.getTipoSocio());
        assertEquals(1L, resultado.getSocioTitularId());
        verify(socioRepository, times(1)).save(any(Socio.class));
    }

    @Test
    @DisplayName("Debe rechazar el registro de socio familiar si no se indica el socio titular")
    void testRegistrarSocioFamiliarSinTitular() {
        SocioFormDTO familiarForm = new SocioFormDTO();
        familiarForm.setDni("38111222");
        familiarForm.setTipoSocio(com.clubdeportivo.model.enums.TipoSocio.FAMILIAR);
        familiarForm.setSocioTitularId(null);

        when(socioRepository.existsByDni("38111222")).thenReturn(false);
        when(socioMapper.toEntity(familiarForm, null)).thenReturn(new Socio());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            socioService.registrarSocio(familiarForm);
        });

        assertTrue(ex.getMessage().contains("Debe seleccionar el socio titular"));
        verify(socioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe delegar el cálculo de estado de cuota del socio familiar en el titular")
    void testCalcularEstadoCuotaSocioFamiliarDelegaEnTitular() {
        Socio socioFamiliar = new Socio();
        socioFamiliar.setId(2L);
        socioFamiliar.setTipoSocio(com.clubdeportivo.model.enums.TipoSocio.FAMILIAR);
        socioFamiliar.setSocioTitular(socio);

        when(socioRepository.findById(2L)).thenReturn(Optional.of(socioFamiliar));
        when(socioRepository.findById(1L)).thenReturn(Optional.of(socio));

        LocalDate hoy = LocalDate.now();
        when(pagoCuotaRepository.existsBySocioIdAndPeriodoAnioAndPeriodoMes(1L, hoy.getYear(), hoy.getMonthValue()))
                .thenReturn(true);

        EstadoCuota estado = socioService.calcularEstadoCuota(2L);

        assertEquals(EstadoCuota.AL_DIA, estado);
    }

    @Test
    @DisplayName("Debe determinar que la cuota está AL_DIA si el mes actual está abonado")
    void testCalcularEstadoCuotaAlDia() {
        LocalDate hoy = LocalDate.now();
        when(socioRepository.findById(1L)).thenReturn(Optional.of(socio));
        when(pagoCuotaRepository.existsBySocioIdAndPeriodoAnioAndPeriodoMes(1L, hoy.getYear(), hoy.getMonthValue()))
                .thenReturn(true);

        EstadoCuota estado = socioService.calcularEstadoCuota(1L);

        assertEquals(EstadoCuota.AL_DIA, estado);
    }

    @Test
    @DisplayName("Debe determinar que ADEUDA si no pagó el mes actual pero sí pagó el mes previo")
    void testCalcularEstadoCuotaAdeuda() {
        LocalDate hoy = LocalDate.now();
        LocalDate mesAnt = hoy.minusMonths(1);

        when(socioRepository.findById(1L)).thenReturn(Optional.of(socio));
        when(pagoCuotaRepository.existsBySocioIdAndPeriodoAnioAndPeriodoMes(1L, hoy.getYear(), hoy.getMonthValue()))
                .thenReturn(false);
        when(pagoCuotaRepository.existsBySocioIdAndPeriodoAnioAndPeriodoMes(1L, mesAnt.getYear(), mesAnt.getMonthValue()))
                .thenReturn(true);

        EstadoCuota estado = socioService.calcularEstadoCuota(1L);

        assertEquals(EstadoCuota.ADEUDA, estado);
    }

    @Test
    @DisplayName("Debe marcar al socio como inactivo al dar de baja (Soft-Delete)")
    void testDarDeBajaSocio() {
        when(socioRepository.findById(1L)).thenReturn(Optional.of(socio));

        socioService.darDeBaja(1L);

        assertFalse(socio.isActivo());
        verify(socioRepository, times(1)).save(socio);
    }

    @Test
    @DisplayName("Debe listar correctamente a los socios titulares activos para el formulario de vinculación")
    void testListarSociosTitularesActivos() {
        when(socioRepository.findSociosTitularesActivos(null)).thenReturn(java.util.List.of(socio));
        when(socioRepository.findById(1L)).thenReturn(Optional.of(socio));
        when(socioMapper.toDTO(eq(socio), any(EstadoCuota.class))).thenReturn(socioDTO);

        java.util.List<SocioDTO> resultado = socioService.listarSociosTitularesActivos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("GÓMEZ, Carlos", resultado.get(0).getNombreCompleto());
    }
}
