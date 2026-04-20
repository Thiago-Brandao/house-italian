package br.edu.ifg.dto;

import java.time.LocalDateTime;

public record ReservaRequestDTO(
        Long mesaId,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        Integer numeroPessoas,
        String observacao
) {}
