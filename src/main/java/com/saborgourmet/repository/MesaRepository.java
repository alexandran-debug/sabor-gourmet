
package com.saborgourmet.repository;

import com.saborgourmet.model.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Gestiona las operaciones de base de datos de las mesas.
@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {

    // Busca las mesas según su estado.
    List<Mesa> findByEstado(Mesa.EstadoMesa estado);

    // Comprueba si ya existe una mesa con ese número.
    boolean existsByNumero(Integer numero);
}
