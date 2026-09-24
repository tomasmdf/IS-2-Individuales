# Sistema de Registro y Login con Bloqueo por Intentos Fallidos

Ejercicio a) — Arquitectura **MVC** + **ORM (JPA/Hibernate)** + **Thymeleaf** + **MySQL**.

## 1. Enunciado resuelto

- Las personas se registran con: **Nombre, Apellido, Documento, Fecha de Nacimiento, Correo Personal**.
- El **usuario** del sistema es el **correo personal**.
- Si el correo no está registrado al intentar loguearse, se **invita a registrarse**.
- Si el usuario está registrado y **equivoca la clave 3 veces**, la cuenta **se bloquea**.

## 2. Arquitectura en capas (MVC + ORM)

```
Navegador (HTML/Thymeleaf)
        │
        ▼
┌─────────────────────┐
│   CONTROLLER (C)     │  AuthController, HomeController
│  Spring MVC          │  Reciben la petición HTTP, validan el formulario
└─────────┬────────────┘  y delegan la lógica al Service
          ▼
┌─────────────────────┐
│   SERVICE            │  UsuarioService / UsuarioServiceImpl
│  Lógica de negocio    │  Reglas: registro, autenticación, bloqueo por 3 intentos
└─────────┬────────────┘
          ▼
┌─────────────────────┐
│  REPOSITORY (ORM)    │  UsuarioRepository extends JpaRepository
│  Spring Data JPA      │  Traduce llamadas Java a SQL sobre MySQL (Hibernate)
└─────────┬────────────┘
          ▼
┌─────────────────────┐
│   MODEL (M)           │  Usuario (@Entity) -> tabla "usuarios"
│  Base de datos MySQL  │
└─────────────────────┘

          ▲
          │ Model (datos)
┌─────────┴────────────┐
│   VIEW (V)            │  login.html, registro.html, home.html (Thymeleaf)
│  Plantilla Sneat/BS5  │  Renderizadas en el servidor con los datos del Model
└─────────────────────┘
```

### Capas y responsabilidad de cada paquete

| Paquete | Rol | Clases |
|---|---|---|
| `model` | Entidades JPA (Modelo/ORM) | `Usuario` |
| `repository` | Acceso a datos (ORM, Spring Data JPA) | `UsuarioRepository` |
| `service` / `service.impl` | Lógica de negocio | `UsuarioService`, `UsuarioServiceImpl`, `ResultadoLogin` |
| `dto` | Objetos para los formularios (Vista ↔ Controlador) | `RegistroDTO`, `LoginDTO` |
| `controller` | Controladores Spring MVC | `AuthController`, `HomeController` |
| `config` | Configuración transversal | `WebConfig`, `SesionInterceptor` |
| `templates` | Vistas Thymeleaf (estilo Sneat/Bootstrap 5) | `login.html`, `registro.html`, `home.html` |

## 3. Anotaciones clave utilizadas (resumen)

- `@SpringBootApplication`, `@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@Column`, `@PrePersist`
- `@Repository` (implícito vía `JpaRepository`), *Query Methods* (`findByCorreoPersonal`)
- `@Service`, `@Transactional`, inyección de dependencias por constructor
- `@Controller`, `@GetMapping`, `@PostMapping`, `@ModelAttribute`, `@Valid`, `BindingResult`
- `@NotBlank`, `@Email`, `@Past`, `@Size` (Jakarta Bean Validation)
- `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor` (Lombok)
- `HandlerInterceptor` + `WebMvcConfigurer` (protección de rutas privadas sin Spring Security completo)

Cada clase del proyecto tiene comentarios extensos explicando **qué hace y por qué**.

## 4. Regla de bloqueo (3 intentos fallidos)

Implementada en `UsuarioServiceImpl.autenticar(...)`:

