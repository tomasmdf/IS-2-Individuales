# Sistema de Gestion de Compras a Proveedores — INCISO A

Trabajo practico: sistema para una empresa de venta de productos de
tecnologia que actualiza su stock mediante **Ordenes de Compra** emitidas a
**Proveedores mayoristas**, cada una con su **detalle** de productos.

## 1. Arquitectura (patron MVC en capas)

```
Vista (Thymeleaf + plantilla Sneat)
        |
   Controller  (recibe HTTP, arma el Model, no tiene logica de negocio)
        |
     Service   (VALIDACIONES DE NEGOCIO + logica, transaccional)
        |
   Repository  (Spring Data JPA / Hibernate -> ORM)
        |
      Entity   (mapeadas 1:1 a tablas MySQL con anotaciones JPA)
```

La informacion que viaja entre Controller <-> Service <-> Vista lo hace a
traves de **DTOs** (paquete `dto`), nunca exponiendo las `@Entity`
directamente. La conversion Entity <-> DTO la hacen los `mapper` (paquete
`mapper`).

### Paquetes

| Paquete       | Responsabilidad                                                        |
|---------------|--------------------------------------------------------------------------|
| `entity`      | Clases `@Entity` (ORM/JPA), mapeadas a tablas MySQL                     |
| `repository`  | Interfaces `JpaRepository` (acceso a datos, sin SQL manual)             |
| `dto`         | Objetos planos que viajan entre capas, con Bean Validation declarativa  |
| `mapper`      | Conversion manual Entity <-> DTO                                        |
| `service`     | Interfaces de negocio                                                   |
| `service.impl`| Implementacion con **validaciones de negocio** y `@Transactional`       |
| `controller`  | Controladores Spring MVC (`@Controller`)                                |
| `security`    | Integracion con Spring Security (UserDetails/UserDetailsService)        |
| `config`      | Configuracion de seguridad y utilidades transversales                   |
| `exception`   | Excepciones de negocio propias                                          |

### Modelo de dominio

- **Usuario**: login (usuario + contraseña encriptada con BCrypt), rol (`ADMIN` / `OPERADOR`).
- **Proveedor**: proveedor mayorista al que se le compra mercaderia.
- **Categoria**: rubro de los productos de tecnologia.
- **Producto**: catalogo de la empresa, con el campo `stock`.
- **OrdenCompra** (cabecera) + **DetalleOrdenCompra** (renglones): registra
  una compra a un proveedor. Al pasar la orden al estado **RECIBIDA**, el
  `OrdenCompraServiceImpl` incrementa automaticamente el `stock` de cada
  producto del detalle (regla de negocio central del enunciado).

## 2. Seguridad

El acceso al sistema requiere **usuario y contraseña** (Spring Security,
formulario de login en `/login`). Las contraseñas se guardan **encriptadas
con BCrypt**, nunca en texto plano. El modulo de **Usuarios** queda
restringido al rol `ADMIN`.

Usuarios de prueba precargados por `data.sql`:

| Usuario    | Contraseña | Rol      |
|------------|------------|----------|
| `admin`    | `admin123` | ADMIN    |
| `operador` | `admin123` | OPERADOR |

## 3. Puesta en marcha

1. Crear la base de datos en MySQL:
   ```sql
   CREATE DATABASE compras_db CHARACTER SET utf8mb4;
   ```
2. Ajustar usuario/clave de conexion en
   `src/main/resources/application.properties` si es necesario.
3. Ejecutar la aplicacion:
   ```bash
   mvn spring-boot:run
   ```
   Hibernate creara las tablas automaticamente (`ddl-auto=update`) y
   `data.sql` cargara los usuarios y datos de ejemplo.
4. Ingresar a `http://localhost:8080` (redirige a `/login`).

## 4. Flujo tipico de una compra

1. Cargar (si hace falta) Categorias, Productos y Proveedores.
2. `Ordenes de Compra -> Nueva orden de compra`: elegir proveedor, fecha y
   agregar uno o mas productos con cantidad y precio unitario. Al guardar,
   la orden queda en estado **PENDIENTE** (todavia no afecta el stock).
3. Desde el detalle de la orden, presionar **"Recibir orden"**: la orden
   pasa a **RECIBIDA** y el stock de cada producto se incrementa segun las
   cantidades compradas.
4. Una orden **PENDIENTE** tambien puede **anularse** (no impacta stock).
   Una orden **RECIBIDA** ya no puede anularse.

## 5. Tecnologias

- Java 17 + Spring Boot 3
- Spring MVC + Thymeleaf (vista, plantilla admin **Sneat**)
- Spring Data JPA / Hibernate (ORM) sobre **MySQL**
- Spring Security (autenticacion por usuario y contraseña, BCrypt)
- Bean Validation (Jakarta Validation) para las reglas declarativas de los DTO
- Lombok
