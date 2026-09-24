package com.clubdeportivo.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * ========================================================================================
 * DTO DE MÉTRICAS: DashboardDTO
 * ========================================================================================
 * Consolida todas las estadísticas operativas y financieras requeridas por la vista principal
 * (Dashboard de la plantilla Sneat): socios, accesos del día, distribución de pagos y movimientos.
 * ========================================================================================
 */
public class DashboardDTO {

    private long totalSociosActivos;
    private long totalFamiliaresActivos;
    private long totalPersonasRegistradas;
    private long totalAccesosHoy;
    private long entradasHoy;
    private long salidasHoy;
    private long personasActualmenteDentro;

    private BigDecimal recaudacionMesActual = BigDecimal.ZERO;
    private BigDecimal recaudacionEfectivo = BigDecimal.ZERO;
    private BigDecimal recaudacionTransferencia = BigDecimal.ZERO;
    private BigDecimal recaudacionMercadoPago = BigDecimal.ZERO;

    private List<RegistroAccesoDTO> ultimosAccesos = new ArrayList<>();
    private List<PagoCuotaDTO> ultimosPagos = new ArrayList<>();

    public DashboardDTO() {
    }

    public long getTotalSociosActivos() {
        return totalSociosActivos;
    }

    public void setTotalSociosActivos(long totalSociosActivos) {
        this.totalSociosActivos = totalSociosActivos;
    }

    public long getTotalFamiliaresActivos() {
        return totalFamiliaresActivos;
    }

    public void setTotalFamiliaresActivos(long totalFamiliaresActivos) {
        this.totalFamiliaresActivos = totalFamiliaresActivos;
    }

    public long getTotalPersonasRegistradas() {
        return totalPersonasRegistradas;
    }

    public void setTotalPersonasRegistradas(long totalPersonasRegistradas) {
        this.totalPersonasRegistradas = totalPersonasRegistradas;
    }

    public long getTotalAccesosHoy() {
        return totalAccesosHoy;
    }

    public void setTotalAccesosHoy(long totalAccesosHoy) {
        this.totalAccesosHoy = totalAccesosHoy;
    }

    public long getEntradasHoy() {
        return entradasHoy;
    }

    public void setEntradasHoy(long entradasHoy) {
        this.entradasHoy = entradasHoy;
    }

    public long getSalidasHoy() {
        return salidasHoy;
    }

    public void setSalidasHoy(long salidasHoy) {
        this.salidasHoy = salidasHoy;
    }

    public long getPersonasActualmenteDentro() {
        return personasActualmenteDentro;
    }

    public void setPersonasActualmenteDentro(long personasActualmenteDentro) {
        this.personasActualmenteDentro = personasActualmenteDentro;
    }

    public BigDecimal getRecaudacionMesActual() {
        return recaudacionMesActual;
    }

    public void setRecaudacionMesActual(BigDecimal recaudacionMesActual) {
        this.recaudacionMesActual = (recaudacionMesActual != null ? recaudacionMesActual : BigDecimal.ZERO);
    }

    public BigDecimal getRecaudacionEfectivo() {
        return recaudacionEfectivo;
    }

    public void setRecaudacionEfectivo(BigDecimal recaudacionEfectivo) {
        this.recaudacionEfectivo = (recaudacionEfectivo != null ? recaudacionEfectivo : BigDecimal.ZERO);
    }

    public BigDecimal getRecaudacionTransferencia() {
        return recaudacionTransferencia;
    }

    public void setRecaudacionTransferencia(BigDecimal recaudacionTransferencia) {
        this.recaudacionTransferencia = (recaudacionTransferencia != null ? recaudacionTransferencia : BigDecimal.ZERO);
    }

    public BigDecimal getRecaudacionMercadoPago() {
        return recaudacionMercadoPago;
    }

    public void setRecaudacionMercadoPago(BigDecimal recaudacionMercadoPago) {
        this.recaudacionMercadoPago = (recaudacionMercadoPago != null ? recaudacionMercadoPago : BigDecimal.ZERO);
    }

    public List<RegistroAccesoDTO> getUltimosAccesos() {
        return ultimosAccesos;
    }

    public void setUltimosAccesos(List<RegistroAccesoDTO> ultimosAccesos) {
        this.ultimosAccesos = ultimosAccesos;
    }

    public List<PagoCuotaDTO> getUltimosPagos() {
        return ultimosPagos;
    }

    public void setUltimosPagos(List<PagoCuotaDTO> ultimosPagos) {
        this.ultimosPagos = ultimosPagos;
    }
}
