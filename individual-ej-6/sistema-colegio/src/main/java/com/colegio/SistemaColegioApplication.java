package com.colegio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * =============================================================================
 * Clase de arranque de la aplicación (entry point).
 * =============================================================================
 * @SpringBootApplication es una anotación "combo" que equivale a:
 *   - @Configuration       -> esta clase puede declarar Beans (@Bean).
 *   - @EnableAutoConfiguration -> Spring Boot configura automáticamente Tomcat,
 *         Thymeleaf, Hibernate/JPA, Spring Security, etc. según lo que
 *         encuentra en el classpath (las dependencias del pom.xml).
 *   - @ComponentScan       -> escanea el paquete "com.colegio" y subpaquetes
 *         buscando @Component, @Service, @Repository, @Controller, etc.
 *
 * @EnableJpaAuditing habilita la AUDITORÍA DE ENTIDADES de Spring Data JPA:
 *   permite que los campos anotados con @CreatedDate, @LastModifiedDate,
 *   @CreatedBy y @LastModifiedBy (ver model.entity.Auditable) se completen
 *   solos en cada INSERT/UPDATE. auditorAwareRef apunta al Bean que le dice
 *   a Spring "quién es el usuario actual" (ver config.AuditoriaConfig).
 *
 * @EnableAsync habilita el envío ASÍNCRONO del correo de bienvenida
 *   (ver service.EmailService), para que registrar un docente no quede
 *   esperando a que el servidor SMTP responda.
 * =============================================================================
 */
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableAsync
public class SistemaColegioApplication {

    public static void main(String[] args) {
        SpringApplication.run(SistemaColegioApplication.class, args);
    }
}
