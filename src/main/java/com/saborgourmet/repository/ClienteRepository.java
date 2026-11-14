
package com.saborgourmet.repository;

import com.saborgourmet.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Gestiona las operaciones de base de datos de los clientes.
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Busca un cliente por su correo electrónico.
    Optional<Cliente> findByCorreo(String correo);
}
