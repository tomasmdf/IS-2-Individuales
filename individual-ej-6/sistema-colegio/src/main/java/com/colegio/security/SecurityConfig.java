package com.colegio.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

/**
 * =============================================================================
 * CONFIGURACIÓN DE SEGURIDAD (Spring Security 6)
 * =============================================================================
 * Define:
 *   1) Cómo se codifican las contraseñas (BCrypt).
 *   2) Cómo se autentica un docente (DaoAuthenticationProvider, que usa
 *      nuestro CustomUserDetailsService + el PasswordEncoder).
 *   3) Qué URLs son públicas y cuáles requieren estar logueado o tener un
 *      rol determinado (autorización).
 *   4) El formulario de login propio (Thymeleaf, plantilla Sneat) en vez del
 *      formulario genérico que Spring Security muestra por defecto.
 * =============================================================================
 * @EnableMethodSecurity habilita las anotaciones @PreAuthorize / @PostAuthorize
 * sobre métodos de Controller/Service (usadas, por ejemplo, en AlumnoController
 * para exigir ROLE_ADMIN sólo en el alta/edición/baja de alumnos), como
 * complemento más fino a las reglas por URL definidas más abajo.
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    /**
     * BCrypt aplica un algoritmo de hash adaptativo con "salt" aleatorio
     * incorporado: dos contraseñas iguales producen hashes distintos, y no es
     * posible (en la práctica) revertir el hash para obtener la contraseña
     * original. Se usa tanto al registrar/cambiar contraseña (encode) como
     * al hacer login (matches), a través del AuthenticationProvider.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * DaoAuthenticationProvider: implementación estándar de Spring Security
     * que autentica contra una base de datos (DAO = Data Access Object) a
     * través de un UserDetailsService, comparando la contraseña con el
     * PasswordEncoder configurado.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    /** Expone el AuthenticationManager por si se necesita autenticar programáticamente. */
    @Bean
    public org.springframework.security.authentication.AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Cadena de filtros HTTP: reglas de autorización por URL + configuración
     * del login/logout basado en formulario.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                // Recursos públicos: página de login y assets estáticos (CSS/JS/imágenes de Sneat)
                .requestMatchers("/login", "/assets/**", "/webjars/**", "/favicon.ico").permitAll()
                // ABM administrativo: sólo ADMIN
                .requestMatchers("/docentes/**", "/grados/**", "/aulas/**", "/materias/**", "/asignaciones/**")
                    .hasRole("ADMIN")
                // Alumnos: ADMIN gestiona el ABM completo; DOCENTE sólo puede listar/ver (se restringe fino en el Controller)
                .requestMatchers("/alumnos/**").hasAnyRole("ADMIN", "DOCENTE")
                // Notas: el propio docente carga notas de sus asignaciones; el admin supervisa
                .requestMatchers("/notas/**").hasAnyRole("ADMIN", "DOCENTE")
                // Cuenta propia (perfil, cambio de contraseña): cualquier usuario autenticado
                .requestMatchers("/cuenta/**").authenticated()
                .requestMatchers("/", "/dashboard").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(withDefaults())
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )
            // La sesión HTTP guarda quién está logueado entre pedido y pedido (patrón MVC clásico
            // basado en sesión, adecuado para una aplicación server-rendered con Thymeleaf).
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            // CSRF queda HABILITADO (comportamiento por defecto): cada <form> de Thymeleaf incluye
            // automáticamente un campo oculto "_csrf" (vía el atributo th:action) que Spring Security
            // valida en cada POST/PUT/DELETE, protegiendo contra ataques Cross-Site Request Forgery.
            .exceptionHandling(handling -> handling.accessDeniedPage("/acceso-denegado"));

        return http.build();
    }

    /** Personaliza la página de login propia y a dónde redirige tras un login exitoso. */
    private static org.springframework.security.config.Customizer<FormLoginConfigurer<HttpSecurity>> withDefaults() {
        return form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login") // URL que procesa el POST del <form> de login
                .usernameParameter("correo")
                .passwordParameter("password")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error");
    }
}
