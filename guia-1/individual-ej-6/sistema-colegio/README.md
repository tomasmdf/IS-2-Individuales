# Sistema de Gestión Escolar — Spring Boot + Thymeleaf + MySQL

Aplicación web MVC para la gestión de un colegio: grados, aulas, materias,
docentes (con login), alumnos, asignaciones (carga horaria) y notas.

## 1. Arquitectura (MVC en capas)

```
Vista (Thymeleaf + Sneat)
        ↕  DTO
Controller  (com.colegio.controller)
        ↕  DTO
Service     (com.colegio.service)     ← reglas de negocio, @Transactional
        ↕  Entity
Repository  (com.colegio.repository)  ← Spring Data JPA / Hibernate (ORM)
        ↕
MySQL
```

- **Model**: `com.colegio.model.entity` (entidades JPA) y `com.colegio.model.enums`.
- **DTO**: `com.colegio.dto` — únicos objetos que cruzan Controller ↔ Service ↔ Vista
  (ver `dto/package-info.java` para la explicación completa del porqué).
- **Mapper**: `com.colegio.mapper` — convierte Entity → DTO a mano.
- **Repository**: `com.colegio.repository` — interfaces `JpaRepository` (ORM).
- **Service**: `com.colegio.service` — lógica de negocio y transacciones.
- **Controller**: `com.colegio.controller` — capa MVC "C", resuelve vistas Thymeleaf.
- **Security**: `com.colegio.security` — `UserDetailsService`, adaptador `UserDetails`,
  `SecurityConfig` (BCrypt, reglas por rol, CSRF, formulario de login propio).
- **Event**: `com.colegio.event` — evento de dominio para el correo de bienvenida.
- **Config**: `com.colegio.config` — auditoría de entidades (`AuditorAware`) y
  `DataSeeder` (usuario admin inicial + datos de ejemplo).
- **Exception**: `com.colegio.exception` — excepciones de negocio + `@ControllerAdvice`.

Cada clase incluye comentarios Javadoc extensos explicando la anotación y el
"por qué" de cada decisión (auditoría, DTO, seguridad, eventos, etc.).

## 2. Modelo de dominio

Ver `docs/diagrama-clases.png` (o `.svg`). Resumen:

- **Docente**: profesor y usuario de login (username = correo personal). Roles `ADMIN`/`DOCENTE`.
- **Grado** 1 → N **Aula** (divisiones/secciones).
- **Aula** 1 → N **Alumno**.
- **Asignacion**: "el Docente X dicta la Materia Y en el Aula Z" (tabla intermedia).
- **Nota**: calificación de un Alumno en una Asignacion, por Período (trimestre).
- **Auditable** (`@MappedSuperclass`): fechaCreacion/Modificacion + creadoPor/modificadoPor
  en TODAS las entidades, vía `@EnableJpaAuditing` + `AuditorAware<String>`.

## 3. Seguridad

- Login por formulario propio (plantilla Sneat) en `/login`, usuario = correo.
- Contraseñas con **BCrypt** (nunca texto plano).
- Autorización por rol: `/docentes/**`, `/grados/**`, `/aulas/**`, `/materias/**`,
  `/asignaciones/**` → sólo `ADMIN`. `/alumnos/**` y `/notas/**` → `ADMIN` y `DOCENTE`
  (con reglas más finas por `@PreAuthorize` y en el propio `NotaService`, donde un
  docente sólo puede cargar notas de SUS asignaciones).
- CSRF habilitado (token automático en cada `<form>` de Thymeleaf).
- Cambio de contraseña propio en **Mi cuenta → Cambiar contraseña**.
- Al registrar un docente se genera una contraseña provisoria y se envía por
  correo (evento `DocenteRegistradoEvent` → `EmailService`, asíncrono y
  post-commit).
- **Restablecer contraseña (admin)**: en el listado de Docentes, el botón
  con el ícono de llave (🔑) genera una contraseña nueva para ese docente y
  se la reenvía por correo (evento `PasswordRestablecidaEvent` →
  `email/restablecimiento.html`). Como la contraseña sólo se guarda con
  hash BCrypt, esta es la única forma de "recuperar el acceso": nunca se
  puede leer la contraseña anterior, sólo generar una nueva.

## 4. Cómo ejecutar

### Requisitos
- JDK 17+
- Maven 3.9+ (o usar el wrapper si se agrega `mvnw`)
- MySQL 8 (o Docker)

### 4.1. Levantar la base de datos
```bash
docker compose up -d          # MySQL en localhost:3306 (root/root/colegio_db)
# opcional: Mailpit para probar el correo de bienvenida sin SMTP real
# bandeja: http://localhost:8025
```

Si se usa Mailpit, exportar antes de arrancar la app:
```bash
export MAIL_HOST=localhost MAIL_PORT=1025 MAIL_SMTP_AUTH=false MAIL_STARTTLS=false
```

### 4.2. Compilar y ejecutar
```bash
mvn spring-boot:run
```
La aplicación queda en `http://localhost:8080`.

### 4.3. Usuario administrador inicial
El `DataSeeder` crea automáticamente, si no existe, un ADMIN con:
- correo: `admin@colegio.local`
- contraseña: `Admin1234`

(configurable con `APP_ADMIN_CORREO` / `APP_ADMIN_PASSWORD`). También carga
datos de ejemplo (grados, aulas, materias, 2 alumnos) si `APP_SEED_DEMO=true`
(por defecto).

### 4.4. Flujo de prueba sugerido
1. Ingresar como admin.
2. **Grados** → crear un grado (o usar los de ejemplo).
3. **Aulas** → crear una división para ese grado.
4. **Materias** → crear una materia.
5. **Docentes** → registrar un docente (se envía el correo de bienvenida
   con la contraseña provisoria — revisar Mailpit si se usó, o el log de la
   aplicación si `APP_MAIL_ENABLED=false`).
6. **Asignaciones** → vincular ese docente + esa materia + esa aula.
7. **Alumnos** → registrar alumnos en esa aula.
8. Cerrar sesión, ingresar como el docente registrado (con la contraseña del
   correo) → **Notas** → elegir la asignación → cargar notas por período.
9. Como docente: **Mi cuenta → Cambiar contraseña**.
10. Como admin: si un docente perdió su contraseña, en **Docentes** usar el
    botón 🔑 ("Restablecer contraseña") para generarle una nueva y
    reenviársela por correo.

## 5. Notas de implementación / limitaciones conocidas

- El proyecto se generó en un entorno sandbox **sin acceso a Maven Central**,
  por lo que no pudo compilarse ni probarse en este entorno; revisar con
  `mvn -q compile` al bajarlo a un entorno con acceso a internet.
- `spring.jpa.hibernate.ddl-auto=update` crea/actualiza el esquema
  automáticamente; para producción se recomienda `validate` + migraciones
  (Flyway/Liquibase).
- La interfaz reutiliza los assets estáticos (CSS/JS/fuentes) de la plantilla
  **Sneat** (ThemeSelection, versión gratuita, licencia MIT incluida en
  `docs/LICENSE-sneat-MIT.md`).
