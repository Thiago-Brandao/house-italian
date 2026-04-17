package br.edu.ifg.dto;

// O que o front envia para a tela de login

public record LoginRequestDTO(
        String email,
        String senha
) {}
