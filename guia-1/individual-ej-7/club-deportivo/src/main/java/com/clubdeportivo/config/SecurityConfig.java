package com.clubdeportivo.config;

import com.clubdeportivo.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * ========================================================================================
 * CONFIGURACIÓN DE SEGURIDAD: SecurityConfig (Spring Security 6)
 * ========================================================================================
 * Establece la arquitectura de autenticación y autorización del sistema.
 *
 * Anotaciones utilizadas:
 * - @Configuration: Declara la clase como proveedora de beans en el ApplicationContext.
 * - @EnableWebSecurity: Habilita el soporte de seguridad web de Spring Security y la cadena de filtros.
 * - @EnableMethodSecurity: Permite seguridad granular a nivel de métodos (@PreAuthorize).
 *
 * Características implementadas:
 * 1. Encriptación robusta de contraseñas mediante algoritmo BCrypt con factor de costo configurable.
 * 2. Formulario de autenticación personalizado adaptado visualmente a la plantilla Sneat.
 * 3. Control de acceso perimetral según roles (ADMIN, RECEPCIONISTA, SOCIO).
 * 4. Protección contra ataques Cross-Site Request Forgery (CSRF).
 * 5. Gestión de sesiones seguras y cierre de sesión explícito.
 * ========================================================================================
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    /**
     * Bean de encriptación de contraseñas mediante función hash adaptativa unidireccional BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Proveedor de autenticación DAO que vincula nuestro CustomUserDetailsService con BCrypt.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Administrador de autenticación central expuesto como bean.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Cadena principal de filtros de seguridad HTTP (SecurityFilterChain).
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        // Recursos estáticos públicos (CSS, JS, iconos de la plantilla Sneat y fotos subidas)
                        .requestMatchers(
                                new AntPathRequestMatcher("/assets/**"),
                                new AntPathRequestMatcher("/uploads/**"),
                                new AntPathRequestMatcher("/favicon.ico"),
                                new AntPathRequestMatcher("/login"),
                                new AntPathRequestMatcher("/error")
                        ).permitAll()

                        // Módulos con restricción por perfil
                        .requestMatchers(new AntPathRequestMatcher("/usuarios/**")).hasRole("ADMIN")
                        .requestMatchers(new AntPathRequestMatcher("/socios/eliminar/**")).hasRole("ADMIN")
                        .requestMatchers(new AntPathRequestMatcher("/socios/**")).hasAnyRole("ADMIN", "RECEPCIONISTA")
                        .requestMatchers(new AntPathRequestMatcher("/familiares/**")).hasAnyRole("ADMIN", "RECEPCIONISTA")
                        .requestMatchers(new AntPathRequestMatcher("/accesos/**")).hasAnyRole("ADMIN", "RECEPCIONISTA")
                        .requestMatchers(new AntPathRequestMatcher("/pagos/**")).hasAnyRole("ADMIN", "RECEPCIONISTA")

                        // Cualquier otra ruta requiere usuario autenticado
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/error?denied=true")
                );

        return http.build();
    }
}
