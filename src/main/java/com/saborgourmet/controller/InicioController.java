package com.saborgourmet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Controla el acceso a la página principal.
@Controller
public class InicioController {

    @GetMapping("/")
    public String inicio() {
        return "inicio";
    }
}
