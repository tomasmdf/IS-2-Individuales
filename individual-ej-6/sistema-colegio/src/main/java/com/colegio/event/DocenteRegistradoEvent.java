package com.colegio.event;

import com.colegio.model.entity.Docente;

/**
 * Evento de aplicación (Spring ApplicationEvent, vía ApplicationEventPublisher)
 * publicado justo después de registrar un docente nuevo. Desacopla el
 * "guardar el docente" del "enviar el correo de bienvenida": DocenteService
 * no conoce ni depende de EmailService directamente, sólo publica el hecho
 * de que ocurrió un alta; quien esté interesado (DocenteRegistradoListener)
 * reacciona a él.
 */
public record DocenteRegistradoEvent(Docente docente, String passwordSinCifrar) {
}
