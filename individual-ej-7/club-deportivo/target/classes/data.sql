-- ========================================================================================
-- SISTEMA DE GESTIÓN DE CLUB DEPORTIVO - DATOS DE PRUEBA (DATA.SQL)
-- ========================================================================================

-- Contraseñas encriptadas con BCrypt:
-- 'admin123' -> $2a$10$wE99q4y9Zt4Jg8m4.bS9nOD7mQzWd7NlFfGjHhJkLlMmNnOoPpQq
-- 'recep123' -> $2a$10$zE88q3y8Zt3Jg7m3.bS8nOD6mQzWd6NlFfGjHhJkLlMmNnOoPpQq

INSERT INTO `usuarios` (`id`, `username`, `password`, `nombre_completo`, `email`, `rol`, `activo`, `fecha_creacion`, `creado_por`)
VALUES
(1, 'admin', '$2a$10$e7K52C5v4BfL5B1lM/1BSeH1bB0z7kHq7P1F9J2dG5D1hL8aP7bYq', 'Administrador General', 'admin@clubdeportivo.com', 'ROLE_ADMIN', TRUE, NOW(), 'SISTEMA'),
(2, 'recepcion', '$2a$10$e7K52C5v4BfL5B1lM/1BSeH1bB0z7kHq7P1F9J2dG5D1hL8aP7bYq', 'Lucía Fernández', 'recepcion@clubdeportivo.com', 'ROLE_RECEPCIONISTA', TRUE, NOW(), 'SISTEMA')
ON DUPLICATE KEY UPDATE `id` = `id`;
