package com.colegio.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.colegio.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * "Escucha" el evento DocenteRegistradoEvent y envía el correo de
 * bienvenida.
 *
 * @TransactionalEventListener(phase = AFTER_COMMIT): el correo recién se
 *   envía DESPUÉS de que la transacción que guardó al docente haya hecho
 *   COMMIT exitosamente en la base de datos. Así se evita el caso raro de
 *   enviar un correo de bienvenida y que, milisegundos después, el alta se
 *   revierta por un error (rollback).
 * @Async: se ejecuta en un hilo (thread) aparte del que atiende el pedido
 *   HTTP, para que el administrador no tenga que esperar a que el servidor
 *   SMTP responda al guardar el formulario de alta de un docente.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DocenteRegistradoListener {

    private final EmailService emailService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void enviarBienvenida(DocenteRegistradoEvent evento) {
        log.info("Enviando correo de bienvenida a {}", evento.docente().getCorreo());
        emailService.enviarCorreoBienvenida(evento.docente(), evento.passwordSinCifrar());
    }
}
