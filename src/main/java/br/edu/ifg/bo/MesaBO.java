package br.edu.ifg.bo;

import br.edu.ifg.dao.MesaDAO;
import br.edu.ifg.model.Mesa;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class MesaBO {

    @Inject
    MesaDAO mesaDAO;

    // Lista todas as mesas (ADMIN vê todas, CLIENTE só as ativas)
    public List<Mesa> listarTodas() {
        return mesaDAO.listAll();
    }

    public List<Mesa> listarAtivas() {
        return mesaDAO.listarTodasAtivas();
    }

    // Busca por ID
    public Mesa buscarPorId(Long id) {
        Mesa mesa = mesaDAO.findById(id);
        if (mesa == null) {
            throw new IllegalArgumentException("Mesa não encontrada.");
        }
        return mesa;
    }

    // Cria nova mesa — UC-02
    @Transactional
    public Mesa criar(Integer numero, Integer capacidade,
                      String localizacao, String descricao) {

        // Regra 1: O número precissa ser único
        if (mesaDAO.existeMesaNumero(numero)) {
            throw new IllegalArgumentException(
                "Já existe uma mesa com o número " + numero + ".");
        }

        // Regra 2: A capacidade deve ser entre 1 e 20
        if (capacidade < 1 || capacidade > 20) {
            throw new IllegalArgumentException(
                "Capacidade deve ser entre 1 e 20 pessoas.");
        }

        // Regra 3: A localização deve ser obrigatória
        if (localizacao == null || localizacao.isBlank()) {
            throw new IllegalArgumentException(
                "A localização é obrigatória.");
        }

        // Cria mesa
        Mesa mesa = new Mesa();
        mesa.setNumero(numero);
        mesa.setCapacidade(capacidade);
        mesa.setLocalizacao(localizacao.trim());
        mesa.setDescricao(descricao);
        mesa.setAtiva(true);

        mesaDAO.persist(mesa);
        return mesa;
    }

    // Edita mesa — UC-02
    @Transactional
    public Mesa editar(Long id, Integer numero, Integer capacidade,
                       String localizacao, String descricao) {

        Mesa mesa = buscarPorId(id);

        // Regra: número único (ignorando a própria mesa)
        if (mesaDAO.existeMesaNumeroeId(numero, id)) {
            throw new IllegalArgumentException(
                "Já existe outra mesa com o número " + numero + ".");
        }

        if (capacidade < 1 || capacidade > 20) {
            throw new IllegalArgumentException(
                "Capacidade deve ser entre 1 e 20 pessoas.");
        }

        if (localizacao == null || localizacao.isBlank()) {
            throw new IllegalArgumentException(
                "A localização é obrigatória.");
        }

        // Edita a mesa
        mesa.setNumero(numero);
        mesa.setCapacidade(capacidade);
        mesa.setLocalizacao(localizacao.trim());
        mesa.setDescricao(descricao);

        return mesa;
    }

    // Ativa ou inativa mesa — UC-02
    @Transactional
    public Mesa alterarStatus(Long id, Boolean ativa) {
        Mesa mesa = buscarPorId(id);
        mesa.setAtiva(ativa);
        return mesa;
    }
}