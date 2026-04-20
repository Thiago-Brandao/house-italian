package br.edu.ifg.dao;

import java.util.List;
import br.edu.ifg.model.Reserva;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

public class ReservaDAO implements PanacheRepository<Reserva> {

    // Lista todas as reservas
    public List<Reserva> listarTodasReservas(){
        return list("status");
    }
}
