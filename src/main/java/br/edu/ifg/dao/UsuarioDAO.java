package br.edu.ifg.dao;

import br.edu.ifg.model.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class UsuarioDAO implements PanacheRepository<Usuario> {

    // Buscar um usuário pelo email (usado no login)        // O panach já da métodos como o findAll() para facilitar a busca
    public Optional<Usuario> buscarPorEmail(String email) { // Optional retorna vazio, ao invez de null
        return find("email", email).firstResultOptional(); // Ele considera o Optional // O find cria uma query
    }

    // Verificação se o email já foi usado(usado no cadastro)
    public boolean existeEmail(String email) {
        return count("email", email) > 0;
    }
}