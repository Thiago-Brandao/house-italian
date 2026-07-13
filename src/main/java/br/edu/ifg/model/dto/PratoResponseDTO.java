package br.edu.ifg.model.dto;

import br.edu.ifg.model.CategoriaPrato;
import br.edu.ifg.model.Prato;

public record PratoResponseDTO(
        Long id,
        String nome,
        String descricao,
        Double preco,
        CategoriaPrato categoria
) {
    public static PratoResponseDTO de(Prato p) {
        return new PratoResponseDTO(
                p.getId(),
                p.getNome(),
                p.getDescricao(),
                p.getPreco(),
                p.getCategoria()
        );
    }
}