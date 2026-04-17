package br.edu.ifg.dto;

import br.edu.ifg.model.Role;
import java.time.LocalDateTime;

// O que o back-end devolve - Menos a senha

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        Role perfil,
        Boolean ativo,
        LocalDateTime dataCadastro
) {
    // Método estático que converte uma Entity em DTO
    // O BO vai chamar isso antes de retornar para o Resource
    public static UsuarioResponseDTO de(br.edu.ifg.model.Usuario u) {
        return new UsuarioResponseDTO(
                u.getId(),
                u.getNome(),
                u.getEmail(),
                u.getPerfil(),
                u.getAtivo(),
                u.getDataCadastro()
        );
    }
}