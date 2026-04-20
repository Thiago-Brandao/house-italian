package br.edu.ifg.bo;

import br.edu.ifg.dao.ReservaDAO;
import br.edu.ifg.model.Mesa;
import br.edu.ifg.model.Reserva;
import br.edu.ifg.model.StatusReserva;
import br.edu.ifg.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class ReservaBO {

    @Inject
    ReservaDAO reservaDAO;

    @Inject
    UsuarioBO usuarioBO;

    @Inject
    MesaBO mesaBO;

    // Lista todas as reservas (ADMIN)
    public List<Reserva> listarTodas() {
        return reservaDAO.listarTodas();
    }

    // Lista reservas do usuário logado (CLIENTE)
    public List<Reserva> listarPorUsuario(Long usuarioId) {
        return reservaDAO.listarPorUsuario(usuarioId);
    }

    // Busca por ID
    public Reserva buscarPorId(Long id) {
        Reserva reserva = reservaDAO.findById(id);
        if (reserva == null) {
            throw new IllegalArgumentException("Reserva não encontrada.");
        }
        return reserva;
    }

    // Cria nova reserva — UC-03
    @Transactional
    public Reserva criar(Long usuarioId, Long mesaId, LocalDateTime inicio,
                         LocalDateTime fim, Integer pessoas, String observacao) {

        Usuario usuario = usuarioBO.buscarPorId(usuarioId);
        Mesa mesa = mesaBO.buscarPorId(mesaId);

        // Regra 1: A mesa deve estar ativa
        if (!Boolean.TRUE.equals(mesa.getAtiva())) {
            throw new IllegalArgumentException("Esta mesa não está disponível para reservas.");
        }

        // Regra 2: Data de início deve ser no futuro
        if (inicio.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("A reserva deve ser para uma data futura.");
        }

        // Regra 3: Fim deve ser após o início
        if (fim.isBefore(inicio) || fim.isEqual(inicio)) {
            throw new IllegalArgumentException("O horário de fim deve ser após o início.");
        }

        // Regra 4: Capacidade da mesa
        if (pessoas > mesa.getCapacidade()) {
            throw new IllegalArgumentException(
                "A mesa " + mesa.getNumero() + " suporta no máximo " + mesa.getCapacidade() + " pessoas.");
        }

        // Regra 5: Conflito de horários
        if (reservaDAO.mesaOcupada(mesaId, inicio, fim)) {
            throw new IllegalArgumentException("A mesa já está ocupada ou reservada neste horário.");
        }

        // Cria a reserva
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setMesa(mesa);
        reserva.setDataHoraInicio(inicio);
        reserva.setDataHoraFim(fim);
        reserva.setNumeroPessoas(pessoas);
        reserva.setObservacao(observacao);
        reserva.setStatus(StatusReserva.PENDENTE);

        reservaDAO.persist(reserva);
        return reserva;
    }

    // Cancela reserva — UC-03
    @Transactional
    public Reserva cancelar(Long id, Long usuarioId, boolean isAdmin) {
        Reserva reserva = buscarPorId(id);

        // Regra: Cliente só cancela a própria reserva
        if (!isAdmin && !reserva.getUsuario().getId().equals(usuarioId)) {
            throw new IllegalArgumentException("Você não tem permissão para cancelar esta reserva.");
        }

        // Regra: Só cancela se não estiver concluida ou já cancelada
        if (reserva.getStatus() == StatusReserva.CONCLUIDA || reserva.getStatus() == StatusReserva.CANCELADA) {
            throw new IllegalArgumentException("Esta reserva não pode mais ser cancelada.");
        }

        reserva.setStatus(StatusReserva.CANCELADA);
        return reserva;
    }

    // Altera status (ADMIN)
    @Transactional
    public Reserva alterarStatus(Long id, StatusReserva novoStatus) {
        Reserva reserva = buscarPorId(id);
        reserva.setStatus(novoStatus);
        return reserva;
    }
}
