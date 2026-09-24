package com.colegio.config;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.colegio.model.entity.Alumno;
import com.colegio.model.entity.Aula;
import com.colegio.model.entity.Docente;
import com.colegio.model.entity.Grado;
import com.colegio.model.entity.Materia;
import com.colegio.model.enums.Rol;
import com.colegio.model.enums.Sexo;
import com.colegio.repository.AlumnoRepository;
import com.colegio.repository.AulaRepository;
import com.colegio.repository.DocenteRepository;
import com.colegio.repository.GradoRepository;
import com.colegio.repository.MateriaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * =============================================================================
 * CommandLineRunner: Spring Boot ejecuta el método run(...) de todo Bean que
 * implemente esta interfaz UNA SOLA VEZ, justo después de que el contexto de
 * la aplicación terminó de inicializarse (y antes de aceptar pedidos HTTP).
 * =============================================================================
 * Se usa aquí para:
 *   1) garantizar que exista al menos un usuario ADMIN para poder ingresar
 *      la primera vez (credenciales configurables por app.admin.* en
 *      application.properties, ver README para cambiarlas);
 *   2) opcionalmente (app.seed-demo-data=true) cargar Grados/Aulas/Materias/
 *      Alumnos de ejemplo, para poder probar la aplicación sin cargar todo
 *      a mano.
 * Todas las inserciones son idempotentes (verifican "si no existe" antes de
 * crear), por lo que es seguro reiniciar la aplicación varias veces.
 * =============================================================================
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final DocenteRepository docenteRepository;
    private final GradoRepository gradoRepository;
    private final AulaRepository aulaRepository;
    private final MateriaRepository materiaRepository;
    private final AlumnoRepository alumnoRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.correo}")
    private String adminCorreo;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.seed-demo-data}")
    private boolean sembrarDatosDemo;

    @Override
    public void run(String... args) {
        crearAdminSiNoExiste();
        if (sembrarDatosDemo) {
            sembrarDatosDemo();
        }
    }

    private void crearAdminSiNoExiste() {
        if (docenteRepository.existsByCorreo(adminCorreo)) {
            return;
        }
        Docente admin = Docente.builder()
                .nombre("Administrador")
                .apellido("Sistema")
                .sexo(Sexo.MASCULINO)
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .correo(adminCorreo)
                .passwordHash(passwordEncoder.encode(adminPassword))
                .rol(Rol.ADMIN)
                .activo(true)
                .build();
        docenteRepository.save(admin);
        log.info("Usuario ADMIN inicial creado -> correo: {} / contraseña: {}", adminCorreo, adminPassword);
    }

    private void sembrarDatosDemo() {
        if (gradoRepository.count() > 0) {
            return; // ya hay datos cargados, no volver a sembrar
        }
        Grado primero = gradoRepository.save(Grado.builder().nombre("1er Año").nivel("Secundario").build());
        Grado segundo = gradoRepository.save(Grado.builder().nombre("2do Año").nivel("Secundario").build());

        Aula primeroA = aulaRepository.save(Aula.builder().grado(primero).division("A").capacidad(30).build());
        aulaRepository.save(Aula.builder().grado(primero).division("B").capacidad(30).build());
        Aula segundoA = aulaRepository.save(Aula.builder().grado(segundo).division("A").capacidad(28).build());

        materiaRepository.save(Materia.builder().nombre("Matemática").descripcion("Álgebra y geometría básica").build());
        materiaRepository.save(Materia.builder().nombre("Lengua y Literatura").descripcion("Comprensión y producción de textos").build());
        materiaRepository.save(Materia.builder().nombre("Historia").descripcion("Historia argentina y latinoamericana").build());

        alumnoRepository.save(Alumno.builder()
                .nombre("Sofía").apellido("Gómez").dni("40111222").sexo(Sexo.FEMENINO)
                .fechaNacimiento(LocalDate.of(2011, 3, 12)).grado(primero).aula(primeroA).build());
        alumnoRepository.save(Alumno.builder()
                .nombre("Mateo").apellido("Pérez").dni("40222333").sexo(Sexo.MASCULINO)
                .fechaNacimiento(LocalDate.of(2010, 7, 5)).grado(segundo).aula(segundoA).build());

        log.info("Datos de demostración cargados (grados, aulas, materias y alumnos de ejemplo).");
    }
}
