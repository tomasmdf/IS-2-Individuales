-- ============================================================================
-- data.sql
-- Datos iniciales de carga (se ejecuta automaticamente al arrancar la app,
-- despues de que Hibernate crea las tablas, gracias a
-- spring.jpa.defer-datasource-initialization=true).
--
-- Usuario administrador inicial para poder ingresar por primera vez:
--   usuario  : admin
--   password : admin123   (aqui se guarda ya encriptado con BCrypt)
-- ============================================================================

INSERT INTO usuarios (username, password, nombre_completo, rol, activo)
SELECT 'admin', '$2b$10$wYF77twSnDVMmfjSU9W0c.ndF8w0U5UmhLP1XDT7Xexur9MesuDwS', 'Administrador del Sistema', 'ADMIN', true
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE username = 'admin');

INSERT INTO usuarios (username, password, nombre_completo, rol, activo)
SELECT 'operador', '$2b$10$wYF77twSnDVMmfjSU9W0c.ndF8w0U5UmhLP1XDT7Xexur9MesuDwS', 'Operador de Compras', 'OPERADOR', true
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE username = 'operador');

-- Categorias de ejemplo (productos de tecnologia)
INSERT INTO categorias (nombre, descripcion)
SELECT 'Notebooks', 'Computadoras portatiles' WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Notebooks');

INSERT INTO categorias (nombre, descripcion)
SELECT 'Perifericos', 'Mouse, teclados, auriculares, camaras web' WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Perifericos');

INSERT INTO categorias (nombre, descripcion)
SELECT 'Almacenamiento', 'Discos rigidos, SSD y pendrives' WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Almacenamiento');

INSERT INTO categorias (nombre, descripcion)
SELECT 'Componentes', 'Placas de video, memorias RAM, procesadores' WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Componentes');

-- Proveedor de ejemplo
INSERT INTO proveedores (razon_social, cuit, telefono, email, direccion, activo)
SELECT 'Distribuidora Tech Mayorista S.A.', '30-71234567-8', '261-4123456', 'ventas@techmayorista.com', 'Av. San Martin 1250, Mendoza', true
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE cuit = '30-71234567-8');

-- Productos de ejemplo
INSERT INTO productos (codigo, nombre, descripcion, categoria_id, precio_compra, precio_venta, stock, stock_minimo, activo)
SELECT 'NB-001', 'Notebook 15" Core i5 8GB/512GB SSD', 'Notebook para uso hogareño y oficina', c.id, 450000.00, 620000.00, 8, 3, true
FROM categorias c WHERE c.nombre = 'Notebooks'
AND NOT EXISTS (SELECT 1 FROM productos WHERE codigo = 'NB-001');

INSERT INTO productos (codigo, nombre, descripcion, categoria_id, precio_compra, precio_venta, stock, stock_minimo, activo)
SELECT 'PE-010', 'Mouse inalambrico', 'Mouse optico inalambrico USB', c.id, 8500.00, 14900.00, 25, 10, true
FROM categorias c WHERE c.nombre = 'Perifericos'
AND NOT EXISTS (SELECT 1 FROM productos WHERE codigo = 'PE-010');

INSERT INTO productos (codigo, nombre, descripcion, categoria_id, precio_compra, precio_venta, stock, stock_minimo, activo)
SELECT 'AL-020', 'SSD 480GB SATA III', 'Disco solido de estado', c.id, 32000.00, 49900.00, 15, 5, true
FROM categorias c WHERE c.nombre = 'Almacenamiento'
AND NOT EXISTS (SELECT 1 FROM productos WHERE codigo = 'AL-020');
