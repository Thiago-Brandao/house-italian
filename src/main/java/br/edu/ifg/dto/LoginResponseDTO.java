package br.edu.ifg.dto;

import br.edu.ifg.model.Role;

public record LoginResponseDTO(
        String token,
        String tipo,
        String nome,
        String email,
        Role perfil
) {}