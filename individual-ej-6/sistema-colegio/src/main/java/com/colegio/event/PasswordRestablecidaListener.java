package com.colegio.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.colegio.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Envía el correo de "restablecimiento de contraseña" tras el COMMIT de la
 * transacción que generó y guardó la nueva contraseña (mismo patrón que
 * DocenteRegistradoListener: @Async + AFTER_COMMIT).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordRestablecidaListener {

    private final EmailService emailService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void enviarCorreo(PasswordRestablecidaEvent evento) {
        log.info("Enviando correo de restablecimiento de contraseña a {}", evento.docente().getCorreo());
        emailService.enviarCorreoRestablecimiento(evento.docente(), evento.passwordSinCifrar());
    }
}
