package br.com.gitmatch.gitmatch.controller.usuario;

import br.com.gitmatch.gitmatch.dto.usuario.*;
import br.com.gitmatch.gitmatch.enums.TipoUsuario;
import br.com.gitmatch.gitmatch.model.usuario.Usuario;
import br.com.gitmatch.gitmatch.model.vaga.Vaga;
import br.com.gitmatch.gitmatch.repository.usuario.UsuarioRepository;
import br.com.gitmatch.gitmatch.repository.vaga.CandidaturaRepository;
import br.com.gitmatch.gitmatch.repository.vaga.VagaRepository;
import br.com.gitmatch.gitmatch.security.JwtUtil;
import br.com.gitmatch.gitmatch.service.usuario.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

    @Autowired 
    private CandidaturaRepository candidaturaRepository;

    @Autowired
    private VagaRepository vagaRepository;


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
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado" + email));
               

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

    
@Transactional
@DeleteMapping("/delete/{id}")
public ResponseEntity<Void> deletarUsuario(@PathVariable Long id, Authentication authentication) {
    Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
    String emailLogado = usuarioLogado.getEmail();

    Usuario usuarioParaDeletar = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

    Usuario usuarioLogadoBanco = usuarioRepository.findByEmail(emailLogado)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário logado não encontrado"));

    if (!usuarioLogadoBanco.getTipoUsuario().equals(TipoUsuario.ADMIN) &&
            !usuarioLogadoBanco.getIdUsuario().equals(id)) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para deletar este usuário");
    }

    // Se for empresa, deleta as candidaturas de todas as vagas dela
    if (usuarioParaDeletar.getTipoUsuario().equals(TipoUsuario.EMPRESA)) {
        List<Vaga> vagasDaEmpresa = vagaRepository.findAllByEmpresa(usuarioParaDeletar);

        for (Vaga vaga : vagasDaEmpresa) {
            candidaturaRepository.deleteAllByVaga(vaga);
        }

        vagaRepository.deleteAllByEmpresa(usuarioParaDeletar);
    }

    // Se for candidato, deleta as candidaturas dele
    if (usuarioParaDeletar.getTipoUsuario().equals(TipoUsuario.CANDIDATO)) {
        candidaturaRepository.deleteAllByCandidato(usuarioParaDeletar);
    }

    usuarioRepository.delete(usuarioParaDeletar);

    return ResponseEntity.noContent().build();
}





    @PutMapping("/alterar-senha")
    // public ResponseEntity<String> alterarSenha(@RequestBody AlterarSenhaDTO dto, Authentication authentication) {
    //     String email = authentication.getName();
    //     usuarioService.alterarSenha(email, dto.getSenhaAtual(), dto.getNovaSenha());
    //     return ResponseEntity.ok("Senha alterada com sucesso.");
    // }

public ResponseEntity<?> alterarSenha(
        @RequestBody AlterarSenhaDTO dto,
        @AuthenticationPrincipal Usuario usuario) {
    try {
        usuarioService.alterarSenha(usuario.getEmail(), dto.getSenhaAtual(), dto.getNovaSenha());
        return ResponseEntity.ok("Senha alterada com sucesso.");
    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}




    //nao ta usando
    @PostMapping("/recuperar-senha")
    public ResponseEntity<String> enviarCodigo(@RequestBody RecuperarSenhaDTO dto) {
        usuarioService.enviarCodigoRecuperacao(dto.getEmail());
        return ResponseEntity.ok("Código enviado.");
    }

    @PostMapping("/verificar-codigo")
    public ResponseEntity<String> verificarCodigo(@RequestBody VerificarCodigoDTO dto) {
        usuarioService.verificarCodigo(dto.getEmail(), dto.getToken());
        return ResponseEntity.ok("Código válido.");
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<String> redefinirSenha(@RequestBody RedefinirSenhaDTO dto) {
        usuarioService.redefinirSenha(dto.getEmail(), dto.getNovaSenha());
        return ResponseEntity.ok("Senha redefinida com sucesso.");
    }

    

    
    
}
