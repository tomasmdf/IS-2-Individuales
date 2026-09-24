package com.clubdeportivo.config;

import com.clubdeportivo.model.*;
import com.clubdeportivo.model.enums.*;
import com.clubdeportivo.repository.*;
import com.clubdeportivo.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ========================================================================================
 * SEMILLA DE DATOS: DataInitializer
 * ========================================================================================
 * Inicializa automáticamente datos representativos en el arranque de la aplicación:
 * 1. Usuarios con roles (Administrador y Operador de Recepción) con contraseñas encriptadas.
 * 2. Socios titulares con fotografías faciales para el control biométrico visual.
 * 3. Integrantes de grupos familiares dependientes.
 * 4. Historial de cobranzas con diferentes medios de pago (Efectivo, Transferencia, Mercado Pago).
 * 5. Registros de accesos perimetrales (Entradas y Salidas).
 * ========================================================================================
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UsuarioRepository usuarioRepository;
    private final SocioRepository socioRepository;
    private final FamiliarRepository familiarRepository;
    private final PagoCuotaRepository pagoCuotaRepository;
    private final RegistroAccesoRepository registroAccesoRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           SocioRepository socioRepository,
                           FamiliarRepository familiarRepository,
                           PagoCuotaRepository pagoCuotaRepository,
                           RegistroAccesoRepository registroAccesoRepository,
                           PasswordEncoder passwordEncoder,
                           FileStorageService fileStorageService) {
        this.usuarioRepository = usuarioRepository;
        this.socioRepository = socioRepository;
        this.familiarRepository = familiarRepository;
        this.pagoCuotaRepository = pagoCuotaRepository;
        this.registroAccesoRepository = registroAccesoRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public void run(String... args) {
        inicializarUsuarios();
        inicializarImagenesDeMuestra();
        inicializarSociosYFamiliares();
        migrarSociosExistentesSinTipo();
        inicializarPagos();
        inicializarAccesos();
        log.info("=== SISTEMA CLUB DEPORTIVO INICIALIZADO EXITOSAMENTE CON DATOS DE DEMOSTRACIÓN ===");
    }

    private void inicializarUsuarios() {
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario(
                    "admin",
                    passwordEncoder.encode("admin123"),
                    "Administrador General",
                    "admin@clubdeportivo.com",
                    Rol.ROLE_ADMIN
            );
            usuarioRepository.save(admin);

            Usuario recepcionista = new Usuario(
                    "recepcion",
                    passwordEncoder.encode("recep123"),
                    "Lucía Fernández (Recepción)",
                    "recepcion@clubdeportivo.com",
                    Rol.ROLE_RECEPCIONISTA
            );
            usuarioRepository.save(recepcionista);

            log.info("Usuarios creados: admin/admin123 (ADMIN), recepcion/recep123 (RECEPCIONISTA)");
        }
    }

    private void inicializarImagenesDeMuestra() {
        try {
            Path destinoDirectorio = fileStorageService.getRootLocation();
            Files.createDirectories(destinoDirectorio);

            for (int i = 1; i <= 4; i++) {
                String avatarName = i + ".png";
                Path archivoDestino = destinoDirectorio.resolve("avatar-" + avatarName);
                if (!Files.exists(archivoDestino)) {
                    try (InputStream is = getClass().getResourceAsStream("/static/assets/img/avatars/" + avatarName)) {
                        if (is != null) {
                            Files.copy(is, archivoDestino, StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("No se pudieron copiar los avatares iniciales: {}", e.getMessage());
        }
    }

    private void inicializarSociosYFamiliares() {
        if (socioRepository.count() == 0) {
            // Socio 1: Carlos Gómez (Familia al día)
            Socio socio1 = new Socio();
            socio1.setNumeroSocio("SOC-2026-0001");
            socio1.setDni("32456789");
            socio1.setNombre("Carlos Alberto");
            socio1.setApellido("Gómez");
            socio1.setEmail("carlos.gomez@email.com");
            socio1.setTelefono("+54 11 4567-8901");
            socio1.setDireccion("Av. del Libertador 4520, CABA");
            socio1.setFechaNacimiento(LocalDate.of(1986, 5, 14));
            socio1.setFechaAlta(LocalDate.of(2023, 1, 15));
            socio1.setFotoRostro("avatar-1.png");
            socio1.setActivo(true);
            socio1.setTipoSocio(TipoSocio.TITULAR);

            Familiar fam1 = new Familiar(socio1, "33987456", "Mariana", "Pérez", Parentesco.CONYUGE, LocalDate.of(1988, 8, 22));
            fam1.setFotoRostro("avatar-2.png");
            socio1.agregarFamiliar(fam1);

            Familiar fam2 = new Familiar(socio1, "52145896", "Tomás", "Gómez", Parentesco.HIJO, LocalDate.of(2015, 3, 10));
            fam2.setFotoRostro("avatar-3.png");
            socio1.agregarFamiliar(fam2);

            socioRepository.save(socio1);

            // Socio 2: Laura Martínez (Adeuda cuota actual)
            Socio socio2 = new Socio();
            socio2.setNumeroSocio("SOC-2026-0002");
            socio2.setDni("29876543");
            socio2.setNombre("Laura Viviana");
            socio2.setApellido("Martínez");
            socio2.setEmail("laura.martinez@email.com");
            socio2.setTelefono("+54 11 5678-1234");
            socio2.setDireccion("Calle San Martín 1234, San Isidro");
            socio2.setFechaNacimiento(LocalDate.of(1982, 11, 28));
            socio2.setFechaAlta(LocalDate.of(2023, 6, 1));
            socio2.setFotoRostro("avatar-4.png");
            socio2.setActivo(true);
            socio2.setTipoSocio(TipoSocio.TITULAR);

            Familiar fam3 = new Familiar(socio2, "50321654", "Sofía", "Martínez", Parentesco.HIJO, LocalDate.of(2012, 9, 5));
            fam3.setFotoRostro("avatar-1.png");
            socio2.agregarFamiliar(fam3);

            socioRepository.save(socio2);

            // Socio 3: Roberto Rossi (Moroso / Cuotas vencidas)
            Socio socio3 = new Socio();
            socio3.setNumeroSocio("SOC-2026-0003");
            socio3.setDni("25412365");
            socio3.setNombre("Roberto Martín");
            socio3.setApellido("Rossi");
            socio3.setEmail("roberto.rossi@email.com");
            socio3.setTelefono("+54 11 6789-4321");
            socio3.setDireccion("Av. Santa Fe 3420, CABA");
            socio3.setFechaNacimiento(LocalDate.of(1976, 4, 3));
            socio3.setFechaAlta(LocalDate.of(2022, 3, 10));
            socio3.setFotoRostro("avatar-2.png");
            socio3.setActivo(true);
            socio3.setTipoSocio(TipoSocio.TITULAR);

            socioRepository.save(socio3);

            log.info("Socios y familiares inicializados.");
        }
    }

    /**
     * Asegura la consistencia de datos históricos asignando la categoría TITULAR a registros
     * preexistentes que posean el campo tipo_socio nulo en MySQL.
     */
    private void migrarSociosExistentesSinTipo() {
        try {
            java.util.List<Socio> socios = socioRepository.findAll();
            int actualizados = 0;
            for (Socio s : socios) {
                if (s.getTipoSocio() == null || s.getTipoSocio() == TipoSocio.TITULAR && s.getSocioTitular() != null) {
                    s.setTipoSocio(s.getSocioTitular() != null ? TipoSocio.FAMILIAR : TipoSocio.TITULAR);
                    socioRepository.save(s);
                    actualizados++;
                }
            }
            if (actualizados > 0) {
                log.info("Migración de datos ejecutada: {} socios asignados a su categoría correspondiente.", actualizados);
            }
        } catch (Exception e) {
            log.warn("Aviso durante la verificación de categorías de socios: {}", e.getMessage());
        }
    }

    private void inicializarPagos() {
        if (pagoCuotaRepository.count() == 0) {
            Socio socio1 = socioRepository.findByDni("32456789").orElse(null);
            Socio socio2 = socioRepository.findByDni("29876543").orElse(null);

            LocalDate hoy = LocalDate.now();
            int mesActual = hoy.getMonthValue();
            int anioActual = hoy.getYear();

            if (socio1 != null) {
                // Pago del mes actual en Mercado Pago
                PagoCuota pago1 = new PagoCuota(
                        socio1,
                        mesActual,
                        anioActual,
                        new BigDecimal("15000.00"),
                        MedioPago.MERCADO_PAGO,
                        "REC-" + anioActual + String.format("%02d", mesActual) + "-MP8921",
                        "Abonado con QR Mercado Pago - Cuota Grupo Familiar"
                );
                pagoCuotaRepository.save(pago1);

                // Pago del mes anterior en Transferencia
                LocalDate mesAnt = hoy.minusMonths(1);
                PagoCuota pagoAnt = new PagoCuota(
                        socio1,
                        mesAnt.getMonthValue(),
                        mesAnt.getYear(),
                        new BigDecimal("15000.00"),
                        MedioPago.TRANSFERENCIA,
                        "REC-" + mesAnt.getYear() + String.format("%02d", mesAnt.getMonthValue()) + "-TR4412",
                        "Transferencia bancaria comprobante #982341"
                );
                pagoCuotaRepository.save(pagoAnt);
            }

            if (socio2 != null) {
                // Solo pagó el mes anterior en Efectivo (por ende, adeuda el mes en curso)
                LocalDate mesAnt = hoy.minusMonths(1);
                PagoCuota pago2 = new PagoCuota(
                        socio2,
                        mesAnt.getMonthValue(),
                        mesAnt.getYear(),
                        new BigDecimal("15000.00"),
                        MedioPago.EFECTIVO,
                        "REC-" + mesAnt.getYear() + String.format("%02d", mesAnt.getMonthValue()) + "-EF1054",
                        "Pago en caja recepción en efectivo"
                );
                pagoCuotaRepository.save(pago2);
            }

            log.info("Pagos de prueba inicializados.");
        }
    }

    private void inicializarAccesos() {
        if (registroAccesoRepository.count() == 0) {
            Socio socio1 = socioRepository.findByDni("32456789").orElse(null);
            if (socio1 != null) {
                RegistroAcceso reg1 = new RegistroAcceso(
                        TipoAcceso.ENTRADA,
                        socio1.getDni(),
                        socio1.getNombreCompleto(),
                        socio1.getFotoRostro(),
                        true,
                        socio1.getId(),
                        null,
                        EstadoCuota.AL_DIA,
                        "Molinete Principal #1",
                        "Ingreso a gimnasio"
                );
                reg1.setFechaHora(LocalDateTime.now().minusHours(2));
                registroAccesoRepository.save(reg1);

                RegistroAcceso reg2 = new RegistroAcceso(
                        TipoAcceso.SALIDA,
                        socio1.getDni(),
                        socio1.getNombreCompleto(),
                        socio1.getFotoRostro(),
                        true,
                        socio1.getId(),
                        null,
                        EstadoCuota.AL_DIA,
                        "Molinete Principal #1",
                        "Salida regular"
                );
                reg2.setFechaHora(LocalDateTime.now().minusMinutes(35));
                registroAccesoRepository.save(reg2);
            }
            log.info("Registros de acceso inicializados.");
        }
    }
}
