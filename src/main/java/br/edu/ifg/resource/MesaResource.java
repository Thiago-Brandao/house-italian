package br.edu.ifg.resource;

import br.edu.ifg.bo.MesaBO;
import br.edu.ifg.dto.MesaRequestDTO;
import br.edu.ifg.dto.MesaResponseDTO;
import br.edu.ifg.model.Mesa;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/mesas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MesaResource {

    @Inject
    MesaBO mesaBO;

    // GET /api/mesas — ADMIN vê todas, CLIENTE só as ativas
    @GET
    @RolesAllowed({"ADMIN", "CLIENTE"})
    public Response listar(@QueryParam("todas") Boolean todas) {
        List<MesaResponseDTO> lista;

        if (Boolean.TRUE.equals(todas)) {
            lista = mesaBO.listarTodas()
                    .stream()
                    .map(MesaResponseDTO::de)
                    .toList();
        } else {
            lista = mesaBO.listarAtivas()
                    .stream()
                    .map(MesaResponseDTO::de)
                    .toList();
        }

        return Response.ok(lista).build();
    }

    // GET /api/mesas/{id}
    @GET
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "CLIENTE"})
    public Response buscarPorId(@PathParam("id") Long id) {
        try {
            Mesa mesa = mesaBO.buscarPorId(id);
            return Response.ok(MesaResponseDTO.de(mesa)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new MensagemErro(e.getMessage()))
                    .build();
        }
    }

    // POST /api/mesas — só ADMIN (UC-02)
    @POST
    @RolesAllowed("ADMIN")
    public Response criar(MesaRequestDTO dto) {
        try {
            Mesa mesa = mesaBO.criar(
                    dto.numero(),
                    dto.capacidade(),
                    dto.localizacao(),
                    dto.descricao()
            );
            return Response.status(Response.Status.CREATED)
                    .entity(MesaResponseDTO.de(mesa))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensagemErro(e.getMessage()))
                    .build();
        }
    }

    // PUT /api/mesas/{id} — só ADMIN (UC-02)
    @PUT
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    public Response editar(@PathParam("id") Long id, MesaRequestDTO dto) {
        try {
            Mesa mesa = mesaBO.editar(
                    id,
                    dto.numero(),
                    dto.capacidade(),
                    dto.localizacao(),
                    dto.descricao()
            );
            return Response.ok(MesaResponseDTO.de(mesa)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensagemErro(e.getMessage()))
                    .build();
        }
    }

    // PUT /api/mesas/{id}/status — só ADMIN (UC-02)
    @PUT
    @Path("/{id}/status")
    @RolesAllowed("ADMIN")
    public Response alterarStatus(
            @PathParam("id") Long id,
            @QueryParam("ativa") Boolean ativa) {
        try {
            Mesa mesa = mesaBO.alterarStatus(id, ativa);
            return Response.ok(MesaResponseDTO.de(mesa)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new MensagemErro(e.getMessage()))
                    .build();
        }
    }

    public record MensagemErro(String erro) {}
}