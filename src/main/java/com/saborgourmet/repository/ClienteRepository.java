package com.saborgourmet.repository;

import com.saborgourmet.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Permite acceder a los datos de los clientes mediante Spring Data JPA.
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Busca un cliente por correo y devuelve un resultado si existe.
    Optional<Cliente> findByCorreo(String correo);
}
