package com.clubdeportivo.model;

import com.clubdeportivo.model.enums.Parentesco;
import com.clubdeportivo.model.enums.TipoSocio;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ========================================================================================
 * ENTIDAD JPA: Socio (Titular del Grupo Familiar)
 * ========================================================================================
 * Representa al socio titular del club deportivo.
 * Es la cabeza de familia responsable del pago de la cuota social que cubre a sus dependientes.
 *
 * Anotaciones utilizadas:
 * - @Entity & @Table: Mapeo de la tabla "socios" con índices únicos en DNI y número de socio.
 * - @OneToMany(mappedBy = "socio", cascade = CascadeType.ALL, orphanRemoval = true):
 *   Establece la relación uno-a-muchos con la entidad Familiar y PagoCuota.
 *   - cascade = CascadeType.ALL: Cualquier operación sobre el socio (guardar, actualizar, borrar)
 *     se propaga automáticamente a sus familiares dependientes y registros de cuotas.
 *   - orphanRemoval = true: Si se remueve un familiar de la lista en memoria, Hibernate lo elimina
 *     físicamente de la base de datos para no dejar registros huérfanos.
 * ========================================================================================
 */
@Entity
@Table(name = "socios", indexes = {
        @Index(name = "idx_socio_dni", columnList = "dni", unique = true),
        @Index(name = "idx_socio_numero", columnList = "numero_socio", unique = true)
})
public class Socio extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_socio", nullable = false, length = 20, unique = true)
    private String numeroSocio;

    @Column(name = "dni", nullable = false, length = 15, unique = true)
    private String dni;

    @Column(name = "nombre", nullable = false, length = 80)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 80)
    private String apellido;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "telefono", length = 30)
    private String telefono;

    @Column(name = "direccion", length = 150)
    private String direccion;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(name = "fecha_alta", nullable = false)
    private LocalDate fechaAlta;

    /**
     * Nombre del archivo o ruta relativa de la fotografía del rostro guardada en el servidor.
     * Requisito explícito: "El sistema guarda además de los datos principales una imagen con el rostro de cada persona."
     */
    @Column(name = "foto_rostro", length = 255)
    private String fotoRostro;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    /**
     * Tipo de socio: TITULAR (cabeza de grupo familiar) o FAMILIAR (socio dependiente de otro socio).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_socio", length = 30)
    private TipoSocio tipoSocio = TipoSocio.TITULAR;

    /**
     * Si es socio familiar, referencia al socio titular del cual depende su membresía familiar.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "socio_titular_id", foreignKey = @ForeignKey(name = "fk_socio_titular"))
    private Socio socioTitular;

    /**
     * Parentesco con el socio titular (cuando tipoSocio == FAMILIAR).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "parentesco", length = 30)
    private Parentesco parentesco;

    /**
     * Lista de socios familiares que dependen de este socio titular.
     */
    @OneToMany(mappedBy = "socioTitular", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Socio> sociosFamiliares = new ArrayList<>();

    /**
     * Integrantes del grupo familiar dependientes de este socio titular.
     */
    @OneToMany(mappedBy = "socio", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Familiar> familiares = new ArrayList<>();

    /**
     * Historial de pagos de cuotas sociales registradas para este grupo familiar.
     */
    @OneToMany(mappedBy = "socio", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PagoCuota> pagos = new ArrayList<>();

    // ====================================================================================
    // CONSTRUCTORES
    // ====================================================================================

    public Socio() {
        this.fechaAlta = LocalDate.now();
        this.activo = true;
    }

    // ====================================================================================
    // MÉTODOS DE CONVENIENCIA (HELPER METHODS)
    // ====================================================================================

    public String getNombreCompleto() {
        return (apellido != null ? apellido.toUpperCase() : "") + ", " + (nombre != null ? nombre : "");
    }

    public void agregarFamiliar(Familiar familiar) {
        familiares.add(familiar);
        familiar.setSocio(this);
    }

    public void removerFamiliar(Familiar familiar) {
        familiares.remove(familiar);
        familiar.setSocio(null);
    }

    public void agregarPago(PagoCuota pago) {
        pagos.add(pago);
        pago.setSocio(this);
    }

    // ====================================================================================
    // GETTERS Y SETTERS
    // ====================================================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroSocio() {
        return numeroSocio;
    }

    public void setNumeroSocio(String numeroSocio) {
        this.numeroSocio = numeroSocio;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public String getFotoRostro() {
        return fotoRostro;
    }

    public void setFotoRostro(String fotoRostro) {
        this.fotoRostro = fotoRostro;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public List<Familiar> getFamiliares() {
        return familiares;
    }

    public void setFamiliares(List<Familiar> familiares) {
        this.familiares = familiares;
    }

    public List<PagoCuota> getPagos() {
        return pagos;
    }

    public void setPagos(List<PagoCuota> pagos) {
        this.pagos = pagos;
    }

    public TipoSocio getTipoSocio() {
        if (this.tipoSocio != null) {
            return this.tipoSocio;
        }
        return (this.socioTitular != null) ? TipoSocio.FAMILIAR : TipoSocio.TITULAR;
    }

    public void setTipoSocio(TipoSocio tipoSocio) {
        this.tipoSocio = (tipoSocio != null ? tipoSocio : TipoSocio.TITULAR);
    }

    public Socio getSocioTitular() {
        return socioTitular;
    }

    public void setSocioTitular(Socio socioTitular) {
        this.socioTitular = socioTitular;
    }

    public Parentesco getParentesco() {
        return parentesco;
    }

    public void setParentesco(Parentesco parentesco) {
        this.parentesco = parentesco;
    }

    public List<Socio> getSociosFamiliares() {
        return sociosFamiliares;
    }

    public void setSociosFamiliares(List<Socio> sociosFamiliares) {
        this.sociosFamiliares = sociosFamiliares;
    }

    public void agregarSocioFamiliar(Socio socioFamiliar, Parentesco parentesco) {
        this.sociosFamiliares.add(socioFamiliar);
        socioFamiliar.setSocioTitular(this);
        socioFamiliar.setTipoSocio(TipoSocio.FAMILIAR);
        socioFamiliar.setParentesco(parentesco);
    }
}
