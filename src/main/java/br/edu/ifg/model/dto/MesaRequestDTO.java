package br.edu.ifg.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MesaRequestDTO(
        @NotNull(message = "Número da mesa é obrigatório")
        Integer numero,
        @NotNull(message = "Capacidade é obrigatória")
        @Min(value = 1, message = "Capacidade deve ser pelo menos 1")
        @Max(value = 20, message = "Capacidade deve ser no máximo 20")
        Integer capacidade,
        @NotBlank(message = "Localização é obrigatória")
        String localizacao,
        String descricao
) {}
