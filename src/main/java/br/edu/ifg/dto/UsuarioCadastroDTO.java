package br.edu.ifg.dto;

// O que o Front envia quando alguém se cadastra

public record UsuarioCadastroDTO(
        String nome,
        String email,
        String senha
) {}