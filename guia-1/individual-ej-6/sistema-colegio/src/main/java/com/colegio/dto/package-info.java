/**
 * =============================================================================
 * CAPA DTO (Data Transfer Object)
 * =============================================================================
 * Ningún @Controller ni ninguna vista Thymeleaf recibe o devuelve una
 * @Entity directamente. En su lugar viajan objetos de este paquete:
 *
 *   - "...Request"  : lo que entra a la aplicación (formulario -> Controller
 *                      -> Service). Llevan anotaciones de Bean Validation
 *                      (@NotBlank, @Email, @Past, etc.) que Spring valida
 *                      con @Valid antes de tocar la base de datos.
 *   - "...Response"  : lo que sale de la aplicación (Service -> Controller
 *                      -> vista). Sólo exponen los datos que la pantalla
 *                      necesita mostrar (nunca, por ejemplo, el hash de la
 *                      contraseña de un Docente).
 *
 * ¿Por qué no usar las @Entity directamente en la vista?
 *   1) Seguridad: una @Entity puede tener campos sensibles (passwordHash)
 *      o relaciones @ManyToOne/@OneToMany que, si Thymeleaf las recorre
 *      fuera de una transacción, disparan LazyInitializationException
 *      (recordar que application.properties define spring.jpa.open-in-view=false).
 *   2) Desacoplamiento: el modelo de base de datos puede cambiar (nueva
 *      columna, relación distinta) sin romper el contrato con la vista.
 *   3) Forma a medida: una pantalla puede necesitar combinar datos de
 *      varias entidades (por ej. NotaResponse trae el nombre del alumno Y
 *      de la materia, no sólo sus IDs).
 *
 * Se implementan como "record" de Java (desde Java 16): clases inmutables
 * y muy concisas que generan automáticamente constructor, getters,
 * equals/hashCode y toString a partir de los campos declarados.
 * =============================================================================
 */
package com.colegio.dto;
