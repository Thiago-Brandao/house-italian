package br.edu.ifg.dao;

import br.edu.ifg.model.Mesa;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class MesaDAO implements PanacheRepository<Mesa> {

    // Lista todas as mesas ativas
    public List<Mesa> listarTodasAtivas() {
        return list("ativa", true);
    }

    public boolean existeMesaNumero(Integer numero) {
        return count("numero", numero) > 0;
    }

    public boolean existeMesaNumeroeId(Integer numero, Long id) {
        return count("numero = ?1 and id != ?2", numero, id) > 0;
    }
}