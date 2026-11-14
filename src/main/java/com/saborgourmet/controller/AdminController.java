package com.saborgourmet.controller;

import com.saborgourmet.model.Mesa;
import com.saborgourmet.service.MesaService;
import com.saborgourmet.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Controla las funciones administrativas del restaurante.
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final MesaService mesaService;
    private final ReservaService reservaService;

    public AdminController(
            MesaService mesaService,
            ReservaService reservaService) {
        this.mesaService = mesaService;
        this.reservaService = reservaService;
    }

    // Muestra el panel principal de administración.
    @GetMapping
    public String inicio(Model model) {
        model.addAttribute("totalMesas", mesaService.listarMesas().size());
        model.addAttribute("totalReservas", reservaService.listarReservas().size());
        model.addAttribute("mesas", mesaService.listarMesas());
        model.addAttribute("reservas", reservaService.listarReservas());
        return "admin/inicio";
    }

    // Muestra todas las mesas registradas.
    @GetMapping("/mesas")
    public String listarMesas(Model model) {
        model.addAttribute("mesas", mesaService.listarMesas());
        return "admin/mesas";
    }

    // Muestra el formulario para registrar una mesa.
    @GetMapping("/mesas/nueva")
    public String nuevaMesa(Model model) {
        model.addAttribute("mesa", new Mesa());
        return "admin/formulario-mesa";
    }

    // Guarda una mesa nueva.
    @PostMapping("/mesas/guardar")
    public String guardarMesa(
            @Valid @ModelAttribute("mesa") Mesa mesa,
            BindingResult resultado,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (resultado.hasErrors()) {
            return "admin/formulario-mesa";
        }

        try {
            mesaService.guardarMesa(mesa);
            redirectAttributes.addFlashAttribute(
                    "mensaje", "Mesa registrada correctamente.");
            return "redirect:/admin/mesas";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "admin/formulario-mesa";
        }
    }

    // Muestra el formulario para editar una mesa.
    @GetMapping("/mesas/editar/{id}")
    public String editarMesa(@PathVariable Long id, Model model) {
        Mesa mesa = mesaService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró la mesa solicitada."));

        model.addAttribute("mesa", mesa);
        return "admin/formulario-mesa";
    }

    // Actualiza una mesa existente.
    @PostMapping("/mesas/actualizar/{id}")
    public String actualizarMesa(
            @PathVariable Long id,
            @Valid @ModelAttribute("mesa") Mesa mesa,
            BindingResult resultado,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (resultado.hasErrors()) {
            return "admin/formulario-mesa";
        }

        try {
            mesaService.actualizarMesa(id, mesa);
            redirectAttributes.addFlashAttribute(
                    "mensaje", "Mesa actualizada correctamente.");
            return "redirect:/admin/mesas";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "admin/formulario-mesa";
        }
    }

    // Cambia el estado de disponibilidad de una mesa.
    @PostMapping("/mesas/estado/{id}")
    public String cambiarEstado(
            @PathVariable Long id,
            @RequestParam Mesa.EstadoMesa estado,
            RedirectAttributes redirectAttributes) {

        try {
            mesaService.cambiarEstado(id, estado);
            redirectAttributes.addFlashAttribute(
                    "mensaje", "Disponibilidad de la mesa actualizada.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/mesas";
    }

    // Elimina una mesa.
    @PostMapping("/mesas/eliminar/{id}")
    public String eliminarMesa(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            mesaService.eliminarMesa(id);
            redirectAttributes.addFlashAttribute(
                    "mensaje", "Mesa eliminada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error", "No se pudo eliminar la mesa. Compruebe si tiene reservas asociadas.");
        }

        return "redirect:/admin/mesas";
    }
}
