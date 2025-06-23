package br.com.gitmatch.gitmatch.controller.usuario;

import br.com.gitmatch.gitmatch.dto.usuario.*;
import br.com.gitmatch.gitmatch.enums.TipoUsuario;
import br.com.gitmatch.gitmatch.model.usuario.Usuario;
import br.com.gitmatch.gitmatch.repository.usuario.UsuarioRepository;
import br.com.gitmatch.gitmatch.security.JwtUtil;
import br.com.gitmatch.gitmatch.service.usuario.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/usuario")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public UsuarioResponseDTO cadastrar(@RequestBody UsuarioDTO dto) {
        return usuarioService.cadastrar(dto);
    }

    @PostMapping("/login")
    public UsuarioResponseDTO login(@RequestBody LoginDTO dto) throws Exception {
        return usuarioService.login(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarPerfil(@PathVariable Long id,
                                             @RequestBody UsuarioUpdateDTO dto,
                                             HttpServletRequest request) {
        String emailToken = jwtUtil.extractUsername(request.getHeader("Authorization").substring(7));
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!usuario.getEmail().equals(emailToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado.");
        }

        if (dto.getNome() != null) usuario.setNome(dto.getNome());
        if (dto.getEmail() != null) usuario.setEmail(dto.getEmail());
        if (dto.getProfissao() != null) usuario.setProfissao(dto.getProfissao());
        if (dto.getBio() != null) usuario.setBio(dto.getBio());
        if (dto.getFotoPerfil() != null) usuario.setFotoPerfil(dto.getFotoPerfil());

        usuarioRepository.save(usuario);
        return ResponseEntity.ok("Perfil atualizado com sucesso.");
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioDetalhesDTO> getUsuarioLogado(Authentication auth) {
        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        UsuarioDetalhesDTO dto = new UsuarioDetalhesDTO(
                usuario.getIdUsuario(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTipoUsuario(),
                usuario.getGithubUsername(),
                usuario.getProfissao(),
                usuario.getBio(),
                usuario.getFotoPerfil(),
                usuario.getCnpj()
        );

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioDetalhesDTO> getUsuarioPorId(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        UsuarioDetalhesDTO dto = new UsuarioDetalhesDTO(
                usuario.getIdUsuario(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTipoUsuario(),
                usuario.getGithubUsername(),
                usuario.getProfissao(),
                usuario.getBio(),
                usuario.getFotoPerfil(),
                usuario.getCnpj()
        );

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/candidatos")
    public ResponseEntity<List<UsuarioDetalhesDTO>> listarCandidatos() {
        List<Usuario> candidatos = usuarioRepository.findByTipoUsuario(TipoUsuario.CANDIDATO);
        List<UsuarioDetalhesDTO> resposta = candidatos.stream().map(u -> new UsuarioDetalhesDTO(
                u.getIdUsuario(),
                u.getNome(),
                u.getEmail(),
                u.getTipoUsuario(),
                u.getGithubUsername(),
                u.getProfissao(),
                u.getBio(),
                u.getFotoPerfil(),
                null
        )).toList();

        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/empresas")
    public ResponseEntity<List<UsuarioDetalhesDTO>> listarEmpresas() {
        List<Usuario> empresas = usuarioRepository.findByTipoUsuario(TipoUsuario.EMPRESA);
        List<UsuarioDetalhesDTO> resposta = empresas.stream().map(u -> new UsuarioDetalhesDTO(
                u.getIdUsuario(),
                u.getNome(),
                u.getEmail(),
                u.getTipoUsuario(),
                null,
                u.getProfissao(),
                u.getBio(),
                u.getFotoPerfil(),
                u.getCnpj()
        )).toList();

        return ResponseEntity.ok(resposta);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id, Authentication authentication) {
        String emailLogado = authentication.getName();

        Usuario usuarioParaDeletar = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        Usuario usuarioLogado = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário logado não encontrado"));

        if (!usuarioLogado.getTipoUsuario().equals(TipoUsuario.ADMIN) &&
                !usuarioLogado.getIdUsuario().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para deletar este usuário");
        }

        usuarioRepository.delete(usuarioParaDeletar);

        return ResponseEntity.noContent().build();
    }
}
