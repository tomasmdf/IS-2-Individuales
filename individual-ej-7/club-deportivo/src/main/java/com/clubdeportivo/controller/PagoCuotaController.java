package com.clubdeportivo.controller;

import com.clubdeportivo.dto.PagoCuotaDTO;
import com.clubdeportivo.dto.PagoCuotaFormDTO;
import com.clubdeportivo.dto.SocioDTO;
import com.clubdeportivo.model.enums.MedioPago;
import com.clubdeportivo.service.PagoCuotaService;
import com.clubdeportivo.service.SocioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * ========================================================================================
 * CONTROLADOR MVC: PagoCuotaController
 * ========================================================================================
 * Administra el cobro de la cuota social del club para cada grupo familiar.
 *
 * Requerimiento de consigna:
 * "Se debe agregar la funcionalidad para poder registrar el pago de la cuota del club para cada
 * familia, donde el pago se puede realizar con distintos medios de pago (Efectivo, Transferencia,
 * Mercado Pago)."
 * ========================================================================================
 */
@Controller
@RequestMapping("/pagos")
public class PagoCuotaController {

    private final PagoCuotaService pagoCuotaService;
    private final SocioService socioService;

    public PagoCuotaController(PagoCuotaService pagoCuotaService, SocioService socioService) {
        this.pagoCuotaService = pagoCuotaService;
        this.socioService = socioService;
    }

    /**
     * Muestra la tabla de todos los pagos registrados con sus comprobantes y medios de pago.
     */
    @GetMapping
    public String listarPagos(Model model) {
        List<PagoCuotaDTO> pagos = pagoCuotaService.listarTodos();
        model.addAttribute("pagos", pagos);
        model.addAttribute("recaudacionMes", pagoCuotaService.calcularRecaudacionMesActual());
        model.addAttribute("moduloActivo", "pagos");
        return "pagos/list";
    }

    /**
     * Formulario para cobrar y registrar una nueva cuota familiar.
     */
    @GetMapping("/nuevo")
    public String nuevoPago(@RequestParam(value = "socioId", required = false) Long socioId, Model model) {
        PagoCuotaFormDTO form = new PagoCuotaFormDTO();
        if (socioId != null) {
            SocioDTO socio = socioService.obtenerPorId(socioId);
            form.setSocioId(socioId);
            form.setSocioNombreCompleto(socio.getNombreCompleto());
        }

        model.addAttribute("pagoForm", form);
        model.addAttribute("socios", socioService.listarTodosActivos());
        model.addAttribute("mediosPago", MedioPago.values());
        model.addAttribute("moduloActivo", "pagos");
        return "pagos/form";
    }

    /**
     * Procesa la transacción del pago y emite el recibo/comprobante oficial.
     */
    @PostMapping("/guardar")
    public String registrarPago(
            @Valid @ModelAttribute("pagoForm") PagoCuotaFormDTO formDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("socios", socioService.listarTodosActivos());
            model.addAttribute("mediosPago", MedioPago.values());
            model.addAttribute("moduloActivo", "pagos");
            return "pagos/form";
        }

        try {
            PagoCuotaDTO pagoRealizado = pagoCuotaService.registrarPago(formDTO);
            redirectAttributes.addFlashAttribute("mensajeExito",
                    String.format("Pago registrado con éxito. Comprobante: %s (%s)",
                            pagoRealizado.getNumeroComprobante(),
                            pagoRealizado.getMedioPagoEtiqueta()));
            // Redirige directamente al comprobante para su visualización o impresión
            return "redirect:/pagos/comprobante/" + pagoRealizado.getId();
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("socios", socioService.listarTodosActivos());
            model.addAttribute("mediosPago", MedioPago.values());
            model.addAttribute("mensajeError", e.getMessage());
            model.addAttribute("moduloActivo", "pagos");
            return "pagos/form";
        }
    }

    /**
     * Vista de comprobante / recibo oficial emitido con la plantilla Sneat.
     */
    @GetMapping("/comprobante/{id}")
    public String verComprobante(@PathVariable("id") Long id, Model model) {
        PagoCuotaDTO pago = pagoCuotaService.obtenerPorId(id);
        SocioDTO socio = socioService.obtenerPorId(pago.getSocioId());

        model.addAttribute("pago", pago);
        model.addAttribute("socio", socio);
        model.addAttribute("moduloActivo", "pagos");
        return "pagos/comprobante";
    }
}
