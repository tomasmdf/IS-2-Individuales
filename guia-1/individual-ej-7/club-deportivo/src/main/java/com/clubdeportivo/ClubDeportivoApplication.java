package com.clubdeportivo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * ========================================================================================
 * CLASE PRINCIPAL: ClubDeportivoApplication
 * ========================================================================================
 * Punto de entrada principal para el inicio de la aplicación Spring Boot.
 *
 * Anotaciones utilizadas:
 * - @SpringBootApplication: Meta-anotación que combina:
 *   1. @Configuration: Declara la clase como fuente de definiciones de beans en el contexto.
 *   2. @EnableAutoConfiguration: Habilita el mecanismo de autoconfiguración inteligente de Spring Boot,
 *      configurando automáticamente DataSource, Hibernate, Tomcat, DispatcherServlet y Thymeleaf.
 *   3. @ComponentScan: Escanea recursivamente el paquete base "com.clubdeportivo" y sus subpaquetes
 *      buscando componentes anotados con @Component, @Service, @Repository, @Controller, etc.
 *
 * - @EnableJpaAuditing(auditorAwareRef = "securityAuditorAware"):
 *   Activa el soporte de auditoría automática provisto por Spring Data JPA. Permite que las
 *   propiedades anotadas con @CreatedDate, @LastModifiedDate, @CreatedBy y @LastModifiedBy
 *   en la superclase AuditableEntity se completen de manera transparente con el usuario
 *   autenticado y las marcas de tiempo correspondientes.
 * ========================================================================================
 */
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "securityAuditorAware")
public class ClubDeportivoApplication {

    public static void main(String[] args) {
        // Inicializa el contexto de Spring (ApplicationContext) y arranca el servidor embebido Tomcat
        SpringApplication.run(ClubDeportivoApplication.class, args);
    }
}
