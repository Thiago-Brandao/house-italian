package br.edu.ifg.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor


@Entity
@Table(name = "tb_mesa")
public class Mesa {

    @Column(nullable = false, unique = true)
    private Long id;

    @Column(nullable = false,unique = true,length = 30)
    private Integer numero;

    @Column(nullable = false,length = 6)
    private Integer capacidade;

    @Column(nullable = false,length = 30)
    private String localizacao;

    @Column(nullable = false)
    private Boolean ativa;

        @Column(nullable = true)
    private String descricao;

}
