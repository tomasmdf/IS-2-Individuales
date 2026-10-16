package com.colegio.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.colegio.dto.NotaRequest;
import com.colegio.model.enums.Periodo;
import com.colegio.security.DocenteUserDetails;
import com.colegio.service.AsignacionService;
import com.colegio.service.NotaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Carga de notas. "Mis asignaciones" (GET /notas) muestra al docente
 * logueado SÓLO las materias/aulas que él dicta (o, si es ADMIN, todas),
 * y desde ahí entra a la planilla de carga de cada una.
 */
@Controller
@RequestMapping("/notas")
@RequiredArgsConstructor
public class NotaController {

    private final NotaService notaService;
    private final AsignacionService asignacionService;

    @GetMapping
    public String misAsignaciones(@AuthenticationPrincipal DocenteUserDetails principal, Model model) {
        boolean esAdmin = principal.getDocente().getRol().name().equals("ADMIN");
        model.addAttribute("asignaciones",
                esAdmin ? asignacionService.listar() : asignacionService.listarPorDocente(principal.getUsername()));
        return "notas/mis-asignaciones";
    }

    /** Planilla de carga: un alumno por fila, una nota por período. */
    @GetMapping("/asignacion/{asignacionId}")
    public String planilla(@PathVariable Long asignacionId,
                            @RequestParam(required = false, defaultValue = "PRIMER_TRIMESTRE") Periodo periodo,
                            Model model) {
        model.addAttribute("asignacion", asignacionService.obtener(asignacionId));
        model.addAttribute("alumnos", notaService.alumnosDeAsignacion(asignacionId));
        model.addAttribute("notas", notaService.listarPorAsignacion(asignacionId));
        model.addAttribute("periodoSeleccionado", periodo);
        model.addAttribute("periodos", Periodo.values());
        if (!model.containsAttribute("notaRequest")) {
            model.addAttribute("notaRequest", new NotaRequest(null, null, asignacionId, periodo, null, ""));
        }
        return "notas/planilla";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("notaRequest") NotaRequest request,
                           BindingResult bindingResult,
                           @AuthenticationPrincipal DocenteUserDetails principal,
                           RedirectAttributes redirectAttributes) {
        boolean esAdmin = principal.getDocente().getRol().name().equals("ADMIN");
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("mensajeError", "Revise los datos de la nota ingresada.");
        } else {
            notaService.guardar(request, principal.getUsername(), esAdmin);
            redirectAttributes.addFlashAttribute("mensajeExito", "Nota guardada correctamente.");
        }
        return "redirect:/notas/asignacion/" + request.asignacionId() + "?periodo=" + request.periodo();
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id,
                            @RequestParam Long asignacionId,
                            @AuthenticationPrincipal DocenteUserDetails principal,
                            RedirectAttributes redirectAttributes) {
        boolean esAdmin = principal.getDocente().getRol().name().equals("ADMIN");
        notaService.eliminar(id, principal.getUsername(), esAdmin);
        redirectAttributes.addFlashAttribute("mensajeExito", "Nota eliminada.");
        return "redirect:/notas/asignacion/" + asignacionId;
    }
}
