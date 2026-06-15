package br.edu.ifg.model.dto;

import br.edu.ifg.model.Prato;

public record PratoResponseDTO(
        Long id,
        String nome,
        String descricao,
        Double preco
) {
    public static PratoResponseDTO de(Prato p) {
        return new PratoResponseDTO(
                p.getId(),
                p.getNome(),
                p.getDescricao(),
                p.getPreco()
        );
    }
}