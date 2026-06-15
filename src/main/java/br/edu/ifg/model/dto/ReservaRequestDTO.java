package br.edu.ifg.model.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ReservaRequestDTO(
        @NotNull(message = "Mesa é obrigatória")
        Long mesaId,
        @NotNull(message = "Data e hora de início são obrigatórias")
        @Future(message = "Data e hora de início devem ser no futuro")
        LocalDateTime dataHoraInicio,
        @NotNull(message = "Data e hora de término são obrigatórias")
        LocalDateTime dataHoraFim,
        @NotNull(message = "Número de pessoas é obrigatório")
        @Min(value = 1, message = "Número de pessoas deve ser pelo menos 1")
        Integer numeroPessoas,
        String observacao
) {}
