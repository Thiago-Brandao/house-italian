package br.edu.ifg.controller;

import br.edu.ifg.model.bo.AuditoriaBO;
import br.edu.ifg.model.dto.LogAuditoriaResponseDTO;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/auditoria")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("ADMIN")
public class AuditoriaController {

    @Inject
    AuditoriaBO auditoriaBO;

    // GET /api/auditoria — lista todos os logs
    @GET
    public Response listarTodos() {
        List<LogAuditoriaResponseDTO> lista = auditoriaBO.listarTodos()
                .stream()
                .map(LogAuditoriaResponseDTO::de)
                .toList();
        return Response.ok(lista).build();
    }

    // GET /api/auditoria/usuario/{id} — logs de um usuário
    @GET
    @Path("/usuario/{id}")
    public Response listarPorUsuario(@PathParam("id") Long id) {
        List<LogAuditoriaResponseDTO> lista = auditoriaBO
                .listarPorUsuario(id)
                .stream()
                .map(LogAuditoriaResponseDTO::de)
                .toList();
        return Response.ok(lista).build();
    }
}