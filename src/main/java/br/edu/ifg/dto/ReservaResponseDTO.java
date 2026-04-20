package br.edu.ifg.dto;

import br.edu.ifg.model.Reserva;
import br.edu.ifg.model.StatusReserva;
import java.time.LocalDateTime;

public record ReservaResponseDTO(
        Long id,
        Long mesaId,
        Integer mesaNumero,
        String mesaLocalizacao,
        Long usuarioId,
        String usuarioNome,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        Integer numeroPessoas,
        StatusReserva status,
        String observacao,
        LocalDateTime dataCriacao
) {
    public static ReservaResponseDTO de(Reserva r) {
        return new ReservaResponseDTO(
                r.getId(),
                r.getMesa().getId(),
                r.getMesa().getNumero(),
                r.getMesa().getLocalizacao(),
                r.getUsuario().getId(),
                r.getUsuario().getNome(),
                r.getDataHoraInicio(),
                r.getDataHoraFim(),
                r.getNumeroPessoas(),
                r.getStatus(),
                r.getObservacao(),
                r.getDataCriacao()
        );
    }
}
