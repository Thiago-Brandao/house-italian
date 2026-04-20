package br.edu.ifg.dto;
import br.edu.ifg.model.Mesa;

public record MesaResponseDTO(
    Long id,
    Integer numero,
    Integer capacidade,
    String localizacao,
    Boolean ativa,
    String descricao
)

{ public static MesaResponseDTO de(Mesa m){
    return new MesaResponseDTO (
            m.getId(),
            m.getNumero(),
            m.getCapacidade(),
            m.getLocalizacao(),
            m.getAtiva(),
            m.getDescricao()
        );
    }   
}
