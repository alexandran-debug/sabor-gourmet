package com.saborgourmet.service;

import com.saborgourmet.model.Mesa;
import com.saborgourmet.repository.MesaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

// Se encarga de administrar las mesas y controlar sus datos.
@Service
@Transactional
public class MesaService {

    private final MesaRepository mesaRepository;

    // Recibe el repositorio que permite consultar y guardar las mesas.
    public MesaService(MesaRepository mesaRepository) {
        this.mesaRepository = mesaRepository;
    }

    // Obtiene todas las mesas registradas en el restaurante.
    @Transactional(readOnly = true)
    public List<Mesa> listarMesas() {
        return mesaRepository.findAll();
    }

    // Busca una mesa a partir de su identificador.
    @Transactional(readOnly = true)
    public Optional<Mesa> buscarPorId(Long id) {
        return mesaRepository.findById(id);
    }

    // Muestra solamente las mesas que tienen el estado indicado.
    @Transactional(readOnly = true)
    public List<Mesa> listarPorEstado(Mesa.EstadoMesa estado) {
        return mesaRepository.findByEstado(estado);
    }

    // Antes de guardar, comprueba que el número de mesa no esté repetido.
    public Mesa guardarMesa(Mesa mesa) {
        if (mesaRepository.existsByNumero(mesa.getNumero())) {
            throw new IllegalArgumentException(
                    "Ya existe una mesa con ese número."
            );
        }

        return mesaRepository.save(mesa);
    }

    // Busca la mesa y actualiza sus datos con la información recibida.
    public Mesa actualizarMesa(Long id, Mesa datosActualizados) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró la mesa solicitada."
                ));

        // Evita asignar a esta mesa un número que ya utiliza otra.
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

    // Permite cambiar el estado de una mesa, por ejemplo, su disponibilidad.
    public Mesa cambiarEstado(Long id, Mesa.EstadoMesa nuevoEstado) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró la mesa solicitada."
                ));

        mesa.setEstado(nuevoEstado);

        return mesaRepository.save(mesa);
    }

    // Verifica que la mesa exista antes de eliminarla.
    public void eliminarMesa(Long id) {
        if (!mesaRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "No se encontró la mesa solicitada."
            );
        }

        mesaRepository.deleteById(id);
    }
}
