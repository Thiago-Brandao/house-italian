package br.edu.ifg.resource;

import br.edu.ifg.bo.ReservaBO;
import br.edu.ifg.bo.UsuarioBO;
import br.edu.ifg.dto.ReservaRequestDTO;
import br.edu.ifg.dto.ReservaResponseDTO;
import br.edu.ifg.model.Reserva;
import br.edu.ifg.model.StatusReserva;
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

    // GET /api/reservas — ADMIN vê todas, CLIENTE só as próprias
    @GET
    @RolesAllowed({"ADMIN", "CLIENTE"})
    public Response listar() {
        String email = jwt.getSubject();
        Usuario usuario = usuarioBO.buscarPorEmail(email);
        
        List<ReservaResponseDTO> lista;
        if (jwt.getGroups().contains("ADMIN")) {
            lista = reservaBO.listarTodas()
                    .stream()
                    .map(ReservaResponseDTO::de)
                    .toList();
        } else {
            lista = reservaBO.listarPorUsuario(usuario.getId())
                    .stream()
                    .map(ReservaResponseDTO::de)
                    .toList();
        }

        return Response.ok(lista).build();
    }

    // GET /api/reservas/{id}
    @GET
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "CLIENTE"})
    public Response buscarPorId(@PathParam("id") Long id) {
        try {
            Reserva reserva = reservaBO.buscarPorId(id);
            // Verifica se o cliente está tentando ver a reserva de outro
            if (!jwt.getGroups().contains("ADMIN")) {
                String email = jwt.getSubject();
                Usuario usuario = usuarioBO.buscarPorEmail(email);
                if (!reserva.getUsuario().getId().equals(usuario.getId())) {
                    return Response.status(Response.Status.FORBIDDEN)
                            .entity(new MensagemErro("Você não tem permissão para visualizar esta reserva."))
                            .build();
                }
            }
            return Response.ok(ReservaResponseDTO.de(reserva)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new MensagemErro(e.getMessage()))
                    .build();
        }
    }

    // POST /api/reservas — UC-03
    @POST
    @RolesAllowed({"ADMIN", "CLIENTE"})
    public Response criar(ReservaRequestDTO dto) {
        try {
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

    // PUT /api/reservas/{id}/cancelar — UC-03
    @PUT
    @Path("/{id}/cancelar")
    @RolesAllowed({"ADMIN", "CLIENTE"})
    public Response cancelar(@PathParam("id") Long id) {
        try {
            String email = jwt.getSubject();
            Usuario usuario = usuarioBO.buscarPorEmail(email);
            boolean isAdmin = jwt.getGroups().contains("ADMIN");
            
            Reserva reserva = reservaBO.cancelar(id, usuario.getId(), isAdmin);
            return Response.ok(ReservaResponseDTO.de(reserva)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensagemErro(e.getMessage()))
                    .build();
        }
    }

    // PUT /api/reservas/{id}/status — só ADMIN
    @PUT
    @Path("/{id}/status")
    @RolesAllowed("ADMIN")
    public Response alterarStatus(
            @PathParam("id") Long id,
            @QueryParam("status") StatusReserva status) {
        try {
            Reserva reserva = reservaBO.alterarStatus(id, status);
            return Response.ok(ReservaResponseDTO.de(reserva)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensagemErro(e.getMessage()))
                    .build();
        }
    }

    public record MensagemErro(String erro) {}
}
