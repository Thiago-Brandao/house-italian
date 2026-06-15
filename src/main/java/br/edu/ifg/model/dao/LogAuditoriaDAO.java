package br.edu.ifg.model.dao;

import br.edu.ifg.model.LogAuditoria;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class LogAuditoriaDAO implements PanacheRepository<LogAuditoria> {

    // Lista logs de um usuário específico
    public List<LogAuditoria> findByUsuarioId(Long usuarioId) {
        return list("usuario.id = ?1 order by dataHora desc", usuarioId);
    }

    // Lista logs por ação
    public List<LogAuditoria> findByAcao(String acao) {
        return list("acao = ?1 order by dataHora desc", acao);
    }

    // Lista todos os logs ordenados por data
    public List<LogAuditoria> listarTodos() {
        return list("order by dataHora desc");
    }
}