
package com.saborgourmet.service;

import com.saborgourmet.model.Mesa;
import com.saborgourmet.repository.MesaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

// Contiene la lógica de negocio relacionada con las mesas.
@Service
@Transactional
public class MesaService {

    private final MesaRepository mesaRepository;

    public MesaService(MesaRepository mesaRepository) {
        this.mesaRepository = mesaRepository;
    }

    // Obtiene todas las mesas registradas.
    @Transactional(readOnly = true)
    public List<Mesa> listarMesas() {
        return mesaRepository.findAll();
    }

    // Busca una mesa por su identificador.
    @Transactional(readOnly = true)
    public Optional<Mesa> buscarPorId(Long id) {
        return mesaRepository.findById(id);
    }

    // Obtiene las mesas según su disponibilidad.
    @Transactional(readOnly = true)
    public List<Mesa> listarPorEstado(Mesa.EstadoMesa estado) {
        return mesaRepository.findByEstado(estado);
    }

    // Registra una mesa nueva.
    public Mesa guardarMesa(Mesa mesa) {
        if (mesaRepository.existsByNumero(mesa.getNumero())) {
            throw new IllegalArgumentException(
                    "Ya existe una mesa con ese número."
            );
        }

        return mesaRepository.save(mesa);
    }

    // Actualiza los datos de una mesa existente.
    public Mesa actualizarMesa(Long id, Mesa datosActualizados) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró la mesa solicitada."
                ));

        // Comprueba que el número no esté asignado a otra mesa.
        if (!mesa.getNumero().equals(datosActualizados.getNumero())
                && mesaRepository.existsByNumero(datosActualizados.getNumero())) {
            throw new IllegalArgumentException(
                    "Ya existe otra mesa con ese número."
            );
        }

        mesa.setNumero(datosActualizados.getNumero());
        mesa.setCapacidad(datosActualizados.getCapacidad());
        mesa.setUbicacion(datosActualizados.getUbicacion());
        mesa.setEstado(datosActualizados.getEstado());

        return mesaRepository.save(mesa);
    }

    // Cambia la disponibilidad de una mesa.
    public Mesa cambiarEstado(Long id, Mesa.EstadoMesa nuevoEstado) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró la mesa solicitada."
                ));

        mesa.setEstado(nuevoEstado);

        return mesaRepository.save(mesa);
    }

    // Elimina una mesa por su identificador.
    public void eliminarMesa(Long id) {
        if (!mesaRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "No se encontró la mesa solicitada."
            );
        }

        mesaRepository.deleteById(id);
    }
}
