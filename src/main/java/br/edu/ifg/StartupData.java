package br.edu.ifg;

import br.edu.ifg.bo.UsuarioBO;
import br.edu.ifg.dao.UsuarioDAO;
import br.edu.ifg.model.Prato;
import br.edu.ifg.model.Role;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
// Importa no topo do arquivo
import br.edu.ifg.bo.MesaBO;
import br.edu.ifg.dao.MesaDAO;
import br.edu.ifg.dao.PratoDAO;

@ApplicationScoped
public class StartupData {

    @Inject
    PratoDAO pratoDAO;

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

        // Pratos — adiciona no onStart após as mesas
        if (pratoDAO.count() == 0) {
            criarPrato("Lasagna alla Bolognese",
                "Camadas de massa fresca intercaladas com rico molho de carne (ragu), molho cremoso e queijo parmesão gratinado.",
                58.00);
            criarPrato("Fettuccine Alfredo",
                "Massa longa e achatada envolvida em uma emulsão aveludada de manteiga de alta qualidade e queijo parmesão tipo Grana Padano.",
                52.00);
            criarPrato("Gnocchi de Batata",
                "Pequenas almofadas de batata e farinha, servidas com molho de tomate caseiro e manjericão fresco, ou na opção ao molho quatro queijos.",
                48.00);
            criarPrato("Polpetone Recheado",
                "Grande almôndega de carne bovina recheada com queijo muçarela derretido, empanada e frita, servida sobre uma cama de espaguete ao sugo.",
                62.00);
            System.out.println(">>> Pratos criados!");
        }
    }
            // Método auxiliar — adiciona no final da classe
            private void criarPrato(String nome, String descricao, Double preco) {
            Prato p = new Prato();
            p.setNome(nome);
            p.setDescricao(descricao);
            p.setPreco(preco);
            p.setDisponivel(true);
            pratoDAO.persist(p);
}
    
}