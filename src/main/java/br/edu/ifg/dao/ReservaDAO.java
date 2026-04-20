package br.edu.ifg.dao;

import br.edu.ifg.model.Reserva;
import br.edu.ifg.model.StatusReserva;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class ReservaDAO implements PanacheRepository<Reserva> {

    // Busca reservas de um usuário específico
    public List<Reserva> findByUsuarioId(Long usuarioId) {
        return list("usuario.id", usuarioId);
    }

    // Verifica conflito de horário em uma mesa
    // Uma mesa tem conflito se já existe reserva PENDENTE ou CONFIRMADA
    // que se sobrepõe ao intervalo solicitado
    public boolean existeConflito(Long mesaId,
                                   LocalDateTime inicio,
                                   LocalDateTime fim,
                                   Long reservaIdIgnorar) {
        if (reservaIdIgnorar != null) {
            return count("""
                mesa.id = ?1
                and id != ?2
                and status in (?3, ?4)
                and dataHoraInicio < ?6
                and dataHoraFim > ?5
                """,
                mesaId, reservaIdIgnorar,
                StatusReserva.PENDENTE, StatusReserva.CONFIRMADA,
                inicio, fim) > 0;
        }
        return count("""
                mesa.id = ?1
                and status in (?2, ?3)
                and dataHoraInicio < ?5
                and dataHoraFim > ?4
                """,
                mesaId,
                StatusReserva.PENDENTE, StatusReserva.CONFIRMADA,
                inicio, fim) > 0;
    }

    // Lista reservas por status
    public List<Reserva> buscarPorStatus(StatusReserva status) {
        return list("status", status);
    }
}