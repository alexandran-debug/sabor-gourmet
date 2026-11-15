package com.saborgourmet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Representa a los clientes registrados en el restaurante.
@Entity
@Table(name = "clientes")
public class Cliente {

    // Identificador único que se genera automáticamente para cada cliente.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // El nombre es obligatorio y no puede superar los 60 caracteres.
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 60)
    @Column(nullable = false, length = 60)
    private String nombre;

    // Se guarda el apellido del cliente con las mismas restricciones de longitud.
    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 60)
    @Column(nullable = false, length = 60)
    private String apellido;

    // El correo debe tener un formato válido y no puede repetirse en la base de datos.
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Ingrese un correo válido")
    @Column(nullable = false, unique = true, length = 120)
    private String correo;

    // Número de teléfono utilizado como dato de contacto.
    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String telefono;

    // Constructor vacío necesario para que JPA pueda crear objetos de esta entidad.
    public Cliente() {
    }

    // Getters y setters para consultar y modificar los datos del cliente.

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
