package com.empresa.compras;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ============================================================================
 * ComprasApplication
 * ============================================================================
 * Clase de arranque (bootstrap) de la aplicacion Spring Boot.
 *
 * CONTEXTO DEL PROBLEMA (INCISO A):
 *   Empresa de venta de productos de tecnologia. El stock de dicha empresa
 *   se actualiza mediante COMPRAS a proveedores mayoristas, registradas por
 *   medio de ORDENES DE COMPRA que contienen el detalle (productos,
 *   cantidades y precios) de cada compra. El acceso al sistema requiere
 *   autenticacion por usuario y contraseña.
 *
 * ARQUITECTURA (patron MVC en capas):
 *   1) VISTA (View)        -> plantillas Thymeleaf (src/main/resources/templates)
 *                              maquetadas con la plantilla admin "Sneat".
 *   2) CONTROLADOR (Controller) -> paquete controller. Recibe las peticiones
 *      HTTP, invoca a la capa de Service y arma el Model para la vista.
 *   3) MODELO (Model)      -> compuesto por:
 *        - entity     : clases anotadas con JPA (@Entity) que se mapean 1:1
 *                        a tablas de la base de datos (ORM).
 *        - repository : interfaces Spring Data JPA que abstraen el acceso
 *                        a datos (CRUD) sin necesidad de escribir SQL.
 *        - service    : logica de negocio y VALIDACIONES. Es la unica capa
 *                        que puede tomar decisiones de negocio (por ejemplo,
 *                        no permitir una compra sin detalle, o actualizar
 *                        el stock al recibir una orden).
 *        - dto        : objetos planos (Data Transfer Object) que viajan
 *                        entre Controller <-> Service <-> Vista, evitando
 *                        exponer directamente las entidades JPA.
 *
 * SEGURIDAD:
 *   Se utiliza Spring Security con autenticacion basada en formulario
 *   (usuario + contraseña), contra la tabla "usuarios" de la base de datos.
 *   Las contraseñas se almacenan hasheadas con BCrypt (nunca en texto plano).
 * ============================================================================
 */
@SpringBootApplication
public class ComprasApplication {

    public static void main(String[] args) {
        SpringApplication.run(ComprasApplication.class, args);
    }

}
