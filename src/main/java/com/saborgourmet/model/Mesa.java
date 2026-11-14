
package com.saborgourmet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// Representa las mesas disponibles en el restaurante.
@Entity
@Table(name = "mesas")
public class Mesa {

    // Identificador único de la mesa.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Número identificador de la mesa.
    @NotNull(message = "El número de mesa es obligatorio")
    @Min(value = 1, message = "El número debe ser mayor que cero")
    @Column(nullable = false, unique = true)
    private Integer numero;

    // Cantidad máxima de personas que admite la mesa.
    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    @Column(nullable = false)
    private Integer capacidad;

    // Ubicación de la mesa dentro del restaurante.
    @Column(length = 80)
    private String ubicacion;

    // Estado actual de la mesa.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoMesa estado = EstadoMesa.DISPONIBLE;

    // Constructor vacío requerido por JPA.
    public Mesa() {
    }

    // Estados permitidos para una mesa.
    public enum EstadoMesa {
        DISPONIBLE,
        NO_DISPONIBLE
    }

    // Getters y setters.

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public EstadoMesa getEstado() {
        return estado;
    }

    public void setEstado(EstadoMesa estado) {
        this.estado = estado;
    }
}
