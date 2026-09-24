package com.clubdeportivo.controller;

import com.clubdeportivo.dto.FamiliarDTO;
import com.clubdeportivo.dto.PagoCuotaDTO;
import com.clubdeportivo.dto.SocioDTO;
import com.clubdeportivo.dto.SocioFormDTO;
import com.clubdeportivo.model.enums.Parentesco;
import com.clubdeportivo.model.enums.TipoSocio;
import com.clubdeportivo.service.FamiliarService;
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
 * CONTROLADOR MVC: SocioController
 * ========================================================================================
 * Gestiona el ciclo de vida de los socios titulares: listado, altas con fotografía facial,
 * edición, consulta de ficha detallada con su grupo familiar y bajas lógicas.
 *
 * Flujo MVC y desacoplamiento con DTO:
 * - El controlador interactúa con la vista mediante objetos SocioDTO y SocioFormDTO.
 * - @Valid intercepta el DTO y puebla el objeto BindingResult si ocurren infracciones de formato.
 * - RedirectAttributes transporta mensajes Flash a través de redirecciones (Post-Redirect-Get).
 * ========================================================================================
 */
@Controller
@RequestMapping("/socios")
public class SocioController {

    private final SocioService socioService;
    private final FamiliarService familiarService;
    private final PagoCuotaService pagoCuotaService;

    public SocioController(SocioService socioService,
                           FamiliarService familiarService,
                           PagoCuotaService pagoCuotaService) {
        this.socioService = socioService;
        this.familiarService = familiarService;
        this.pagoCuotaService = pagoCuotaService;
    }

    /**
     * Muestra la tabla de socios activos con soporte de filtro de búsqueda.
     */
    @GetMapping
    public String listarSocios(@RequestParam(value = "criterio", required = false) String criterio, Model model) {
        List<SocioDTO> socios = socioService.buscarSocios(criterio);
        model.addAttribute("socios", socios);
        model.addAttribute("criterioBusqueda", criterio);
        model.addAttribute("moduloActivo", "socios");
        return "socios/list";
    }

    /**
     * Muestra el formulario para registrar un nuevo socio (titular o socio familiar de otro socio).
     */
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(@RequestParam(value = "titularId", required = false) Long titularId, Model model) {
        SocioFormDTO formDTO = new SocioFormDTO();
        if (titularId != null) {
            formDTO.setTipoSocio(TipoSocio.FAMILIAR);
            formDTO.setSocioTitularId(titularId);
        }

        model.addAttribute("socioForm", formDTO);
        model.addAttribute("sociosTitulares", socioService.listarSociosTitularesActivos());
        model.addAttribute("parentescos", Parentesco.values());
        model.addAttribute("esEdicion", false);
        model.addAttribute("moduloActivo", "socios");
        return "socios/form";
    }

    /**
     * Procesa el guardado (creación o actualización) de un socio.
     */
    @PostMapping("/guardar")
    public String guardarSocio(
            @Valid @ModelAttribute("socioForm") SocioFormDTO formDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("sociosTitulares", socioService.listarSociosTitularesActivos(formDTO.getId()));
            model.addAttribute("parentescos", Parentesco.values());
            model.addAttribute("esEdicion", formDTO.getId() != null);
            model.addAttribute("moduloActivo", "socios");
            return "socios/form";
        }

        try {
            if (formDTO.getId() == null) {
                socioService.registrarSocio(formDTO);
                String tipoDesc = (formDTO.getTipoSocio() == TipoSocio.FAMILIAR) ? "Socio familiar" : "Socio titular";
                redirectAttributes.addFlashAttribute("mensajeExito", tipoDesc + " registrado exitosamente.");
            } else {
                socioService.actualizarSocio(formDTO);
                redirectAttributes.addFlashAttribute("mensajeExito", "Datos del socio actualizados exitosamente.");
            }
            return "redirect:/socios";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("mensajeError", e.getMessage());
            model.addAttribute("sociosTitulares", socioService.listarSociosTitularesActivos(formDTO.getId()));
            model.addAttribute("parentescos", Parentesco.values());
            model.addAttribute("esEdicion", formDTO.getId() != null);
            model.addAttribute("moduloActivo", "socios");
            return "socios/form";
        }
    }

    /**
     * Carga el formulario para editar datos de un socio existente.
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable("id") Long id, Model model) {
        SocioFormDTO formDTO = socioService.obtenerFormularioPorId(id);
        model.addAttribute("socioForm", formDTO);
        model.addAttribute("sociosTitulares", socioService.listarSociosTitularesActivos(id));
        model.addAttribute("parentescos", Parentesco.values());
        model.addAttribute("esEdicion", true);
        model.addAttribute("moduloActivo", "socios");
        return "socios/form";
    }

    /**
     * Ficha completa del socio: foto del rostro, datos de contacto, grupo familiar asociado
     * (tanto dependientes como socios familiares vinculados) y su historial de pagos de cuotas.
     */
    @GetMapping("/detalle/{id}")
    public String verDetalleSocio(@PathVariable("id") Long id, Model model) {
        SocioDTO socio = socioService.obtenerPorId(id);
        List<FamiliarDTO> familiares = familiarService.listarPorSocio(id);
        List<SocioDTO> sociosFamiliares = socioService.listarSociosFamiliaresPorTitular(id);
        List<PagoCuotaDTO> pagos = pagoCuotaService.listarPorSocio(id);

        model.addAttribute("socio", socio);
        model.addAttribute("familiares", familiares);
        model.addAttribute("sociosFamiliares", sociosFamiliares);
        model.addAttribute("pagos", pagos);
        model.addAttribute("moduloActivo", "socios");
        return "socios/detalle";
    }

    /**
     * Da de baja lógica a un socio (requiere rol ADMIN configurado en SecurityConfig).
     */
    @PostMapping("/eliminar/{id}")
    public String eliminarSocio(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            socioService.darDeBaja(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "El socio titular ha sido dado de baja correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se pudo dar de baja al socio: " + e.getMessage());
        }
        return "redirect:/socios";
    }
}
