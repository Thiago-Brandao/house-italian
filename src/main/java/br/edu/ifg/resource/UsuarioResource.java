package br.edu.ifg.resource;

import br.edu.ifg.bo.UsuarioBO;
import br.edu.ifg.dto.UsuarioCadastroDTO;
import br.edu.ifg.dto.UsuarioResponseDTO;
import br.edu.ifg.model.Role;
import br.edu.ifg.model.Usuario;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import java.util.List;

@Path("/api/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {

    @Inject
    UsuarioBO usuarioBO;

    // O Quarkus injeta automaticamente o token do usuário logado
    @Inject
    JsonWebToken jwt;

    // POST /api/usuarios — PÚBLICO (auto-cadastro)
    @POST
    public Response cadastrar(UsuarioCadastroDTO dto) {
        try {
            Usuario usuario = usuarioBO.cadastrar(
                    dto.nome(),
                    dto.email(),
                    dto.senha(),
                    Role.CLIENTE
            );
            return Response
                    .status(Response.Status.CREATED)
                    .entity(UsuarioResponseDTO.de(usuario))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(new MensagemErro(e.getMessage()))
                    .build();
        }
    }

    // GET /api/usuarios — só ADMIN
    @GET
    @RolesAllowed("ADMIN")
    public Response listarTodos() {
        List<UsuarioResponseDTO> lista = usuarioBO.listarTodos()
                .stream()
                .map(UsuarioResponseDTO::de)
                .toList();
        return Response.ok(lista).build();
    }

    // GET /api/usuarios/{id} — só ADMIN
    @GET
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    public Response buscarPorId(@PathParam("id") Long id) {
        try {
            Usuario usuario = usuarioBO.buscarPorId(id);
            return Response.ok(UsuarioResponseDTO.de(usuario)).build();
        } catch (IllegalArgumentException e) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(new MensagemErro(e.getMessage()))
                    .build();
        }
    }

    // GET /api/usuarios/me — usuário logado vê os próprios dados
    @GET
    @Path("/me")
    @RolesAllowed({"ADMIN", "CLIENTE"})
    public Response meusDados() {
        try {
            // Pega o email do token JWT para buscar o usuário
            String email = jwt.getSubject();
            Usuario usuario = usuarioBO.buscarPorEmail(email);
            return Response.ok(UsuarioResponseDTO.de(usuario)).build();
        } catch (IllegalArgumentException e) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(new MensagemErro(e.getMessage()))
                    .build();
        }
    }

    // PUT /api/usuarios/{id}/status — só ADMIN
    @PUT
    @Path("/{id}/status")
    @RolesAllowed("ADMIN")
    public Response alterarStatus(
            @PathParam("id") Long id,
            @QueryParam("ativo") Boolean ativo) {
        try {
            Usuario usuario = usuarioBO.alterarStatus(id, ativo);
            return Response.ok(UsuarioResponseDTO.de(usuario)).build();
        } catch (IllegalArgumentException e) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(new MensagemErro(e.getMessage()))
                    .build();
        }
    }

    public record MensagemErro(String erro) {}
}