package com.colegio.model.enums;

/**
 * Sexo biológico registrado del docente (requisito del enunciado: "Sexo").
 * Un enum de Java se mapea en la base de datos mediante @Enumerated(EnumType.STRING)
 * en la entidad, guardando el texto ("MASCULINO"/"FEMENINO") en lugar del índice
 * numérico (0/1), que sería frágil ante cambios futuros en el orden del enum.
 */
public enum Sexo {
    MASCULINO,
    FEMENINO
}
