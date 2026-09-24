package com.empresa.compras.enums;

/**
 * Roles de acceso al sistema.
 * ADMIN     -> acceso total (ABM de productos, proveedores, usuarios y ordenes de compra).
 * OPERADOR  -> puede registrar y recibir ordenes de compra, y consultar productos/proveedores,
 *              pero no administra usuarios.
 *
 * Spring Security requiere que los roles se antepongan con el prefijo "ROLE_" al construir
 * el GrantedAuthority; eso se resuelve en UsuarioDetailsServiceImpl.
 */
public enum RolUsuario {
    ADMIN,
    OPERADOR
}
