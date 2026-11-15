package com.saborgourmet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// Representa las mesas disponibles en el restaurante.
@Entity
@Table(name = "mesas")
public class Mesa {

    // Identificador único que se genera automáticamente.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Número de mesa, obligatorio y sin valores menores que 1.
    @NotNull(message = "El número de mesa es obligatorio")
    @Min(value = 1, message = "El número debe ser mayor que cero")
    @Column(nullable = false, unique = true)
    private Integer numero;

    // Cantidad máxima de personas que puede recibir la mesa.
    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    @Column(nullable = false)
    private Integer capacidad;

    // Lugar donde se encuentra la mesa dentro del restaurante.
    @Column(length = 80)
    private String ubicacion;

    // Indica si la mesa está disponible para recibir reservas.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoMesa estado = EstadoMesa.DISPONIBLE;

    // Constructor vacío necesario para JPA.
    public Mesa() {
    }

    // Estos son los dos estados que puede tener una mesa.
    public enum EstadoMesa {
        DISPONIBLE,
        NO_DISPONIBLE
    }

    // Getters y setters para consultar y modificar los datos de la mesa.

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
