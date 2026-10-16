package com.colegio.service;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.colegio.model.entity.Docente;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * =============================================================================
 * SERVICIO DE CORREO: envía el correo de bienvenida al registrar un docente.
 * =============================================================================
 * Reutiliza el MISMO motor de plantillas Thymeleaf que la capa web (inyectado
 * por Spring Boot como Bean "templateEngine") para renderizar el cuerpo del
 * correo en HTML a partir de templates/email/bienvenida.html, con el mismo
 * mecanismo de variables (Context.setVariable) que usan los Controllers al
 * devolver un ModelAndView.
 *
 * app.mail.enabled=false (ver application.properties) permite DESACTIVAR el
 * envío real (por ejemplo en un entorno de pruebas sin SMTP configurado): en
 * ese caso el correo sólo queda registrado en el log, sin romper el alta del
 * docente.
 * =============================================================================
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.enabled}")
    private boolean mailHabilitado;

    @Value("${app.mail.from}")
    private String remitente;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.nombre-colegio}")
    private String nombreColegio;

    public void enviarCorreoBienvenida(Docente docente, String passwordSinCifrar) {
        String asunto = "¡Bienvenido/a a " + nombreColegio + "!";

        Context contexto = new Context(Locale.forLanguageTag("es-AR"));
        contexto.setVariable("nombre", docente.getNombre());
        contexto.setVariable("apellido", docente.getApellido());
        contexto.setVariable("correo", docente.getCorreo());
        contexto.setVariable("password", passwordSinCifrar);
        contexto.setVariable("nombreColegio", nombreColegio);
        contexto.setVariable("urlIngreso", baseUrl + "/login");

        // process() combina la plantilla HTML con las variables de arriba,
        // igual que Spring MVC hace con las vistas, pero el resultado es un
        // String que se usa como cuerpo del correo en lugar de una respuesta HTTP.
        String cuerpoHtml = templateEngine.process("email/bienvenida", contexto);

        if (!mailHabilitado) {
            log.warn("app.mail.enabled=false -> NO se envía correo real. Destinatario: {}, asunto: {}",
                    docente.getCorreo(), asunto);
            log.debug("Cuerpo del correo que se habría enviado:\n{}", cuerpoHtml);
            return;
        }

        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            // "true" = multipart, necesario para poder enviar HTML
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
            helper.setFrom(remitente);
            helper.setTo(docente.getCorreo());
            helper.setSubject(asunto);
            helper.setText(cuerpoHtml, true); // true = el texto es HTML
            mailSender.send(mensaje);
            log.info("Correo de bienvenida enviado a {}", docente.getCorreo());
        } catch (MessagingException | org.springframework.mail.MailException ex) {
            // Un fallo de correo NO debe hacer fallar el alta del docente (ya se guardó en
            // la base de datos antes de llegar acá): sólo se registra el error en el log.
            log.error("No se pudo enviar el correo de bienvenida a {}: {}", docente.getCorreo(), ex.getMessage());
        }
    }

    /**
     * Igual que enviarCorreoBienvenida, pero para cuando el ADMIN restablece
     * la contraseña de un docente que ya tenía cuenta (por ejemplo, si nunca
     * recibió o perdió el correo original). Usa una plantilla distinta
     * (email/restablecimiento.html) para dejar en claro que es un cambio,
     * no un alta nueva.
     */
    public void enviarCorreoRestablecimiento(Docente docente, String passwordSinCifrar) {
        String asunto = "Se restableció su contraseña en " + nombreColegio;

        Context contexto = new Context(Locale.forLanguageTag("es-AR"));
        contexto.setVariable("nombre", docente.getNombre());
        contexto.setVariable("apellido", docente.getApellido());
        contexto.setVariable("correo", docente.getCorreo());
        contexto.setVariable("password", passwordSinCifrar);
        contexto.setVariable("nombreColegio", nombreColegio);
        contexto.setVariable("urlIngreso", baseUrl + "/login");

        String cuerpoHtml = templateEngine.process("email/restablecimiento", contexto);

        if (!mailHabilitado) {
            log.warn("app.mail.enabled=false -> NO se envía correo real. Destinatario: {}, asunto: {}",
                    docente.getCorreo(), asunto);
            log.debug("Cuerpo del correo que se habría enviado (incluye la contraseña nueva):\n{}", cuerpoHtml);
            return;
        }

        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
            helper.setFrom(remitente);
            helper.setTo(docente.getCorreo());
            helper.setSubject(asunto);
            helper.setText(cuerpoHtml, true);
            mailSender.send(mensaje);
            log.info("Correo de restablecimiento de contraseña enviado a {}", docente.getCorreo());
        } catch (MessagingException | org.springframework.mail.MailException ex) {
            log.error("No se pudo enviar el correo de restablecimiento a {}: {}", docente.getCorreo(), ex.getMessage());
        }
    }
}
