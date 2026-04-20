package br.edu.ifg;

import br.edu.ifg.bo.UsuarioBO;
import br.edu.ifg.dao.UsuarioDAO;
import br.edu.ifg.model.Role;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
// Importa no topo do arquivo
import br.edu.ifg.bo.MesaBO;
import br.edu.ifg.dao.MesaDAO;

@ApplicationScoped
public class StartupData {

    @Inject
    UsuarioDAO usuarioDAO;

    @Inject
    UsuarioBO usuarioBO;

    // Injeta no corpo da classe
    @Inject
    MesaBO mesaBO;

    @Inject
    MesaDAO mesaDAO;

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

            // Mesas
        if (!mesaDAO.existeMesaNumero(1)) {
            mesaBO.criar(1, 4, "Salao Principal", "Mesa proxima a janela");
            mesaBO.criar(2, 6, "Varanda", "Mesa ao ar livre");
            mesaBO.criar(3, 2, "VIP", "Mesa para ocasioes especiais");
            System.out.println(">>> Mesas criadas!");
        
        }
    }
}