package br.edu.ifg.resource;

import br.edu.ifg.dao.PratoDAO;
import br.edu.ifg.dto.PratoResponseDTO;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/cardapio")
@Produces(MediaType.APPLICATION_JSON)
public class CardapioResource {

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