-- ========================================================================================
-- SISTEMA DE GESTIÓN DE CLUB DEPORTIVO - ESQUEMA DE BASE DE DATOS (MYSQL)
-- ========================================================================================
-- Este script define la estructura relacional de tablas, restricciones de unicidad,
-- claves foráneas e índices de rendimiento para MySQL 8+.
-- ========================================================================================

CREATE DATABASE IF NOT EXISTS `club_deportivo_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `club_deportivo_db`;

-- 1. Tabla de Usuarios y Perfiles del Sistema
CREATE TABLE IF NOT EXISTS `usuarios` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `nombre_completo` VARCHAR(120) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `rol` VARCHAR(30) NOT NULL,
    `activo` BOOLEAN NOT NULL DEFAULT TRUE,
    `fecha_creacion` DATETIME NOT NULL,
    `fecha_modificacion` DATETIME NULL,
    `creado_por` VARCHAR(100) NULL,
    `modificado_por` VARCHAR(100) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Tabla de Socios Titulares (Cabezas del Grupo Familiar)
CREATE TABLE IF NOT EXISTS `socios` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `numero_socio` VARCHAR(20) NOT NULL UNIQUE,
    `dni` VARCHAR(15) NOT NULL UNIQUE,
    `nombre` VARCHAR(80) NOT NULL,
    `apellido` VARCHAR(80) NOT NULL,
    `email` VARCHAR(100) NOT NULL,
    `telefono` VARCHAR(30) NULL,
    `direccion` VARCHAR(150) NULL,
    `fecha_nacimiento` DATE NOT NULL,
    `fecha_alta` DATE NOT NULL,
    `foto_rostro` VARCHAR(255) NULL,
    `activo` BOOLEAN NOT NULL DEFAULT TRUE,
    `fecha_creacion` DATETIME NOT NULL,
    `fecha_modificacion` DATETIME NULL,
    `creado_por` VARCHAR(100) NULL,
    `modificado_por` VARCHAR(100) NULL,
    INDEX `idx_socio_dni` (`dni`),
    INDEX `idx_socio_numero` (`numero_socio`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Tabla de Integrantes del Grupo Familiar
CREATE TABLE IF NOT EXISTS `familiares` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `socio_id` BIGINT NOT NULL,
    `dni` VARCHAR(15) NOT NULL UNIQUE,
    `nombre` VARCHAR(80) NOT NULL,
    `apellido` VARCHAR(80) NOT NULL,
    `parentesco` VARCHAR(30) NOT NULL,
    `fecha_nacimiento` DATE NOT NULL,
    `foto_rostro` VARCHAR(255) NULL,
    `activo` BOOLEAN NOT NULL DEFAULT TRUE,
    `fecha_creacion` DATETIME NOT NULL,
    `fecha_modificacion` DATETIME NULL,
    `creado_por` VARCHAR(100) NULL,
    `modificado_por` VARCHAR(100) NULL,
    CONSTRAINT `fk_familiar_socio` FOREIGN KEY (`socio_id`) REFERENCES `socios` (`id`) ON DELETE CASCADE,
    INDEX `idx_familiar_dni` (`dni`),
    INDEX `idx_familiar_socio` (`socio_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Tabla de Registros de Control de Acceso (Entradas y Salidas con Foto Facial)
CREATE TABLE IF NOT EXISTS `registros_acceso` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `fecha_hora` DATETIME NOT NULL,
    `tipo_acceso` VARCHAR(20) NOT NULL,
    `dni_persona` VARCHAR(15) NOT NULL,
    `nombre_completo_persona` VARCHAR(150) NOT NULL,
    `foto_rostro` VARCHAR(255) NULL,
    `es_socio_titular` BOOLEAN NOT NULL,
    `socio_id` BIGINT NOT NULL,
    `familiar_id` BIGINT NULL,
    `estado_cuota_momento` VARCHAR(30) NOT NULL,
    `punto_acceso` VARCHAR(80) NULL,
    `observaciones` VARCHAR(255) NULL,
    `fecha_creacion` DATETIME NOT NULL,
    `fecha_modificacion` DATETIME NULL,
    `creado_por` VARCHAR(100) NULL,
    `modificado_por` VARCHAR(100) NULL,
    INDEX `idx_acceso_fecha_hora` (`fecha_hora`),
    INDEX `idx_acceso_dni` (`dni_persona`),
    INDEX `idx_acceso_socio` (`socio_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Tabla de Cobranza de Cuotas Familiares (Efectivo, Transferencia, Mercado Pago)
CREATE TABLE IF NOT EXISTS `pagos_cuota` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `socio_id` BIGINT NOT NULL,
    `periodo_mes` INT NOT NULL,
    `periodo_anio` INT NOT NULL,
    `monto` DECIMAL(12, 2) NOT NULL,
    `fecha_pago` DATETIME NOT NULL,
    `medio_pago` VARCHAR(30) NOT NULL,
    `numero_comprobante` VARCHAR(50) NOT NULL UNIQUE,
    `observaciones` VARCHAR(255) NULL,
    `fecha_creacion` DATETIME NOT NULL,
    `fecha_modificacion` DATETIME NULL,
    `creado_por` VARCHAR(100) NULL,
    `modificado_por` VARCHAR(100) NULL,
    CONSTRAINT `fk_pago_socio` FOREIGN KEY (`socio_id`) REFERENCES `socios` (`id`) ON DELETE CASCADE,
    INDEX `idx_pago_socio` (`socio_id`),
    INDEX `idx_pago_periodo` (`periodo_anio`, `periodo_mes`),
    INDEX `idx_pago_comprobante` (`numero_comprobante`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
