package br.edu.ifg.model.bo;

import br.edu.ifg.model.dao.UsuarioDAO;
import br.edu.ifg.model.Role;
import br.edu.ifg.model.Usuario;
import br.edu.ifg.utils.AcoesLog;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;


@ApplicationScoped
public class UsuarioBO {

    @Inject // injeta a instância criada em UsuarioDAO
    UsuarioDAO usuarioDAO;

    // Adiciona o inject no topo da classe
    @Inject
    AuditoriaBO auditoriaBO;

    // Cadastra um novo usuário (chamado pelo endpoint de cadastro)
    @Transactional // Garante que o tudo no método aconteça em uma unica transação
    // com o banco.Se algo der errado, o banco faz rollback e nada fica pela metade

    public Usuario cadastrar(String nome, String email, String senha, Role perfil) {

        // Regra 1: O email não pode já estar cadastrado
        if (usuarioDAO.existeEmail(email)) {
            throw new IllegalArgumentException("E-mail já cadastrado!");
        }

        // Regra 2: A senha deve ter no mínimo 8 caracteres
        if (senha == null || senha.length() < 8) {
            throw new IllegalArgumentException("A senha deve ter no mínimo 8 caracteres!");
        }

        // Regra 3: O nome não pode ser vazio
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome é obrigatório.");
            // IllegalArgumentException o HTTP vai capturar isso e devolver um erro 400
        }

        // Cria o usuario e salva
        Usuario usuario = new Usuario();
        usuario.setNome(nome.trim());
        usuario.setEmail(email.toLowerCase().trim()); // O email.toLowerCase() padroniza o email
        usuario.setSenha(BcryptUtil.bcryptHash(senha));
        usuario.setPerfil(perfil != null ? perfil : Role.CLIENTE);
        usuario.setAtivo(true);

        usuarioDAO.persist(usuario);

        
        auditoriaBO.registrar(
        AcoesLog.CADASTRO_USUARIO,
        "Usuario",
        usuario.getId(),
        usuario,
         "Email: " + usuario.getEmail()
        );

        return usuario;
    }

    // Lista todos os usuários (só ADMIN pode chamar esse método)
    public List<Usuario> listarTodos() {
        return usuarioDAO.listAll();
    }

    // Busca usuário por ID
    public Usuario buscarPorId(Long id) {
        Usuario usuario = usuarioDAO.findById(id);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado!");
        }
        return usuario;
    }

    // Ativa ou inativa um usuário
    @Transactional
    public Usuario alterarStatus(Long id, Boolean ativo) {
        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(ativo);

        auditoriaBO.registrar(
        AcoesLog.CADASTRO_USUARIO,
        "Usuario",
        usuario.getId(),
        usuario,
        "Status alterado: " + ativo
        );
        return usuario;
    }

    // Busca usuário pelo email (usado no /me)
    public Usuario buscarPorEmail(String email) {
        return usuarioDAO.buscarPorEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
    }


}