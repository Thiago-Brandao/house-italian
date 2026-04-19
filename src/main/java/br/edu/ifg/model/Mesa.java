package br.edu.ifg.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_mesa")
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer numero;

    @Column(nullable = false)
    private Integer capacidade;

    @Column(nullable = false, length = 100)
    private String localizacao;

    @Column(nullable = false)
    private Boolean ativa = true;

    @Column(length = 255)
    private String descricao;

    @PrePersist
    public void prePersist() {
        if (this.ativa == null) this.ativa = true;
    }
}