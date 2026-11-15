package com.saborgourmet.controller;

import com.saborgourmet.model.Cliente;
import com.saborgourmet.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Controla las acciones relacionadas con el registro y administración de clientes.
@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    // Recibe el servicio que se encarga de trabajar con los datos de los clientes.
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // Muestra el listado de todos los clientes registrados.
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clienteService.listarClientes());
        return "clientes/listar";
    }

    // Abre el formulario con un cliente vacío para registrar uno nuevo.
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/formulario";
    }

    // Valida los datos y guarda el nuevo cliente si no existe otro con el mismo correo.
    @PostMapping("/guardar")
    public String guardar(
            @Valid @ModelAttribute("cliente") Cliente cliente,
            BindingResult resultado,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Si hay datos incorrectos, vuelve al formulario para corregirlos.
        if (resultado.hasErrors()) {
            return "clientes/formulario";
        }

        // Evita registrar dos clientes con la misma dirección de correo.
        if (clienteService.buscarPorCorreo(cliente.getCorreo()).isPresent()) {
            model.addAttribute("error", "Ya existe un cliente con ese correo.");
            return "clientes/formulario";
        }

        clienteService.guardarCliente(cliente);
        redirectAttributes.addFlashAttribute(
                "mensaje", "Cliente registrado correctamente.");

        return "redirect:/clientes";
    }

    // Busca al cliente seleccionado y carga sus datos en el formulario de edición.
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el cliente solicitado."));

        model.addAttribute("cliente", cliente);
        return "clientes/formulario";
    }

    // Actualiza los datos del cliente después de validar la información ingresada.
    @PostMapping("/actualizar/{id}")
    public String actualizar(
            @PathVariable Long id,
            @Valid @ModelAttribute("cliente") Cliente cliente,
            BindingResult resultado,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Si algún dato no cumple las validaciones, mantiene abierto el formulario.
        if (resultado.hasErrors()) {
            return "clientes/formulario";
        }

        // Comprueba que el correo no esté registrado por otro cliente.
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

    // Elimina al cliente seleccionado y vuelve al listado.
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
