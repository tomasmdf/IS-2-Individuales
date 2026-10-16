package com.colegio.event;

import com.colegio.model.entity.Docente;

/**
 * Evento publicado cuando el ADMINISTRADOR restablece la contraseña de un
 * docente (por ejemplo, porque el docente perdió el correo de bienvenida
 * original o nunca le llegó). Al igual que DocenteRegistradoEvent, lleva la
 * contraseña SIN CIFRAR sólo de forma transitoria en memoria, únicamente
 * para poder incluirla en el correo; nunca se persiste en texto plano.
 */
public record PasswordRestablecidaEvent(Docente docente, String passwordSinCifrar) {
}
