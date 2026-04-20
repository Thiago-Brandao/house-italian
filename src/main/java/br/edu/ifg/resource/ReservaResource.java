package br.edu.ifg.resource;

import br.edu.ifg.bo.ReservaBO;
import br.edu.ifg.bo.UsuarioBO;
import br.edu.ifg.dto.ReservaRequestDTO;
import br.edu.ifg.dto.ReservaResponseDTO;
import br.edu.ifg.model.Reserva;
import br.edu.ifg.model.Usuario;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import java.util.List;

@Path("/api/reservas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservaResource {

    @Inject
    ReservaBO reservaBO;

    @Inject
    UsuarioBO usuarioBO;

    @Inject
    JsonWebToken jwt;

    // POST /api/reservas — UC-01 (CLIENTE)
    @POST
    @RolesAllowed({"CLIENTE", "ADMIN"})
    public Response criar(ReservaRequestDTO dto) {
        try {
            // Pega o ID do usuário logado pelo token JWT
            String email = jwt.getSubject();
            Usuario usuario = usuarioBO.buscarPorEmail(email);

            Reserva reserva = reservaBO.criar(
                    usuario.getId(),
                    dto.mesaId(),
                    dto.dataHoraInicio(),
                    dto.dataHoraFim(),
                    dto.numeroPessoas(),
                    dto.observacao()
            );
            return Response.status(Response.Status.CREATED)
                    .entity(ReservaResponseDTO.de(reserva))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensagemErro(e.getMessage()))
                    .build();
        }
    }

    // GET /api/reservas/minhas — UC-01 (CLIENTE vê só as suas)
    @GET
    @Path("/minhas")
    @RolesAllowed({"CLIENTE", "ADMIN"})
    public Response minhas() {
        String email = jwt.getSubject();
        Usuario usuario = usuarioBO.buscarPorEmail(email);

        List<ReservaResponseDTO> lista = reservaBO
                .listarMinhas(usuario.getId())
                .stream()
                .map(ReservaResponseDTO::de)
                .toList();

        return Response.ok(lista).build();
    }

    // GET /api/reservas — lista todas (ADMIN)
    @GET
    @RolesAllowed("ADMIN")
    public Response listarTodas() {
        List<ReservaResponseDTO> lista = reservaBO.listarTodas()
                .stream()
                .map(ReservaResponseDTO::de)
                .toList();
        return Response.ok(lista).build();
    }

    // GET /api/reservas/{id}
    @GET
    @Path("/{id}")
    @RolesAllowed({"CLIENTE", "ADMIN"})
    public Response buscarPorId(@PathParam("id") Long id) {
        try {
            Reserva reserva = reservaBO.buscarPorId(id);
            return Response.ok(ReservaResponseDTO.de(reserva)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new MensagemErro(e.getMessage()))
                    .build();
        }
    }

    // PUT /api/reservas/{id}/cancelar — UC-01 (CLIENTE)
    @PUT
    @Path("/{id}/cancelar")
    @RolesAllowed({"CLIENTE", "ADMIN"})
    public Response cancelar(@PathParam("id") Long id) {
        try {
            String email = jwt.getSubject();
            Usuario usuario = usuarioBO.buscarPorEmail(email);

            Reserva reserva = reservaBO.cancelar(id, usuario.getId());
            return Response.ok(ReservaResponseDTO.de(reserva)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensagemErro(e.getMessage()))
                    .build();
        }
    }

    // PUT /api/reservas/{id}/confirmar — ADMIN
    @PUT
    @Path("/{id}/confirmar")
    @RolesAllowed("ADMIN")
    public Response confirmar(@PathParam("id") Long id) {
        try {
            Reserva reserva = reservaBO.confirmar(id);
            return Response.ok(ReservaResponseDTO.de(reserva)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensagemErro(e.getMessage()))
                    .build();
        }
    }

    // PUT /api/reservas/{id}/concluir — ADMIN
    @PUT
    @Path("/{id}/concluir")
    @RolesAllowed("ADMIN")
    public Response concluir(@PathParam("id") Long id) {
        try {
            Reserva reserva = reservaBO.concluir(id);
            return Response.ok(ReservaResponseDTO.de(reserva)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensagemErro(e.getMessage()))
                    .build();
        }
    }

    public record MensagemErro(String erro) {}
}