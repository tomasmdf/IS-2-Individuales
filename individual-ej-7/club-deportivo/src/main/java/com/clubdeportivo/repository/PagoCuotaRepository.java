package com.clubdeportivo.repository;

import com.clubdeportivo.model.PagoCuota;
import com.clubdeportivo.model.enums.MedioPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * ========================================================================================
 * REPOSITORIO JPA: PagoCuotaRepository
 * ========================================================================================
 * Gestiona la persistencia y agregaciones financieras de pagos de cuotas societarias.
 * ========================================================================================
 */
@Repository
public interface PagoCuotaRepository extends JpaRepository<PagoCuota, Long> {

    /**
     * Recupera el historial completo de pagos de un socio titular ordenado cronológicamente inverso.
     */
    List<PagoCuota> findBySocioIdOrderByPeriodoAnioDescPeriodoMesDesc(Long socioId);

    /**
     * Verifica si la familia ya tiene abonada la cuota correspondiente a un mes y año específicos.
     */
    boolean existsBySocioIdAndPeriodoAnioAndPeriodoMes(Long socioId, Integer periodoAnio, Integer periodoMes);

    /**
     * Recupera un pago por su código de recibo / comprobante único.
     */
    Optional<PagoCuota> findByNumeroComprobante(String numeroComprobante);

    /**
     * Lista general de pagos ordenados por fecha descendente.
     */
    List<PagoCuota> findAllByOrderByFechaPagoDesc();

    /**
     * Devuelve los últimos N pagos realizados para el feed del Dashboard.
     */
    List<PagoCuota> findTop10ByOrderByFechaPagoDesc();

    /**
     * Suma total recaudada en un período de tiempo.
     * Utiliza COALESCE para evitar valores NULL cuando no existen pagos en el intervalo.
     */
    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM PagoCuota p WHERE p.fechaPago BETWEEN :inicio AND :fin")
    BigDecimal sumarTotalRecaudadoEntreFechas(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    /**
     * Suma total recaudada agrupada por un medio de pago específico (Efectivo, Transferencia o Mercado Pago).
     */
    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM PagoCuota p WHERE p.medioPago = :medioPago")
    BigDecimal sumarTotalPorMedioPago(@Param("medioPago") MedioPago medioPago);
}
