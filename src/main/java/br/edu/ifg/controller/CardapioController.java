package br.edu.ifg.controller;

import br.edu.ifg.model.dao.PratoDAO;
import br.edu.ifg.model.dto.PratoResponseDTO;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/cardapio")
@Produces(MediaType.APPLICATION_JSON)
public class CardapioController {

    @Inject
    PratoDAO pratoDAO;

    @GET
    public Response listar() {
        List<PratoResponseDTO> lista = pratoDAO.findAllDisponiveis()
                .stream()
                .map(PratoResponseDTO::de)
                .toList();
        return Response.ok(lista).build();
    }
}