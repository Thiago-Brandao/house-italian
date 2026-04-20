package br.edu.ifg.security;

import br.edu.ifg.model.Usuario;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.util.Set;

@ApplicationScoped
public class JwtService {

    public String gerarToken(Usuario usuario) {
        return Jwt.issuer("house-italian")
                .subject(usuario.getEmail()) // Identifica o usuário no token — usando o email
                .groups(Set.of(usuario.getPerfil().name())) // Os perfis do usuário
                .claim("id", usuario.getId())
                .claim("nome", usuario.getNome())
                .expiresIn(Duration.ofHours(1))
                .sign();
    }
}