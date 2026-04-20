package br.edu.ifg.dto;

public record MesaRequestDTO(
        Integer numero,
        Integer capacidade,
        String localizacao,
        String descricao
) {}
