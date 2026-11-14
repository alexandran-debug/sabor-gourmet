
package com.saborgourmet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

// Representa las reservas realizadas en el restaurante.
@Entity
@Table(name = "reservas")
public class Reserva {

    // Identificador único de la reserva.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Fecha en que se realizará la reserva.
    @NotNull(message = "La fecha es obligatoria")
    @FutureOrPresent(message = "La fecha no puede ser anterior a hoy")
    @Column(nullable = false)
    private LocalDate fecha;

    // Hora de la reserva.
    @NotNull(message = "La hora es obligatoria")
    @Column(nullable = false)
    private LocalTime hora;

    // Cantidad de personas que asistirán.
    @NotNull(message = "La cantidad de personas es obligatoria")
    @Min(value = 1, message = "Debe asistir al menos una persona")
    @Column(nullable = false)
    private Integer cantidadPersonas;

    // Estado actual de la reserva.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoReserva estado = EstadoReserva.PENDIENTE;

    // Observaciones adicionales de la reserva.
    @Size(max = 300, message = "Las observaciones no pueden superar los 300 caracteres")
    @Column(length = 300)
    private String observaciones;

    // Relación: muchas reservas pueden pertenecer a un cliente.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // Relación: una mesa puede tener varias reservas en distintos horarios.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mesa_id", nullable = false)
    private Mesa mesa;

    // Constructor vacío requerido por JPA.
    public Reserva() {
    }

    // Estados permitidos para una reserva.
    public enum EstadoReserva {
        PENDIENTE,
        CONFIRMADA,
        CANCELADA
    }

    // Getters y setters.

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public Integer getCantidadPersonas() {
        return cantidadPersonas;
    }

    public void setCantidadPersonas(Integer cantidadPersonas) {
        this.cantidadPersonas = cantidadPersonas;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }
}
