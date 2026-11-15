package com.saborgourmet.controller;

import com.saborgourmet.model.Reserva;
import com.saborgourmet.model.Mesa;
import com.saborgourmet.service.ReservaService;
import com.saborgourmet.service.ClienteService;
import com.saborgourmet.service.MesaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Controla las acciones relacionadas con las reservas del restaurante.
@Controller
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;
    private final ClienteService clienteService;
    private final MesaService mesaService;

    // Recibe los servicios necesarios para gestionar las reservas.
    public ReservaController(
            ReservaService reservaService,
            ClienteService clienteService,
            MesaService mesaService) {
        this.reservaService = reservaService;
        this.clienteService = clienteService;
        this.mesaService = mesaService;
    }

    // Muestra el listado de las reservas registradas.
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("reservas", reservaService.listarReservas());
        return "reservas/listar";
    }

    // Prepara el formulario y carga los clientes y mesas disponibles.
    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("reserva", new Reserva());
        cargarDatosFormulario(model);
        return "reservas/formulario";
    }

    // Valida los datos ingresados y solicita registrar la reserva.
    @PostMapping("/guardar")
    public String guardar(
            @Valid @ModelAttribute("reserva") Reserva reserva,
            BindingResult resultado,
            @RequestParam Long clienteId,
            @RequestParam Long mesaId,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Si hay errores, vuelve al formulario para corregirlos.
        if (resultado.hasErrors()) {
            cargarDatosFormulario(model);
            return "reservas/formulario";
        }

        try {
            reservaService.crearReserva(reserva, clienteId, mesaId);
            redirectAttributes.addFlashAttribute(
                    "mensaje", "Reserva registrada correctamente.");
            return "redirect:/reservas";

        } catch (IllegalArgumentException e) {
            // Muestra el motivo por el que no se pudo registrar la reserva.
            model.addAttribute("error", e.getMessage());
            cargarDatosFormulario(model);
            return "reservas/formulario";
        }
    }

    // Busca una reserva y muestra toda su información.
    @GetMapping("/detalle/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        Reserva reserva = reservaService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró la reserva solicitada."));

        model.addAttribute("reserva", reserva);
        return "reservas/detalle";
    }

    // Carga los datos de una reserva existente para poder editarla.
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Reserva reserva = reservaService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró la reserva solicitada."));

        model.addAttribute("reserva", reserva);
        cargarDatosFormulario(model);
        return "reservas/formulario";
    }

    // Valida los cambios y solicita actualizar la reserva.
    @PostMapping("/actualizar/{id}")
    public String actualizar(
            @PathVariable Long id,
            @Valid @ModelAttribute("reserva") Reserva reserva,
            BindingResult resultado,
            @RequestParam Long clienteId,
            @RequestParam Long mesaId,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Si los datos no cumplen las validaciones, mantiene abierto el formulario.
        if (resultado.hasErrors()) {
            cargarDatosFormulario(model);
            return "reservas/formulario";
        }

        try {
            reservaService.actualizarReserva(id, reserva, clienteId, mesaId);
            redirectAttributes.addFlashAttribute(
                    "mensaje", "Reserva actualizada correctamente.");
            return "redirect:/reservas";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            cargarDatosFormulario(model);
            return "reservas/formulario";
        }
    }

    // Cancela la reserva sin eliminar su registro de la base de datos.
    @PostMapping("/cancelar/{id}")
    public String cancelar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            reservaService.cancelarReserva(id);
            redirectAttributes.addFlashAttribute(
                    "mensaje", "Reserva cancelada correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/reservas";
    }

    // Carga los clientes registrados y las mesas que están disponibles.
    private void cargarDatosFormulario(Model model) {
        model.addAttribute("clientes", clienteService.listarClientes());
        model.addAttribute(
                "mesas",
                mesaService.listarPorEstado(Mesa.EstadoMesa.DISPONIBLE));
    }
}
