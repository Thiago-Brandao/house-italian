package br.edu.ifg.controller;

import br.edu.ifg.model.bo.AuditoriaBO;
import br.edu.ifg.model.dao.UsuarioDAO;
import br.edu.ifg.model.dto.LoginRequestDTO;
import br.edu.ifg.model.dto.LoginResponseDTO;
import br.edu.ifg.model.Usuario;
import br.edu.ifg.security.JwtService;
import br.edu.ifg.utils.AcoesLog;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import java.util.Optional;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {

    @Inject
    AuditoriaBO auditoriaBO;

    @Inject
    UsuarioDAO usuarioDAO;

    @Inject
    JwtService jwtService;

    @POST
    @Path("/login")
    @Transactional
    public Response login(@Valid LoginRequestDTO dto) {
        if (dto == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensagemErro("Corpo da requisição inválido."))
                    .build();
        }

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
        if (!BcryptUtil.matches(dto.senha(), usuario.getSenha()))  {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new MensagemErro("E-mail ou senha inválidos."))
                    .build();
        }

        // Gera o token JWT
        String token = jwtService.gerarToken(usuario);

        auditoriaBO.registrar(
        AcoesLog.LOGIN,
        "Usuario",
        usuario.getId(),
        usuario,
        "Login realizado"
        );

        // Cria cookie com o token JWT
        NewCookie authCookie = new NewCookie.Builder("auth_token")
                .value(token)
                .path("/")
                .httpOnly(false) // Permite acesso via JavaScript (para compatibilidade com o frontend existente)
                .maxAge(60 * 60 * 24 * 7) // Expira em 7 dias
                .build();

        return Response.ok(new LoginResponseDTO(
                token,
                "Bearer",
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil()
        ))
        .cookie(authCookie) // Adiciona o cookie na resposta
        .build();

    }

    @POST
    @Path("/logout")
    public Response logout() {
        // Cria cookie vazio e expirado para limpar
        NewCookie clearCookie = new NewCookie.Builder("auth_token")
                .value("")
                .path("/")
                .maxAge(0) // Expira imediatamente
                .build();

        return Response.ok()
                .cookie(clearCookie)
                .build();
    }

    public record MensagemErro(String erro) {}
}