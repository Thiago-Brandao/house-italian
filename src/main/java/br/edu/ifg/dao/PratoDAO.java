package br.edu.ifg.dao;

import br.edu.ifg.model.Prato;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class PratoDAO implements PanacheRepository<Prato> {

    public List<Prato> findAllDisponiveis() {
        return list("disponivel", true);
    }
}