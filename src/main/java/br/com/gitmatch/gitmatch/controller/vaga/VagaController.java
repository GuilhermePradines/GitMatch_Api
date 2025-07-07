package br.com.gitmatch.gitmatch.controller.vaga;

import br.com.gitmatch.gitmatch.dto.vaga.*;
import br.com.gitmatch.gitmatch.model.usuario.Usuario;
import br.com.gitmatch.gitmatch.model.vaga.Tecnologia;
import br.com.gitmatch.gitmatch.model.vaga.Vaga;
import br.com.gitmatch.gitmatch.repository.usuario.UsuarioRepository;
import br.com.gitmatch.gitmatch.repository.vaga.VagaRepository;
import br.com.gitmatch.gitmatch.service.GitHubLangStats;
import br.com.gitmatch.gitmatch.service.vaga.VagaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/vaga")
@CrossOrigin(origins = "*")
public class VagaController {

    @Autowired
    private VagaService vagaService;

    @Autowired
    private VagaRepository vagaRepo;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/criar")
    public ResponseEntity<VagaDetalhesDTO> criarVaga(@RequestBody VagaDTO dto,
                                                     @AuthenticationPrincipal Usuario usuario) {
        VagaDetalhesDTO vagaCriada = vagaService.criarVaga(usuario.getIdUsuario(), dto);
        return ResponseEntity.ok(vagaCriada);
    }

    @GetMapping("/empresa")
    public ResponseEntity<List<VagaDetalhesDTO>> listarVagasEmpresa(@AuthenticationPrincipal Usuario usuario) {
        List<VagaDetalhesDTO> vagas = vagaService.listarVagasEmpresa(usuario.getIdUsuario());
        return ResponseEntity.ok(vagas);
    }

    @GetMapping("/todas")
    public ResponseEntity<List<VagaDetalhesDTO>> listarTodasVagas() {
        return ResponseEntity.ok(vagaService.listarTodasVagas());
    }

    @GetMapping("/ativas")
    public ResponseEntity<List<VagaDetalhesDTO>> listarTodasVagasAtivas() {
        return ResponseEntity.ok(vagaService.listarTodasVagasAtivas());
    }

    

    @PutMapping("/editar/{id}")
    public ResponseEntity<VagaDetalhesDTO> editarVaga(@PathVariable Long id,
                                                      @RequestBody VagaDTO dto,
                                                      @AuthenticationPrincipal Usuario usuario) {
        VagaDetalhesDTO vagaAtualizada = vagaService.editarVaga(id, usuario.getIdUsuario(), dto);
        return ResponseEntity.ok(vagaAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarVaga(@PathVariable Long id,
                                            @AuthenticationPrincipal Usuario usuario) {
        vagaService.desativarVaga(id, usuario.getIdUsuario());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/candidaturas/{idVaga}")
    public ResponseEntity<List<CandidaturaDetalhesDTO>> listarCandidaturasPorVaga(@PathVariable Long idVaga,
                                                                                   @AuthenticationPrincipal Usuario usuario) {
        List<CandidaturaDetalhesDTO> candidaturas = vagaService.listarCandidaturasPorVaga(idVaga);
        return ResponseEntity.ok(candidaturas);
    }

    @GetMapping("/tecnologias/{id}")
    public ResponseEntity<Set<String>> listarTecnologiasDaVaga(@PathVariable Long id) {
        Vaga vaga = vagaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Vaga não encontrada"));

        Set<String> nomesTecnologias = vaga.getTecnologias().stream()
                .map(Tecnologia::getNome)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(nomesTecnologias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VagaDetalhesDTO> buscarVagaPorId(@PathVariable Long id) {
        Vaga vaga = vagaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Vaga não encontrada"));

        return ResponseEntity.ok(vagaService.converterParaDTO(vaga));
    }

    @PostMapping("/buscar")
    public ResponseEntity<List<VagaTecnologiaDTO>> buscarVagasPorTecnologias(@RequestBody List<String> tecnologias) {
        return ResponseEntity.ok(vagaService.buscarVagasPorTecnologias(tecnologias));
    }

    @GetMapping("/usuario/tecnologias")
    public ResponseEntity<?> listarTecnologias(@AuthenticationPrincipal Usuario usuario) {
        try {
            String githubUsername = usuario.getGithubUsername();
            if (githubUsername == null || githubUsername.isEmpty()) {
                return ResponseEntity.badRequest().body("Usuário não possui GitHub cadastrado.");
            }

            List<String> linguagens = GitHubLangStats.buscarLinguagensPorUsername(githubUsername);
            return ResponseEntity.ok(linguagens);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro ao buscar linguagens: " + e.getMessage());
        }
    }

    @GetMapping("/{vagaId}/compatibilidade/{usuarioId}")
    public ResponseEntity<Map<String, Object>> verificarCompatibilidade(@PathVariable Long vagaId,
                                                                         @PathVariable Long usuarioId) {
        Vaga vaga = vagaRepo.findById(vagaId)
                .orElseThrow(() -> new RuntimeException("Vaga não encontrada"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<String> linguagensGitHub;
        try {
            linguagensGitHub = VagaService.getLanguageNames(usuario.getGithubUsername());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("erro", "Não foi possível acessar o GitHub: " + e.getMessage()));
        }

        List<String> linguagensVaga = vaga.getTecnologias().stream()
                .map(Tecnologia::getNome)
                .collect(Collectors.toList());

        int compatibilidade = vagaService.calcularCompatibilidade(linguagensVaga, linguagensGitHub);

        return ResponseEntity.ok(Map.of(
                "vaga", vaga.getTitulo(),
                "empresa", vaga.getEmpresa().getNome(),
                "tecnologiasVaga", linguagensVaga,
                "tecnologiasUsuario", linguagensGitHub,
                "compatibilidade", compatibilidade
        ));
    }


@PostMapping("/candidatar/{idVaga}")
public ResponseEntity<CandidaturaDetalhesDTO> candidatarSe(
        @PathVariable Long idVaga,
        @AuthenticationPrincipal Usuario usuario
) {
    Vaga vaga = vagaRepo.findById(idVaga)
            .orElseThrow(() -> new RuntimeException("Vaga não encontrada"));

    List<String> linguagensGitHub;
    try {
        linguagensGitHub = VagaService.getLanguageNames(usuario.getGithubUsername());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
    }

    List<String> linguagensVaga = vaga.getTecnologias().stream()
            .map(Tecnologia::getNome)
            .collect(Collectors.toList());

    int compatibilidade = vagaService.calcularCompatibilidade(linguagensVaga, linguagensGitHub);

    CandidaturaDetalhesDTO dto = vagaService.candidatar(usuario.getIdUsuario(), idVaga, (double) compatibilidade);

    return ResponseEntity.ok(dto);
}


}
