package com.ejercicio.sistemaregistro.service.impl;

import com.ejercicio.sistemaregistro.dto.RegistroDTO;
import com.ejercicio.sistemaregistro.model.Usuario;
import com.ejercicio.sistemaregistro.repository.UsuarioRepository;
import com.ejercicio.sistemaregistro.service.ResultadoLogin;
import com.ejercicio.sistemaregistro.service.UsuarioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * =============================================================================
 * "UsuarioServiceImpl" — IMPLEMENTACIÓN DE LA CAPA DE SERVICIO
 * =============================================================================
 * @Service : anotación de Spring (especialización de @Component) que marca
 *            esta clase como un "bean de servicio". Spring la detecta
 *            automáticamente (gracias a @ComponentScan) y la instancia como
 *            un singleton administrado por el contenedor de IoC, listo para
 *            ser inyectado en los controladores con @Autowired o, como en
 *            este caso, mediante inyección por constructor (recomendada).
 *
 * @Transactional : envuelve el método en una transacción de base de datos.
 *            Si ocurre una excepción dentro del método, Hibernate hace
 *            ROLLBACK de todos los cambios (por ejemplo, si algo falla
 *            durante el registro, no queda un usuario "a medias" guardado).
 *
 * BCryptPasswordEncoder : implementación del algoritmo BCrypt para hashear
 *            contraseñas de forma segura (con "salt" incorporado). Nunca se
 *            guarda ni se compara la clave en texto plano.
 * =============================================================================
 */
@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /** Cantidad máxima de intentos fallidos antes de bloquear la cuenta (=3 según el enunciado). */
    @Value("${app.seguridad.max-intentos-fallidos}")
    private int maxIntentosFallidos;

    /**
     * INYECCIÓN DE DEPENDENCIAS POR CONSTRUCTOR:
     * Spring detecta que UsuarioServiceImpl necesita un UsuarioRepository
     * y automáticamente le "inyecta" la instancia (bean) correspondiente,
     * sin que nosotros hagamos "new UsuarioRepository()" manualmente.
     * Esto es el principio de Inversión de Control (IoC).
     */
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * {@inheritDoc}
     * Pasos:
     *  1) Verifica que no exista ya un usuario con ese correo o documento.
     *  2) Verifica que "clave" y "confirmarClave" coincidan.
     *  3) Mapea el DTO a la entidad Usuario.
     *  4) Hashea la clave con BCrypt antes de guardarla.
     *  5) Persiste la entidad mediante el repositorio (usuarioRepository.save),
     *     lo que dispara internamente un INSERT vía Hibernate (ORM).
     */
    @Override
    @Transactional
    public Usuario registrar(RegistroDTO dto) {
        if (usuarioRepository.existsByCorreoPersonal(dto.getCorreoPersonal())) {
            throw new IllegalArgumentException("Ya existe un usuario registrado con ese correo personal");
        }
        if (usuarioRepository.existsByDocumento(dto.getDocumento())) {
            throw new IllegalArgumentException("Ya existe un usuario registrado con ese documento");
        }
        if (!dto.getClave().equals(dto.getConfirmarClave())) {
            throw new IllegalArgumentException("La clave y la confirmación de clave no coinciden");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setDocumento(dto.getDocumento());
        usuario.setFechaNacimiento(dto.getFechaNacimiento());
        usuario.setCorreoPersonal(dto.getCorreoPersonal());
        // La clave NUNCA se guarda en texto plano: se aplica hash BCrypt.
        usuario.setClave(passwordEncoder.encode(dto.getClave()));
        usuario.setIntentosFallidos(0);
        usuario.setBloqueado(false);

        // usuarioRepository.save(...) -> el ORM (Hibernate) traduce esto a un
        // INSERT INTO usuarios (...) VALUES (...) sobre la tabla MySQL.
        return usuarioRepository.save(usuario);
    }

    /**
     * {@inheritDoc}
     * Implementa la regla central del ejercicio:
     *   "Si el usuario está registrado y equivoca la clave 3 veces, se bloquea."
     *
     * Flujo:
     *  1) Busca el usuario por correo (findByCorreoPersonal). Si no existe
     *     -> USUARIO_NO_REGISTRADO (el controlador debe invitar a registrarse).
     *  2) Si existe pero ya está bloqueado -> USUARIO_BLOQUEADO (no se
     *     evalúa la clave, ni siquiera si es correcta).
     *  3) Si la clave no coincide (BCrypt.matches) -> se incrementa
     *     intentosFallidos; si llega al máximo (3), se marca bloqueado=true.
     *     Se persiste el cambio y se devuelve CLAVE_INCORRECTA (o
     *     USUARIO_BLOQUEADO si justo se acaba de bloquear).
     *  4) Si la clave coincide -> se reinicia el contador de intentos a 0
     *     (buena práctica: un login exitoso "limpia" los intentos previos)
     *     y se devuelve LOGIN_EXITOSO.
     */
    @Override
    @Transactional
    public ResultadoLogin autenticar(String correoPersonal, String clave) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreoPersonal(correoPersonal);

        if (usuarioOpt.isEmpty()) {
            return ResultadoLogin.USUARIO_NO_REGISTRADO;
        }

        Usuario usuario = usuarioOpt.get();

        if (Boolean.TRUE.equals(usuario.getBloqueado())) {
            return ResultadoLogin.USUARIO_BLOQUEADO;
        }

        boolean claveCorrecta = passwordEncoder.matches(clave, usuario.getClave());

        if (!claveCorrecta) {
            int intentos = usuario.getIntentosFallidos() == null ? 0 : usuario.getIntentosFallidos();
            intentos++;
            usuario.setIntentosFallidos(intentos);

            if (intentos >= maxIntentosFallidos) {
                usuario.setBloqueado(true);
                usuarioRepository.save(usuario);
                return ResultadoLogin.USUARIO_BLOQUEADO;
            }

            usuarioRepository.save(usuario);
            return ResultadoLogin.CLAVE_INCORRECTA;
        }

        // Login correcto: se reinician los intentos fallidos.
        usuario.setIntentosFallidos(0);
        usuarioRepository.save(usuario);
        return ResultadoLogin.LOGIN_EXITOSO;
    }
}
