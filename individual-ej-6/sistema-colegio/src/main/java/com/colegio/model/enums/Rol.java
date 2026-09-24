package com.colegio.model.enums;

/**
 * Roles de seguridad de la aplicación (Spring Security).
 *
 *  - ADMIN   : administra el colegio -> ABM de Grados, Aulas, Materias,
 *              Alumnos, Docentes y Asignaciones (qué docente dicta qué
 *              materia en qué aula).
 *  - DOCENTE : sólo ve/gestiona sus propias asignaciones y carga las notas
 *              de los alumnos de las materias que dicta.
 *
 * getAuthority() arma el nombre exigido por Spring Security, que por
 * convención debe empezar con el prefijo "ROLE_" (lo usan las expresiones
 * hasRole('ADMIN') tanto en SecurityConfig como en las vistas con
 * sec:authorize="hasRole('ADMIN')").
 */
public enum Rol {
    ADMIN,
    DOCENTE;

    public String getAuthority() {
        return "ROLE_" + name();
    }
}
