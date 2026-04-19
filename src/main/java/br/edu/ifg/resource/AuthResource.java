package br.edu.ifg.resource;

import br.edu.ifg.dao.UsuarioDAO;
import br.edu.ifg.dto.LoginRequestDTO;
import br.edu.ifg.dto.LoginResponseDTO;
import br.edu.ifg.model.Usuario;
import br.edu.ifg.security.JwtService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Optional;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    UsuarioDAO usuarioDAO;

    @Inject
    JwtService jwtService;

    @POST
    @Path("/login")
    @Transactional
    public Response login(LoginRequestDTO dto) {

        // Busca usuário pelo email
        Optional<Usuario> optional = usuarioDAO.buscarPorEmail(dto.email());

        if (optional.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new MensagemErro("E-mail ou senha inválidos."))
                    .build();
        }

        Usuario usuario = optional.get();

        // Verifica se o usuário está ativo
        if (!usuario.getAtivo()) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new MensagemErro("Usuário inativo."))
                    .build();
        }

        // Verifica a senha 
        if (!dto.senha().equals(usuario.getSenha())) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new MensagemErro("E-mail ou senha inválidos."))
                    .build();
        }

        // Gera o token JWT
        String token = jwtService.gerarToken(usuario);

        return Response.ok(new LoginResponseDTO(
                token,
                "Bearer",
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil()
        )).build();
    }

    public record MensagemErro(String erro) {}
}