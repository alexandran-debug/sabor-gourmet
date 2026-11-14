
package com.saborgourmet.service;

import com.saborgourmet.model.Cliente;
import com.saborgourmet.model.Mesa;
import com.saborgourmet.model.Reserva;
import com.saborgourmet.repository.ClienteRepository;
import com.saborgourmet.repository.MesaRepository;
import com.saborgourmet.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

// Contiene la lógica de negocio relacionada con las reservas.
@Service
@Transactional
public class ReservaService {

    // Duración establecida para cada reserva.
    private static final int DURACION_RESERVA_HORAS = 2;

    private final ReservaRepository reservaRepository;
    private final ClienteRepository clienteRepository;
    private final MesaRepository mesaRepository;

    public ReservaService(
            ReservaRepository reservaRepository,
            ClienteRepository clienteRepository,
            MesaRepository mesaRepository) {
        this.reservaRepository = reservaRepository;
        this.clienteRepository = clienteRepository;
        this.mesaRepository = mesaRepository;
    }

    // Obtiene todas las reservas registradas.
    @Transactional(readOnly = true)
    public List<Reserva> listarReservas() {
        return reservaRepository.findAll();
    }

    // Busca una reserva por su identificador.
    @Transactional(readOnly = true)
    public Optional<Reserva> buscarPorId(Long id) {
        return reservaRepository.findById(id);
    }

    // Busca las reservas de un cliente.
    @Transactional(readOnly = true)
    public List<Reserva> listarPorCliente(Long clienteId) {
        return reservaRepository.findByClienteId(clienteId);
    }

    // Busca las reservas correspondientes a una fecha.
    @Transactional(readOnly = true)
    public List<Reserva> listarPorFecha(LocalDate fecha) {
        return reservaRepository.findByFecha(fecha);
    }

    // Registra una nueva reserva.
    public Reserva crearReserva(
            Reserva reserva,
            Long clienteId,
            Long mesaId) {

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el cliente solicitado."
                ));

        Mesa mesa = mesaRepository.findById(mesaId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró la mesa solicitada."
                ));

        validarReserva(reserva, mesa, null);

        reserva.setCliente(cliente);
        reserva.setMesa(mesa);
        reserva.setEstado(Reserva.EstadoReserva.PENDIENTE);

        return reservaRepository.save(reserva);
    }

    // Modifica una reserva existente.
    public Reserva actualizarReserva(
            Long id,
            Reserva datosActualizados,
            Long clienteId,
            Long mesaId) {

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró la reserva solicitada."
                ));

        if (reserva.getEstado() == Reserva.EstadoReserva.CANCELADA) {
            throw new IllegalArgumentException(
                    "No se puede modificar una reserva cancelada."
            );
        }

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el cliente solicitado."
                ));

        Mesa mesa = mesaRepository.findById(mesaId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró la mesa solicitada."
                ));

        validarReserva(datosActualizados, mesa, id);

        reserva.setFecha(datosActualizados.getFecha());
        reserva.setHora(datosActualizados.getHora());
        reserva.setCantidadPersonas(datosActualizados.getCantidadPersonas());
        reserva.setObservaciones(datosActualizados.getObservaciones());
        reserva.setCliente(cliente);
        reserva.setMesa(mesa);

        return reservaRepository.save(reserva);
    }

    // Comprueba que la reserva cumpla las reglas del restaurante.
    private void validarReserva(
            Reserva reserva,
            Mesa mesa,
            Long reservaExcluidaId) {

        if (reserva.getFecha() == null || reserva.getHora() == null) {
            throw new IllegalArgumentException(
                    "La fecha y la hora son obligatorias."
            );
        }

        if (!reserva.getFecha().isAfter(LocalDate.now())
                && reserva.getFecha().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "No se pueden realizar reservas en fechas pasadas."
            );
        }

        if (reserva.getFecha().equals(LocalDate.now())
                && !reserva.getHora().isAfter(LocalTime.now())) {
            throw new IllegalArgumentException(
                    "La hora de la reserva debe ser posterior a la hora actual."
            );
        }

        if (mesa.getEstado() != Mesa.EstadoMesa.DISPONIBLE) {
            throw new IllegalArgumentException(
                    "La mesa seleccionada no está disponible."
            );
        }

        if (reserva.getCantidadPersonas() == null
                || reserva.getCantidadPersonas() < 1) {
            throw new IllegalArgumentException(
                    "La cantidad de personas debe ser al menos 1."
            );
        }

        if (reserva.getCantidadPersonas() > mesa.getCapacidad()) {
            throw new IllegalArgumentException(
                    "La capacidad de la mesa es insuficiente."
            );
        }

        // Calcula el horario de inicio y término de la reserva.
        LocalDateTime inicioNuevo = LocalDateTime.of(
                reserva.getFecha(), reserva.getHora());

        LocalDateTime finNuevo = inicioNuevo.plusHours(
                DURACION_RESERVA_HORAS);

        // Revisa las reservas existentes para detectar cruces de horario.
        List<Reserva> reservasExistentes =
                reservaRepository.findByFechaAndEstadoNot(
                        reserva.getFecha(),
                        Reserva.EstadoReserva.CANCELADA);

        for (Reserva existente : reservasExistentes) {

            // Ignora la misma reserva cuando se está modificando.
            if (reservaExcluidaId != null
                    && existente.getId().equals(reservaExcluidaId)) {
                continue;
            }

            // Solo comprueba las reservas de la misma mesa.
            if (!existente.getMesa().getId().equals(mesa.getId())) {
                continue;
            }

            LocalDateTime inicioExistente = LocalDateTime.of(
                    existente.getFecha(), existente.getHora());

            LocalDateTime finExistente = inicioExistente.plusHours(
                    DURACION_RESERVA_HORAS);

            // Detecta si los horarios de las reservas se cruzan.
            boolean existeCruce = inicioNuevo.isBefore(finExistente)
                    && finNuevo.isAfter(inicioExistente);

            if (existeCruce) {
                throw new IllegalArgumentException(
                        "La mesa ya tiene una reserva en ese horario."
                );
            }
        }
    }

    // Cancela una reserva sin eliminar su registro.
    public Reserva cancelarReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró la reserva solicitada."
                ));

        if (reserva.getEstado() == Reserva.EstadoReserva.CANCELADA) {
            throw new IllegalArgumentException(
                    "La reserva ya se encuentra cancelada."
            );
        }

        reserva.setEstado(Reserva.EstadoReserva.CANCELADA);

        return reservaRepository.save(reserva);
    }

}