1. Si el correo no existe → `USUARIO_NO_REGISTRADO` → el controlador muestra el formulario de **registro**.
2. Si existe y está `bloqueado = true` → `USUARIO_BLOQUEADO` (no se evalúa la clave).
3. Si la clave no coincide (BCrypt) → se incrementa `intentosFallidos`; al llegar a **3** (configurable en `application.properties`, propiedad `app.seguridad.max-intentos-fallidos`) se marca `bloqueado = true`.
4. Si la clave coincide → se reinicia `intentosFallidos = 0` y se inicia sesión.

## 5. Cómo ejecutar

1. Crear (o dejar que se autocree) la base MySQL `sistema_registro`, y ajustar usuario/clave en `src/main/resources/application.properties`.
2. Ejecutar:
   ```bash
   mvn spring-boot:run
   ```
3. Abrir `http://localhost:8080/` (redirige a `/login`).

## 6. Integración con la plantilla Sneat (assets reales)

Este proyecto ya incluye los assets **originales** de la plantilla gratuita
**Sneat v1.0.0** (Bootstrap 5 HTML Admin Template), copiados dentro de
`src/main/resources/static/assets/` (carpetas `css`, `js`, `vendor`, `img`),
tal cual vienen en el theme. Spring Boot expone automáticamente todo lo que
está bajo `static/` en la raíz web, por lo que en las vistas se referencian
con `th:href="@{/assets/...}"` / `th:src="@{/assets/...}"`.

Las vistas Thymeleaf son una adaptación 1:1 del HTML original del theme:

| Vista del proyecto | Basada en (Sneat) |
|---|---|
| `templates/login.html` | `html/auth-login-basic.html` |
| `templates/registro.html` | `html/auth-register-basic.html` (ampliada con los campos del enunciado) |
| `templates/home.html` | `html/index.html` (layout "vertical menu", simplificado a lo necesario) |
| `templates/fragments/head-auth.html` | `<head>` de las páginas `auth-*-basic.html` |
| `templates/fragments/head-app.html` | `<head>` de `index.html` (sin librerías de gráficos que no se usan) |
| `templates/fragments/scripts-auth.html` | bloque de `<script>` final de las páginas de auth |

Se mantuvieron las clases y estructura originales de Sneat (`authentication-wrapper`,
`authentication-basic`, `app-brand`, `form-password-toggle`, `layout-menu`,
`content-wrapper`, `dropdown-user`, etc.) y solo se agregaron los atributos
Thymeleaf (`th:field`, `th:object`, `th:if`, `th:text`, `th:href`, `th:src`)
necesarios para conectar el formulario y los datos reales del usuario
autenticado (nombre, apellido, documento, correo, fecha de nacimiento/registro)
con el backend Spring MVC.

Si en el futuro se quiere usar una página del theme que todavía no está en
el proyecto (por ejemplo `auth-forgot-password-basic.html`), el patrón a
seguir es siempre el mismo: copiar el `<body>` original del archivo `.html`
del theme, reemplazar las rutas relativas `../assets/...` por
`th:href="@{/assets/...}"` / `th:src="@{/assets/...}"`, y agregar los
atributos Thymeleaf necesarios para el formulario/datos correspondientes.

## 7. Script SQL de referencia (tabla generada por Hibernate)

Con `spring.jpa.hibernate.ddl-auto=update`, Hibernate crea automáticamente
una tabla equivalente a:

```sql
CREATE TABLE usuarios (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre            VARCHAR(100) NOT NULL,
    apellido          VARCHAR(100) NOT NULL,
    documento         VARCHAR(20)  NOT NULL UNIQUE,
    fecha_nacimiento  DATE         NOT NULL,
    correo_personal   VARCHAR(150) NOT NULL UNIQUE,
    clave             VARCHAR(200) NOT NULL,
    intentos_fallidos INT          NOT NULL DEFAULT 0,
    bloqueado         BOOLEAN      NOT NULL DEFAULT FALSE,
    fecha_registro    DATETIME     NOT NULL
);
```
