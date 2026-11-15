package com.saborgourmet.repository;

import com.saborgourmet.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

// Permite consultar y administrar las reservas mediante Spring Data JPA.
@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    // Obtiene todas las reservas correspondientes a una fecha.
    List<Reserva> findByFecha(LocalDate fecha);

    // Busca las reservas asociadas a un cliente específico.
    List<Reserva> findByClienteId(Long clienteId);

    // Obtiene las reservas de una fecha, excluyendo el estado indicado.
    List<Reserva> findByFechaAndEstadoNot(
            LocalDate fecha,
            Reserva.EstadoReserva estado
    );
}
