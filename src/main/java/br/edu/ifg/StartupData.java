package br.edu.ifg;

import br.edu.ifg.model.bo.UsuarioBO;
import br.edu.ifg.model.dao.UsuarioDAO;
import br.edu.ifg.model.CategoriaPrato;
import br.edu.ifg.model.Prato;
import br.edu.ifg.model.Role;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
// Importa no topo do arquivo
import br.edu.ifg.model.bo.MesaBO;
import br.edu.ifg.model.dao.MesaDAO;
import br.edu.ifg.model.dao.PratoDAO;

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

        // Pratos
        if (pratoDAO.count() == 0) {
            // ENTRADAS
            criarPrato("Bruschetta Classica",
                "Fatias de pão italiano tostado com tomate fresco, alho, manjericão e azeite extravirgem.",
                32.00, CategoriaPrato.ENTRADAS);
            criarPrato("Caprese Salad",
                "Tomate, mozarela de búfala, manjericão fresco e azeite balsâmico.",
                38.00, CategoriaPrato.ENTRADAS);
            
            // MASSAS
            criarPrato("Lasagna alla Bolognese",
                "Camadas de massa fresca intercaladas com rico molho de carne (ragu), molho cremoso e queijo parmesão gratinado.",
                58.00, CategoriaPrato.MASSAS);
            criarPrato("Fettuccine Alfredo",
                "Massa longa e achatada envolvida em uma emulsão aveludada de manteiga de alta qualidade e queijo parmesão tipo Grana Padano.",
                52.00, CategoriaPrato.MASSAS);
            criarPrato("Gnocchi de Batata",
                "Pequenas almofadas de batata e farinha, servidas com molho de tomate caseiro e manjericão fresco, ou na opção ao molho quatro queijos.",
                48.00, CategoriaPrato.MASSAS);
            criarPrato("Spaghetti Carbonara",
                "Massa longa com molho de ovo, queijo parmesão, pancetta e pimenta preta moída na hora.",
                54.00, CategoriaPrato.MASSAS);
            
            // PRATOS PRINCIPAIS
            criarPrato("Polpetone Recheado",
                "Grande almôndega de carne bovina recheada com queijo muçarela derretido, empanada e frita, servida sobre uma cama de espaguete ao sugo.",
                62.00, CategoriaPrato.PRATOS_PRINCIPAIS);
            criarPrato("Osso Buco",
                "Carré de vitela cozido em vinho branco, legumes e ervas aromáticas, servido com risoto milanesa.",
                78.00, CategoriaPrato.PRATOS_PRINCIPAIS);
            
            // SOBREMESAS
            criarPrato("Tiramisu",
                "Camadas de biscoito savoiardi embebidos em café, creme de mascarpone e cacau polvilhado.",
                28.00, CategoriaPrato.SOBREMESAS);
            criarPrato("Panna Cotta",
                "Sobremesa cremosa de leite com calda de frutas vermelhas.",
                26.00, CategoriaPrato.SOBREMESAS);
            criarPrato("Cannoli Siciliani",
                "Massa crocante recheada com ricota doce e chocolate.",
                30.00, CategoriaPrato.SOBREMESAS);
            
            System.out.println(">>> Pratos criados!");
        }
    }
            // Método auxiliar
            private void criarPrato(String nome, String descricao, Double preco, CategoriaPrato categoria) {
            Prato p = new Prato();
            p.setNome(nome);
            p.setDescricao(descricao);
            p.setPreco(preco);
            p.setDisponivel(true);
            p.setCategoria(categoria);
            pratoDAO.persist(p);
}
    
}