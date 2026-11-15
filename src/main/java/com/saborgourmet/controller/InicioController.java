package com.saborgourmet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Controla el acceso a la página principal del restaurante.
@Controller
public class InicioController {

    // Muestra la página de inicio cuando se ingresa a la dirección principal.
    @GetMapping("/")
    public String inicio() {
        return "inicio";
    }
}
