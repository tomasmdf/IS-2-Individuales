package com.ejercicio.sistemaregistro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * =============================================================================
 * CLASE PRINCIPAL DE LA APLICACIÓN
 * =============================================================================
 * Punto de entrada ("main") de toda la aplicación Spring Boot.
 *
 * @SpringBootApplication es una anotación "combo" que agrupa a su vez:
 *   - @Configuration    : indica que la clase puede definir beans de configuración.
 *   - @EnableAutoConfiguration : le dice a Spring Boot que configure automáticamente
 *                          el servidor Tomcat embebido, Thymeleaf, el DataSource
 *                          hacia MySQL, JPA/Hibernate, etc. en base a las
 *                          dependencias que están en el pom.xml.
 *   - @ComponentScan    : le indica a Spring que escanee este paquete y sus
 *                          subpaquetes (controller, service, repository, model,
 *                          config, dto) en busca de clases anotadas con
 *                          @Controller, @Service, @Repository, @Component, etc.
 *                          para registrarlas como "beans" administrados por el
 *                          contenedor de Spring (Inversión de Control - IoC).
 *
 * Al ejecutar SpringApplication.run(...) se levanta:
 *   1) El servidor web embebido (Tomcat) en el puerto configurado (8080 por defecto).
 *   2) El contexto de Spring con todos los beans (controladores, servicios,
 *      repositorios) ya instanciados e "inyectados" entre sí.
 *   3) La conexión con la base de datos MySQL y la sincronización de tablas
 *      (según spring.jpa.hibernate.ddl-auto) usando el ORM Hibernate.
 * =============================================================================
 */
@SpringBootApplication
public class SistemaRegistroApplication {

    public static void main(String[] args) {
        SpringApplication.run(SistemaRegistroApplication.class, args);
    }
}
