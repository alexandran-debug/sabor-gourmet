package com.saborgourmet.repository;

import com.saborgourmet.model.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Permite consultar y administrar las mesas mediante Spring Data JPA.
@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {

    // Obtiene las mesas que tienen el estado indicado.
    List<Mesa> findByEstado(Mesa.EstadoMesa estado);

    // Comprueba si el número de mesa ya está registrado.
    boolean existsByNumero(Integer numero);
}
