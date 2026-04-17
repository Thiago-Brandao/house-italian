package br.edu.ifg.model;

import jakarta.persistence.Column;

public class Reserva {
    
    @Column(nullable = false, unique = true)
    private Long id;

    private Usuario usuario;

    private Mesa mesa;

}
