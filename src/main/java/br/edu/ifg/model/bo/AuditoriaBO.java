package br.edu.ifg.model.bo;

import br.edu.ifg.model.dao.LogAuditoriaDAO;
import br.edu.ifg.model.LogAuditoria;
import br.edu.ifg.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class AuditoriaBO {

    @Inject
    LogAuditoriaDAO logDAO;

    // Registra uma ação no log
    @Transactional
    public void registrar(String acao, String entidade,
                          Long entidadeId, Usuario usuario,
                          String detalhes) {
        LogAuditoria log = new LogAuditoria();
        log.setAcao(acao);
        log.setEntidade(entidade);
        log.setEntidadeId(entidadeId);
        log.setUsuario(usuario);
        log.setDetalhes(detalhes);

        logDAO.persist(log);
    }

    // Lista todos os logs (ADMIN)
    public List<LogAuditoria> listarTodos() {
        return logDAO.listarTodos();
    }

    // Lista logs por usuário
    public List<LogAuditoria> listarPorUsuario(Long usuarioId) {
        return logDAO.findByUsuarioId(usuarioId);
    }
}