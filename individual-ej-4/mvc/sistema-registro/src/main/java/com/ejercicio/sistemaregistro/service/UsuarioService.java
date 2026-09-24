package com.ejercicio.sistemaregistro.service;

import com.ejercicio.sistemaregistro.dto.RegistroDTO;
import com.ejercicio.sistemaregistro.model.Usuario;

/**
 * =============================================================================
 * INTERFAZ "UsuarioService" — CAPA DE SERVICIO (lógica de negocio)
 * =============================================================================
 * En una arquitectura en capas prolija, el Controlador NUNCA debería hablar
 * directamente con el Repositorio ni contener reglas de negocio. Para eso
 * existe la CAPA DE SERVICIO:
 *
 *   Controlador  -->  Service (reglas de negocio)  -->  Repository (acceso a datos)  -->  Base de datos
 *
 * Se define primero una interfaz (contrato) y luego una implementación
 * (UsuarioServiceImpl) siguiendo el principio de "programar contra
 * interfaces, no contra implementaciones" (facilita tests unitarios con
 * mocks y permite cambiar la implementación sin tocar el controlador).
 *
 * Reglas de negocio que resuelve esta capa (definidas por el enunciado):
 *   - Registrar un nuevo usuario validando que el correo y el documento
 *     no estén repetidos, y guardando la clave hasheada (nunca en texto plano).
 *   - Autenticar un usuario: si no existe, se debe avisar para que se registre;
 *     si existe y la clave es incorrecta, se cuentan los intentos fallidos;
 *     al tercer intento fallido consecutivo, se bloquea la cuenta.
 * =============================================================================
 */
public interface UsuarioService {

    /**
     * Registra un nuevo usuario en el sistema a partir de los datos del
     * formulario (RegistroDTO). Hashea la clave antes de persistirla.
     *
     * @param dto datos ingresados en el formulario de registro
     * @return la entidad Usuario ya guardada (con su id generado)
     * @throws IllegalArgumentException si el correo o el documento ya existen
     */
    Usuario registrar(RegistroDTO dto);

    /**
     * Intenta autenticar a un usuario con correo personal (usuario) y clave.
     * Aplica la regla de bloqueo por 3 intentos fallidos.
     *
     * @param correoPersonal "usuario" del sistema
     * @param clave          clave en texto plano ingresada en el formulario
     * @return un valor de ResultadoLogin que indica qué pasó con el intento
     */
    ResultadoLogin autenticar(String correoPersonal, String clave);
}
