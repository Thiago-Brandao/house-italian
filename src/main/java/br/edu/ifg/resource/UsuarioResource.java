package br.edu.ifg.resource;

import br.edu.ifg.bo.UsuarioBO;
import br.edu.ifg.dto.UsuarioCadastroDTO;
import br.edu.ifg.dto.UsuarioResponseDTO;
import br.edu.ifg.model.Role;
import br.edu.ifg.model.Usuario;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/usuarios") // Define a url base do endpoint
@Produces(MediaType.APPLICATION_JSON) // Indica a entrada
@Consumes(MediaType.APPLICATION_JSON) // Indica a saida
public class UsuarioResource {

    @Inject // injeta a instância criada em UsuarioBO
    UsuarioBO usuarioBO;

    // POST /api/usuarios — cadastra novo usuário
    @POST
    public Response cadastrar(UsuarioCadastroDTO dto) {
        try {
            Usuario usuario = usuarioBO.cadastrar(
                    dto.nome(),
                    dto.email(),
                    dto.senha(),
                    Role.CLIENTE  // auto-cadastro sempre cria como CLIENTE
            );
            return Response
                    .status(Response.Status.CREATED)       // HTTP 201
                    .entity(UsuarioResponseDTO.de(usuario))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response
                    .status(Response.Status.BAD_REQUEST)   // HTTP 400
                    .entity(new MensagemErro(e.getMessage()))
                    .build();
        }
    }

    // GET /api/usuarios — lista todos (só ADMIN vai poder chamar, por enquanto aberto)
    @GET
    public Response listarTodos() {
        List<UsuarioResponseDTO> lista = usuarioBO.listarTodos()
                .stream()
                .map(UsuarioResponseDTO::de)
                .toList();
        return Response.ok(lista).build();  // HTTP 200
    }

    // GET /api/usuarios/{id} — busca por ID
    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        try {
            Usuario usuario = usuarioBO.buscarPorId(id);
            return Response.ok(UsuarioResponseDTO.de(usuario)).build();
        } catch (IllegalArgumentException e) {
            return Response
                    .status(Response.Status.NOT_FOUND)     // HTTP 404
                    .entity(new MensagemErro(e.getMessage()))
                    .build();
        }
    }

    // PUT /api/usuarios/{id}/status — ativa ou inativa usuário
    @PUT
    @Path("/{id}/status")
    public Response alterarStatus(@PathParam("id") Long id, @QueryParam("ativo") Boolean ativo) {
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

    // Classe interna para padronizar mensagens de erro no JSON
    public record MensagemErro(String erro) {}
}