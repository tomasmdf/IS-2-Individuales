package com.empresa.compras.config;

import com.empresa.compras.security.UsuarioDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * ============================================================================
 * SecurityConfig
 * ============================================================================
 * Configuracion de Spring Security: implementa el requisito de la consigna
 * de que "quienes acceden al sistema lo realizan por medio de un usuario y
 * contraseña".
 *
 * - Login basado en formulario HTML (Thymeleaf, plantilla Sneat) contra la
 *   tabla "usuarios" (a traves de UsuarioDetailsServiceImpl).
 * - Contraseñas encriptadas con BCrypt (PasswordEncoder), nunca en texto plano.
 * - Autorizacion por rol: la administracion de Usuarios queda restringida
 *   al rol ADMIN; el resto de los modulos (Productos, Proveedores, Ordenes
 *   de Compra) requiere solamente estar autenticado.
 * - Los recursos estaticos de la plantilla Sneat (/assets/**) quedan
 *   publicos para que el login y las paginas puedan renderizar sus estilos.
 * ============================================================================
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UsuarioDetailsServiceImpl usuarioDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(usuarioDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public org.springframework.security.authentication.AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Recursos publicos: assets de la plantilla Sneat y la pantalla de login
                .requestMatchers("/assets/**", "/login", "/css/**", "/js/**").permitAll()
                // Solo ADMIN puede administrar usuarios del sistema
                .requestMatchers("/usuarios/**").hasRole("ADMIN")
                // Cualquier otra URL requiere estar autenticado
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            )
            // CSRF se mantiene habilitado (default de Spring Security); los formularios
            // Thymeleaf incluyen el token automaticamente a traves de th:action.
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
            .authenticationProvider(authenticationProvider());

        return http.build();
    }
}
