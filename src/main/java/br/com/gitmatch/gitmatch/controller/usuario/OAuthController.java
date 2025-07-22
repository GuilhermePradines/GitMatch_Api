package br.com.gitmatch.gitmatch.controller.usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import br.com.gitmatch.gitmatch.dto.usuario.GitHubAuthRequest;
import br.com.gitmatch.gitmatch.dto.usuario.UsuarioResponseDTO;
import br.com.gitmatch.gitmatch.enums.TipoUsuario;
import br.com.gitmatch.gitmatch.model.usuario.Usuario;
import br.com.gitmatch.gitmatch.repository.usuario.UsuarioRepository;
import br.com.gitmatch.gitmatch.security.JwtUtil;

import java.util.Optional;

@RestController
@RequestMapping("/api/oauth")
public class OAuthController {

    private final UsuarioRepository usuarioRepo;
    private final JwtUtil jwtUtil;

    @Autowired
    public OAuthController(UsuarioRepository usuarioRepo, JwtUtil jwtUtil) {
        this.usuarioRepo = usuarioRepo;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/github")
    public UsuarioResponseDTO githubAuth(@RequestBody GitHubAuthRequest request) {
        // Garante email não nulo
        String email = request.getEmail() != null ? 
            request.getEmail() : 
            request.getGithubUsername() + "@github.com";

        // Busca por Firebase UID (mais confiável que email)
        Optional<Usuario> usuarioExistente = usuarioRepo.findByFirebaseUid(request.getFirebaseUid());

        Usuario usuario = usuarioExistente.orElseGet(() -> {
            Usuario novo = new Usuario();
            novo.setFirebaseUid(request.getFirebaseUid());
            novo.setNome(request.getNome());
            novo.setEmail(email);
            novo.setGithubUsername(request.getGithubUsername());
            novo.setTipoUsuario(TipoUsuario.CANDIDATO);
            novo.setSenhaHash("oauth_github");
            novo.setTermosAceitos(true);
            return usuarioRepo.save(novo);
        });

        String token = jwtUtil.generateToken(usuario.getEmail());

        return new UsuarioResponseDTO(
            usuario.getIdUsuario(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getTipoUsuario().name(),
            token
        );
    }
}