package com.saborgourmet.repository;

import com.saborgourmet.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

// Gestiona las operaciones de base de datos de las reservas.
@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    // Busca todas las reservas de una fecha.
    List<Reserva> findByFecha(LocalDate fecha);

    // Busca las reservas realizadas por un cliente.
    List<Reserva> findByClienteId(Long clienteId);

    // Busca las reservas de una fecha que no tengan un estado determinado.
    List<Reserva> findByFechaAndEstadoNot(
            LocalDate fecha,
            Reserva.EstadoReserva estado
    );
}
