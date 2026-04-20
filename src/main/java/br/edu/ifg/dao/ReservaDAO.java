package br.edu.ifg.dao;

import br.edu.ifg.model.Reserva;
import br.edu.ifg.model.StatusReserva;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ReservaDAO implements PanacheRepository<Reserva> {

    // Lista todas as reservas ordenadas por data de criação
    public List<Reserva> listarTodas() {
        return listAll();
    }

    // Lista reservas por status
    public List<Reserva> listarPorStatus(StatusReserva status) {
        return list("status", status);
    }

    // Lista reservas de um usuário
    public List<Reserva> listarPorUsuario(Long usuarioId) {
        return list("usuario.id", usuarioId);
    }

    // Verifica se a mesa já está reservada no período
    public boolean mesaOcupada(Long mesaId, java.time.LocalDateTime inicio, java.time.LocalDateTime fim) {
        String query = "mesa.id = ?1 and status != ?2 and dataHoraInicio < ?3 and dataHoraFim > ?4";
        return count(query, mesaId, StatusReserva.CANCELADA, fim, inicio) > 0;
    }
}
