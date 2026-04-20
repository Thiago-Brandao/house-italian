package br.edu.ifg;

import br.edu.ifg.bo.UsuarioBO;
import br.edu.ifg.dao.UsuarioDAO;
import br.edu.ifg.model.Role;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class StartupData {

    @Inject
    UsuarioDAO usuarioDAO;

    @Inject
    UsuarioBO usuarioBO;

    @Transactional
    public void onStart(@Observes StartupEvent event) {

        // Cria admin padrão se não existir
        if (!usuarioDAO.existeEmail("admin@housitalian.com")) {
            usuarioBO.cadastrar(
                "Admin",
                "admin@housitalian.com",
                "admin123",
                Role.ADMIN
            );
            System.out.println(">>> Admin criado: admin@housitalian.com / admin123");
        }

        // Cria cliente de teste se não existir
        if (!usuarioDAO.existeEmail("thiago@email.com")) {
            usuarioBO.cadastrar(
                "Thiago",
                "thiago@email.com",
                "senha123",
                Role.CLIENTE
            );
            System.out.println(">>> Cliente criado: thiago@email.com / senha123");
        }
    }
}