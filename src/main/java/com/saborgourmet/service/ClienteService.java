
package com.saborgourmet.service;

import com.saborgourmet.model.Cliente;
import com.saborgourmet.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

// Contiene la lógica de negocio relacionada con los clientes.
@Service
@Transactional
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    // Obtiene todos los clientes registrados.
    @Transactional(readOnly = true)
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    // Busca un cliente por su identificador.
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    // Busca un cliente mediante su correo electrónico.
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorCorreo(String correo) {
        return clienteRepository.findByCorreo(correo);
    }

    // Registra un cliente nuevo.
    public Cliente guardarCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    // Actualiza los datos de un cliente existente.
    public Cliente actualizarCliente(Long id, Cliente datosActualizados) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el cliente solicitado."
                ));

        cliente.setNombre(datosActualizados.getNombre());
        cliente.setApellido(datosActualizados.getApellido());
        cliente.setCorreo(datosActualizados.getCorreo());
        cliente.setTelefono(datosActualizados.getTelefono());

        return clienteRepository.save(cliente);
    }

    // Elimina un cliente por su identificador.
    public void eliminarCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "No se encontró el cliente solicitado."
            );
        }

        clienteRepository.deleteById(id);
    }
}
