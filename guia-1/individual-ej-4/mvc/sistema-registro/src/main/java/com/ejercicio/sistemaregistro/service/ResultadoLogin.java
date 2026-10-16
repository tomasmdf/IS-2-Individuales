package com.ejercicio.sistemaregistro.service;

/**
 * =============================================================================
 * ENUM "ResultadoLogin"
 * =============================================================================
 * Representa, de forma clara y tipada, todos los posibles resultados al
 * intentar autenticar a un usuario. En lugar de devolver booleanos sueltos
 * o lanzar excepciones para cada caso de negocio, se usa un enum: hace el
 * código más legible y evita "magic numbers/strings" en el controlador.
 *
 *   USUARIO_NO_REGISTRADO : el correo ingresado no existe en la base ->
 *                            el controlador debe redirigir a la pantalla
 *                            de registro (regla explícita del enunciado).
 *   USUARIO_BLOQUEADO     : el usuario existe pero superó los 3 intentos
 *                            fallidos permitidos.
 *   CLAVE_INCORRECTA      : el usuario existe, no está bloqueado, pero la
 *                            clave ingresada no coincide (se incrementa el
 *                            contador de intentos fallidos).
 *   LOGIN_EXITOSO         : usuario y clave correctos -> se inicia sesión.
 * =============================================================================
 */
public enum ResultadoLogin {
    USUARIO_NO_REGISTRADO,
    USUARIO_BLOQUEADO,
    CLAVE_INCORRECTA,
    LOGIN_EXITOSO
}
