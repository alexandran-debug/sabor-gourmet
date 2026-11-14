package com.saborgourmet.controller;

import com.saborgourmet.model.Cliente;
import com.saborgourmet.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Controla las operaciones de los clientes.
@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // Muestra el listado de clientes.
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clienteService.listarClientes());
        return "clientes/listar";
    }

    // Muestra el formulario para registrar un cliente.
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/formulario";
    }

    // Guarda un cliente nuevo.
    @PostMapping("/guardar")
    public String guardar(
            @Valid @ModelAttribute("cliente") Cliente cliente,
            BindingResult resultado,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (resultado.hasErrors()) {
            return "clientes/formulario";
        }

        if (clienteService.buscarPorCorreo(cliente.getCorreo()).isPresent()) {
            model.addAttribute("error", "Ya existe un cliente con ese correo.");
            return "clientes/formulario";
        }

        clienteService.guardarCliente(cliente);
        redirectAttributes.addFlashAttribute(
                "mensaje", "Cliente registrado correctamente.");

        return "redirect:/clientes";
    }

    // Muestra el formulario para editar un cliente.
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el cliente solicitado."));

        model.addAttribute("cliente", cliente);
        return "clientes/formulario";
    }

    // Actualiza los datos de un cliente.
    @PostMapping("/actualizar/{id}")
    public String actualizar(
            @PathVariable Long id,
            @Valid @ModelAttribute("cliente") Cliente cliente,
            BindingResult resultado,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (resultado.hasErrors()) {
            return "clientes/formulario";
        }

        if (clienteService.buscarPorCorreo(cliente.getCorreo())
                .filter(existente -> !existente.getId().equals(id)).isPresent()) {
            model.addAttribute("error", "Ese correo ya pertenece a otro cliente.");
            return "clientes/formulario";
        }

        clienteService.actualizarCliente(id, cliente);
        redirectAttributes.addFlashAttribute(
                "mensaje", "Cliente actualizado correctamente.");

        return "redirect:/clientes";
    }

    // Elimina un cliente.
    @PostMapping("/eliminar/{id}")
    public String eliminar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        clienteService.eliminarCliente(id);
        redirectAttributes.addFlashAttribute(
                "mensaje", "Cliente eliminado correctamente.");

        return "redirect:/clientes";
    }
}
