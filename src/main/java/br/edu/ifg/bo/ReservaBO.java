package br.edu.ifg.bo;

import br.edu.ifg.dao.MesaDAO;
import br.edu.ifg.dao.ReservaDAO;
import br.edu.ifg.dao.UsuarioDAO;
import br.edu.ifg.model.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@ApplicationScoped
public class ReservaBO {

    @Inject
    ReservaDAO reservaDAO;

    @Inject
    MesaDAO mesaDAO;

    @Inject
    UsuarioDAO usuarioDAO;

    // UC-01: Realizar reserva (CLIENTE)
    @Transactional
    public Reserva criar(Long usuarioId, Long mesaId,
                         LocalDateTime inicio, LocalDateTime fim,
                         Integer numeroPessoas, String observacao) {

        // Busca mesa e usuário
        Mesa mesa = mesaDAO.findById(mesaId);
        if (mesa == null) {
            throw new IllegalArgumentException("Mesa não encontrada.");
        }

        Usuario usuario = usuarioDAO.findById(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }

        // Regra 1: mesa deve estar ativa
        if (!mesa.getAtiva()) {
            throw new IllegalArgumentException(
                "Esta mesa não está disponível para reservas.");
        }

        // Regra 2: data de início deve ser futura
        if (inicio.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                "A reserva deve ser feita para uma data futura.");
        }

        // Regra 3: antecedência mínima de 1 hora
        long minutosAteReserva = ChronoUnit.MINUTES.between(
            LocalDateTime.now(), inicio);
        if (minutosAteReserva < 60) {
            throw new IllegalArgumentException(
                "A reserva deve ser feita com pelo menos 1 hora de antecedência.");
        }

        // Regra 4: fim deve ser depois do início
        if (!fim.isAfter(inicio)) {
            throw new IllegalArgumentException(
                "O horário de término deve ser depois do início.");
        }

        // Regra 5: duração mínima 30 min, máxima 4 horas
        long duracaoMinutos = ChronoUnit.MINUTES.between(inicio, fim);
        if (duracaoMinutos < 30) {
            throw new IllegalArgumentException(
                "A reserva deve ter no mínimo 30 minutos de duração.");
        }
        if (duracaoMinutos > 240) {
            throw new IllegalArgumentException(
                "A reserva deve ter no máximo 4 horas de duração.");
        }

        // Regra 6: número de pessoas não pode exceder capacidade
        if (numeroPessoas > mesa.getCapacidade()) {
            throw new IllegalArgumentException(
                "Número de pessoas (" + numeroPessoas +
                ") excede a capacidade da mesa (" +
                mesa.getCapacidade() + ").");
        }

        if (numeroPessoas < 1) {
            throw new IllegalArgumentException(
                "Número de pessoas deve ser pelo menos 1.");
        }

        // Regra 7: verificar conflito de horário
        if (reservaDAO.existeConflito(mesaId, inicio, fim, null)) {
            throw new IllegalArgumentException(
                "Esta mesa já possui uma reserva neste horário.");
        }

        // Cria a reserva
        Reserva reserva = new Reserva();
        reserva.setMesa(mesa);
        reserva.setUsuario(usuario);
        reserva.setDataHoraInicio(inicio);
        reserva.setDataHoraFim(fim);
        reserva.setNumeroPessoas(numeroPessoas);
        reserva.setObservacao(observacao);
        reserva.setStatus(StatusReserva.PENDENTE);

        reservaDAO.persist(reserva);
        return reserva;
    }

    // Lista reservas do próprio usuário (CLIENTE)
    public List<Reserva> listarMinhas(Long usuarioId) {
        return reservaDAO.findByUsuarioId(usuarioId);
    }

    // Lista todas as reservas (ADMIN)
    public List<Reserva> listarTodas() {
        return reservaDAO.listAll();
    }

    // Busca por ID
    public Reserva buscarPorId(Long id) {
        Reserva reserva = reservaDAO.findById(id);
        if (reserva == null) {
            throw new IllegalArgumentException("Reserva não encontrada.");
        }
        return reserva;
    }

    // UC-01: Cancelar reserva (CLIENTE)
    @Transactional
    public Reserva cancelar(Long reservaId, Long usuarioId) {
        Reserva reserva = buscarPorId(reservaId);

        // Regra: só pode cancelar a própria reserva
        if (!reserva.getUsuario().getId().equals(usuarioId)) {
            throw new IllegalArgumentException(
                "Você só pode cancelar suas próprias reservas.");
        }

        // Regra: só pode cancelar PENDENTE ou CONFIRMADA
        if (reserva.getStatus() == StatusReserva.CANCELADA ||
            reserva.getStatus() == StatusReserva.CONCLUIDA) {
            throw new IllegalArgumentException(
                "Esta reserva não pode ser cancelada.");
        }

        // Regra: cancelamento com pelo menos 2 horas de antecedência
        long minutosAteReserva = ChronoUnit.MINUTES.between(
            LocalDateTime.now(), reserva.getDataHoraInicio());
        if (minutosAteReserva < 120) {
            throw new IllegalArgumentException(
                "O cancelamento deve ser feito com pelo menos " +
                "2 horas de antecedência.");
        }

        reserva.setStatus(StatusReserva.CANCELADA);
        return reserva;
    }

    // Confirmar reserva (ADMIN)
    @Transactional
    public Reserva confirmar(Long reservaId) {
        Reserva reserva = buscarPorId(reservaId);
        if (reserva.getStatus() != StatusReserva.PENDENTE) {
            throw new IllegalArgumentException(
                "Só é possível confirmar reservas com status PENDENTE.");
        }
        reserva.setStatus(StatusReserva.CONFIRMADA);
        return reserva;
    }

    // Concluir reserva (ADMIN)
    @Transactional
    public Reserva concluir(Long reservaId) {
        Reserva reserva = buscarPorId(reservaId);
        if (reserva.getStatus() != StatusReserva.CONFIRMADA) {
            throw new IllegalArgumentException(
                "Só é possível concluir reservas com status CONFIRMADA.");
        }
        reserva.setStatus(StatusReserva.CONCLUIDA);
        return reserva;
    }
}