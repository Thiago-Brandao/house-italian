package br.edu.ifg.model.dto;

import br.edu.ifg.model.LogAuditoria;
import java.time.LocalDateTime;

public record LogAuditoriaResponseDTO(
        Long id,
        String acao,
        String entidade,
        Long entidadeId,
        String usuarioNome,
        String usuarioEmail,
        LocalDateTime dataHora,
        String detalhes
) {
    public static LogAuditoriaResponseDTO de(LogAuditoria log) {
        return new LogAuditoriaResponseDTO(
                log.getId(),
                log.getAcao(),
                log.getEntidade(),
                log.getEntidadeId(),
                log.getUsuario() != null ? log.getUsuario().getNome() : "Sistema",
                log.getUsuario() != null ? log.getUsuario().getEmail() : null,
                log.getDataHora(),
                log.getDetalhes()
        );
    }
}