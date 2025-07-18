package br.com.gitmatch.gitmatch.controller.usuario;

import br.com.gitmatch.gitmatch.dto.usuario.GitHubUserDTO;
import br.com.gitmatch.gitmatch.dto.usuario.UsuarioResponseDTO;
import br.com.gitmatch.gitmatch.enums.TipoUsuario;
import br.com.gitmatch.gitmatch.model.usuario.Usuario;
import br.com.gitmatch.gitmatch.repository.usuario.UsuarioRepository;
import br.com.gitmatch.gitmatch.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/oauth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class OAuthController {

    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil; // ✅ Injetando JwtUtil

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.redirect.uri}")
    private String redirectUri;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/github/callback")
    public ResponseEntity<?> githubCallback(@RequestParam("code") String code) {
        System.out.println(">> [DEBUG] Callback chegou com code: " + code);

        // 1. Trocar o code por um access token
        String url = "https://github.com/login/oauth/access_token";
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        String accessToken = (String) response.getBody().get("access_token");

        // 2. Buscar dados do usuário com o token
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setBearerAuth(accessToken);
        authHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Void> profileRequest = new HttpEntity<>(authHeaders);

        ResponseEntity<GitHubUserDTO> profileResponse = restTemplate.exchange(
            "https://api.github.com/user",
            HttpMethod.GET,
            profileRequest,
            GitHubUserDTO.class
        );

        GitHubUserDTO githubUser = profileResponse.getBody();

        // 3. Buscar ou criar usuário no banco
        Optional<Usuario> optionalUser = usuarioRepository.findByGithubUsername(githubUser.getLogin());

        Usuario usuario = optionalUser.orElseGet(() -> {
            Usuario novo = new Usuario();
            novo.setNome(githubUser.getName() != null ? githubUser.getName() : githubUser.getLogin());
            novo.setGithubUsername(githubUser.getLogin());
            novo.setEmail(
                    githubUser.getEmail() != null ? githubUser.getEmail() : githubUser.getLogin() + "@github.com");
            novo.setTipoUsuario(TipoUsuario.CANDIDATO);
            novo.setSenhaHash("oauth_github"); 
            novo.setCriadoEm(LocalDateTime.now());
            return usuarioRepository.save(novo);
        });

        // ✅ Gerar JWT com base no e-mail
        String jwt = jwtUtil.generateToken(usuario.getEmail());

        return ResponseEntity.ok(Map.of(
            "mensagem", "Usuário autenticado com GitHub!",
            "usuario", new UsuarioResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTipoUsuario().name(),
                jwt // ✅ JWT real adicionado aqui
            )
        ));
    }

    @GetMapping("/github/login")
    public RedirectView login() {
        String githubAuthUrl = "https://github.com/login/oauth/authorize" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&scope=read:user%20user:email%20repo";

        return new RedirectView(githubAuthUrl);
    }
}
