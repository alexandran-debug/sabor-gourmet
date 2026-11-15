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

// Aquí se encuentran las reglas y operaciones para gestionar las reservas.
@Service
@Transactional
public class ReservaService {

    // Cada reserva ocupa la mesa durante dos horas.
    private static final int DURACION_RESERVA_HORAS = 2;

    private final ReservaRepository reservaRepository;
    private final ClienteRepository clienteRepository;
    private final MesaRepository mesaRepository;

    // Recibe los repositorios que permiten consultar y guardar la información.
    public ReservaService(
            ReservaRepository reservaRepository,
            ClienteRepository clienteRepository,
            MesaRepository mesaRepository) {
        this.reservaRepository = reservaRepository;
        this.clienteRepository = clienteRepository;
        this.mesaRepository = mesaRepository;
    }

    // Obtiene todas las reservas para mostrarlas en el sistema.
    @Transactional(readOnly = true)
    public List<Reserva> listarReservas() {
        return reservaRepository.findAll();
    }

    // Busca una reserva específica a partir de su ID.
    @Transactional(readOnly = true)
    public Optional<Reserva> buscarPorId(Long id) {
        return reservaRepository.findById(id);
    }

    // Obtiene las reservas asociadas a un cliente.
    @Transactional(readOnly = true)
    public List<Reserva> listarPorCliente(Long clienteId) {
        return reservaRepository.findByClienteId(clienteId);
    }

    // Filtra las reservas según la fecha seleccionada.
    @Transactional(readOnly = true)
    public List<Reserva> listarPorFecha(LocalDate fecha) {
        return reservaRepository.findByFecha(fecha);
    }

    // Crea una reserva relacionándola con el cliente y la mesa seleccionados.
    public Reserva crearReserva(
            Reserva reserva,
            Long clienteId,
            Long mesaId) {

        // Comprueba que el cliente exista antes de continuar.
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el cliente solicitado."
                ));

        // Comprueba que la mesa seleccionada esté registrada.
        Mesa mesa = mesaRepository.findById(mesaId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró la mesa solicitada."
                ));

        // Revisa que la fecha, la hora y la mesa cumplan las reglas del sistema.
        validarReserva(reserva, mesa, null);

        reserva.setCliente(cliente);
        reserva.setMesa(mesa);
        reserva.setEstado(Reserva.EstadoReserva.PENDIENTE);

        return reservaRepository.save(reserva);
    }

    // Actualiza los datos de una reserva que ya existe.
    public Reserva actualizarReserva(
            Long id,
            Reserva datosActualizados,
            Long clienteId,
            Long mesaId) {

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró la reserva solicitada."
                ));

        // Una reserva cancelada no puede volver a modificarse.
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

        // Valida los nuevos datos antes de guardar los cambios.
        validarReserva(datosActualizados, mesa, id);

        reserva.setFecha(datosActualizados.getFecha());
        reserva.setHora(datosActualizados.getHora());
        reserva.setCantidadPersonas(datosActualizados.getCantidadPersonas());
        reserva.setObservaciones(datosActualizados.getObservaciones());
        reserva.setCliente(cliente);
        reserva.setMesa(mesa);

        return reservaRepository.save(reserva);
    }

    // Revisa que la reserva cumpla las condiciones antes de guardarla.
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

        // No permite reservar mesas que estén marcadas como no disponibles.
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

        // Comprueba que la mesa tenga espacio para todas las personas.
        if (reserva.getCantidadPersonas() > mesa.getCapacidad()) {
            throw new IllegalArgumentException(
                    "La capacidad de la mesa es insuficiente."
            );
        }

        // Calcula cuánto tiempo estará ocupada la mesa.
        LocalDateTime inicioNuevo = LocalDateTime.of(
                reserva.getFecha(), reserva.getHora());

        LocalDateTime finNuevo = inicioNuevo.plusHours(
                DURACION_RESERVA_HORAS);

        // Busca las reservas de esa fecha, dejando fuera las que fueron canceladas.
        List<Reserva> reservasExistentes =
                reservaRepository.findByFechaAndEstadoNot(
                        reserva.getFecha(),
                        Reserva.EstadoReserva.CANCELADA);

        for (Reserva existente : reservasExistentes) {

            // Al editar, no compara la reserva consigo misma.
            if (reservaExcluidaId != null
                    && existente.getId().equals(reservaExcluidaId)) {
                continue;
            }

            // Las reservas de otras mesas no afectan este horario.
            if (!existente.getMesa().getId().equals(mesa.getId())) {
                continue;
            }

            LocalDateTime inicioExistente = LocalDateTime.of(
                    existente.getFecha(), existente.getHora());

            LocalDateTime finExistente = inicioExistente.plusHours(
                    DURACION_RESERVA_HORAS);

            // Comprueba si el nuevo horario coincide con una reserva existente.
            boolean existeCruce = inicioNuevo.isBefore(finExistente)
                    && finNuevo.isAfter(inicioExistente);

            if (existeCruce) {
                throw new IllegalArgumentException(
                        "La mesa ya tiene una reserva en ese horario."
                );
            }
        }
    }

    // Cambia el estado de la reserva a cancelada, sin borrar sus datos.
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
