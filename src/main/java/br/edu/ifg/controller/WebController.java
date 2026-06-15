package br.edu.ifg.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/")
public class WebController {

    @GET
    @Path("/test")
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        return "Hello from WebController!";
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String home() throws IOException {
        return readHtmlFile("templates/index.html");
    }

    @GET
    @Path("/login")
    @Produces(MediaType.TEXT_HTML)
    public String loginPage() throws IOException {
        return readHtmlFile("templates/login.html");
    }

    @GET
    @Path("/cadastro")
    @Produces(MediaType.TEXT_HTML)
    public String cadastroPage() throws IOException {
        return readHtmlFile("templates/cadastro.html");
    }

    @GET
    @Path("/cardapio")
    @Produces(MediaType.TEXT_HTML)
    public String cardapioPage() throws IOException {
        return readHtmlFile("templates/cardapio.html");
    }

    @GET
    @Path("/reserva")
    @Produces(MediaType.TEXT_HTML)
    public String reservaPage() throws IOException {
        return readHtmlFile("templates/reserva.html");
    }

    @GET
    @Path("/dashboard")
    @Produces(MediaType.TEXT_HTML)
    public String dashboardPage() throws IOException {
        return readHtmlFile("templates/dashboard.html");
    }

    private String readHtmlFile(String filePath) throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/" + filePath)) {
            if (is == null) {
                return "<h1>Arquivo não encontrado: /" + filePath + "</h1>";
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
