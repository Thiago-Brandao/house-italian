package br.edu.ifg.dto;

import br.edu.ifg.model.Reserva;
import br.edu.ifg.model.StatusReserva;
import java.time.LocalDateTime;

public record ReservaResponseDTO(
        Long id,
        UsuarioResponseDTO usuario,
        MesaResponseDTO mesa,
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
                UsuarioResponseDTO.de(r.getUsuario()),
                MesaResponseDTO.de(r.getMesa()),
                r.getDataHoraInicio(),
                r.getDataHoraFim(),
                r.getNumeroPessoas(),
                r.getStatus(),
                r.getObservacao(),
                r.getDataCriacao()
        );
    }
}
