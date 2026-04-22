package br.edu.ifg.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_log_auditoria")
public class LogAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String acao;

    @Column(length = 50)
    private String entidade;

    private Long entidadeId;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(length = 50)
    private String ip;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Column(length = 500)
    private String detalhes;

    @PrePersist
    public void prePersist() {
        this.dataHora = LocalDateTime.now();
    }
}