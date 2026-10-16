package com.clubdeportivo.controller;

import com.clubdeportivo.dto.PersonaAccesoDTO;
import com.clubdeportivo.dto.RegistroAccesoDTO;
import com.clubdeportivo.dto.RegistroAccesoRequestDTO;
import com.clubdeportivo.model.enums.TipoAcceso;
import com.clubdeportivo.service.ControlAccesoService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ========================================================================================
 * CONTROLADOR MVC: ControlAccesoController
 * ========================================================================================
 * Atiende las interacciones de la terminal de control de accesos perimetrales.
 *
 * Requerimiento de consigna:
 * "Al ingresar al club el sistema registra el horario de entrada, lo mismo sucede en caso de salida.
 * El sistema guarda además de los datos principales una imagen con el rostro de cada persona."
 *
 * Características implementadas:
 * 1. Búsqueda asíncrona interactiva por DNI (@ResponseBody) para proyectar en el monitor
 *    del operador la foto del rostro del socio o familiar antes de franquear el paso.
 * 2. Validación inmediata de la situación de la cuota social familiar.
 * 3. Registro inmediato de Entrada o Salida con marcas de tiempo automáticas.
 * 4. Historial cronológico de accesos con filtrado por rango de fechas.
 * ========================================================================================
 */
@Controller
@RequestMapping("/accesos")
public class ControlAccesoController {

    private final ControlAccesoService controlAccesoService;

    public ControlAccesoController(ControlAccesoService controlAccesoService) {
        this.controlAccesoService = controlAccesoService;
    }

    /**
     * Pantalla principal del puesto de control perimetral (molinete).
     */
    @GetMapping("/control")
    public String pantallaControl(Model model) {
        model.addAttribute("accesoRequest", new RegistroAccesoRequestDTO());
        model.addAttribute("ultimosAccesos", controlAccesoService.obtenerUltimosAccesos());
        model.addAttribute("entradasHoy", controlAccesoService.contarEntradasHoy());
        model.addAttribute("salidasHoy", controlAccesoService.contarSalidasHoy());
        model.addAttribute("dentroHoy", controlAccesoService.calcularPersonasActualmenteDentro());
        model.addAttribute("moduloActivo", "accesos-control");
        return "accesos/control";
    }

    /**
     * Endpoint API REST / AJAX para búsqueda en tiempo real.
     * Retorna el DTO de la persona con la foto de su rostro, datos familiares y estado de cuota.
     */
    @GetMapping("/buscar-dni")
    @ResponseBody
    public ResponseEntity<?> buscarPorDni(@RequestParam("dni") String dni) {
        try {
            PersonaAccesoDTO persona = controlAccesoService.buscarPersonaPorDni(dni);
            return ResponseEntity.ok(persona);
        } catch (IllegalArgumentException | IllegalStateException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Procesa el registro del movimiento de ENTRADA o SALIDA desde el formulario perimetral.
     */
    @PostMapping("/registrar")
    public String registrarAcceso(
            @Valid @ModelAttribute("accesoRequest") RegistroAccesoRequestDTO requestDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("mensajeError", "Datos de acceso incompletos o inválidos.");
            return "redirect:/accesos/control";
        }

        try {
            RegistroAccesoDTO registro = controlAccesoService.registrarAcceso(requestDTO);
            String accion = (registro.getTipoAcceso() == TipoAcceso.ENTRADA) ? "ENTRADA" : "SALIDA";
            redirectAttributes.addFlashAttribute("mensajeExito",
                    String.format("Se registró la %s de %s (DNI: %s) correctamente a las %s.",
                            accion,
                            registro.getNombreCompletoPersona(),
                            registro.getDniPersona(),
                            registro.getFechaHora().toLocalTime().withNano(0)));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se pudo registrar el acceso: " + e.getMessage());
        }

        return "redirect:/accesos/control";
    }

    /**
     * Muestra la tabla histórica de auditoría de accesos con filtro de fecha.
     */
    @GetMapping("/historial")
    public String historialAccesos(
            @RequestParam(value = "fechaDesde", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(value = "fechaHasta", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            Model model) {

        if (fechaDesde == null) {
            fechaDesde = LocalDate.now();
        }
        if (fechaHasta == null) {
            fechaHasta = LocalDate.now();
        }

        LocalDateTime inicio = fechaDesde.atStartOfDay();
        LocalDateTime fin = fechaHasta.atTime(LocalTime.MAX);

        List<RegistroAccesoDTO> registros = controlAccesoService.filtrarPorRango(inicio, fin);

        model.addAttribute("registros", registros);
        model.addAttribute("fechaDesde", fechaDesde);
        model.addAttribute("fechaHasta", fechaHasta);
        model.addAttribute("moduloActivo", "accesos-historial");
        return "accesos/list";
    }
}
