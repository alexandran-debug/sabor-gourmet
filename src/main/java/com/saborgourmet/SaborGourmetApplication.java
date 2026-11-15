package com.saborgourmet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Clase principal que permite iniciar la aplicación Sabor Gourmet.
@SpringBootApplication
public class SaborGourmetApplication {

    // Inicia Spring Boot y pone en marcha los componentes del sistema.
    public static void main(String[] args) {
        SpringApplication.run(SaborGourmetApplication.class, args);
    }

}
