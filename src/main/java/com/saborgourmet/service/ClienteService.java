package com.saborgourmet.service;

import com.saborgourmet.model.Cliente;
import com.saborgourmet.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

// Se encarga de las operaciones que se realizan con los clientes.
@Service
@Transactional
public class ClienteService {

    private final ClienteRepository clienteRepository;

    // Recibe el repositorio que permite trabajar con los datos de los clientes.
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    // Obtiene la lista completa de clientes registrados.
    @Transactional(readOnly = true)
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    // Busca un cliente usando su identificador.
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    // Permite consultar si un correo ya está registrado.
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorCorreo(String correo) {
        return clienteRepository.findByCorreo(correo);
    }

    // Guarda los datos de un cliente nuevo en la base de datos.
    public Cliente guardarCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    // Busca al cliente y reemplaza sus datos por la información actualizada.
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

    // Comprueba que el cliente exista antes de eliminarlo.
    public void eliminarCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "No se encontró el cliente solicitado."
            );
        }

        clienteRepository.deleteById(id);
    }
}
