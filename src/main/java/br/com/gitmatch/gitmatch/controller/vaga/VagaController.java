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

import java.security.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import java.time.LocalDateTime;
import java.time.ZoneId;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


@RestController
@RequestMapping("/vaga")
@CrossOrigin(origins = "*")
public class VagaController {
    @PersistenceContext
private EntityManager entityManager;

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


    @GetMapping("/empresa/candidatosVaga/{idVaga}")
    
//     public ResponseEntity<?> listarCandidaturasDetalhadas(@PathVariable Long idVaga) {
//     List<CandidaturaDetalhesDTO> lista = vagaService.listarCandidaturasPorVaga(idVaga);
//     Vaga vaga = vagaRepo.findById(idVaga).orElseThrow();
    
//     Map<String, Object> resposta = new HashMap<>();
//     resposta.put("tituloVaga", vaga.getTitulo());
//     resposta.put("empresa", vaga.getEmpresa().getNome());
//     resposta.put("candidatos", lista);

//     return ResponseEntity.ok(resposta);
// }

// public ResponseEntity<?> listarCandidatosDetalhadosRaw(@PathVariable Long idVaga) {
    // Busca candidatos e enumera pelos KB do githun
//     Vaga vaga = vagaRepo.findById(idVaga)
//             .orElseThrow(() -> new RuntimeException("Vaga não encontrada"));

//     String sql = """
//         SELECT
//             u.nome,
//             u.profissao,
//             u.github_username,
//             c.percentual_compatibilidade,
//             c.data_candidatura
//         FROM candidaturas c
//         JOIN usuarios u ON c.id_usuario = u.id_usuario
//         WHERE c.id_vaga = :idVaga
//     """;

//     List<Object[]> resultados = entityManager
//             .createNativeQuery(sql)
//             .setParameter("idVaga", idVaga)
//             .getResultList();

//     List<Map<String, Object>> candidatos = new ArrayList<>();

//     for (Object[] row : resultados) {
//         String nome = (String) row[0];
//         String profissao = (String) row[1];
//         String github = (String) row[2];
//         Double compatibilidade = row[3] != null ? ((Number) row[3]).doubleValue() : null;

//         List<List<Object>> linguagens = new ArrayList<>();
//         long totalBytes = 0;
//         int totalFrameworks = 0;

//         try {
//             linguagens = GitHubLangStats.buscarLinguagensEFrameworksDetalhados(github);

//             totalBytes = linguagens.stream()
//                     .filter(t -> t.get(1) instanceof Number)
//                     .mapToLong(t -> ((Number) t.get(1)).longValue())
//                     .sum();

//             totalFrameworks = (int) linguagens.stream()
//                     .filter(t -> t.get(1) instanceof Integer)
//                     .mapToInt(t -> (Integer) t.get(1)).sum();

//         } catch (Exception e) {
//             System.err.println("Erro GitHub [" + github + "]: " + e.getMessage());
//         }

//         Map<String, Object> candidato = new HashMap<>();
//         candidato.put("nome", nome);
//         candidato.put("profissao", profissao);
//         candidato.put("github", github);
//         candidato.put("compatibilidade", compatibilidade);
//         candidato.put("linguagens", linguagens);
//         candidato.put("totalBytes", totalBytes);
//         candidato.put("totalFrameworks", totalFrameworks);

//         candidatos.add(candidato);
//     }

//     candidatos.sort(Comparator
//             .comparing((Map<String, Object> c) -> (Double) c.get("compatibilidade"), Comparator.reverseOrder())
//             .thenComparing(c -> (Long) c.get("totalBytes"), Comparator.reverseOrder())
//             .thenComparing(c -> (Integer) c.get("totalFrameworks"), Comparator.reverseOrder())
//     );

//     Map<String, Object> resposta = new HashMap<>();
//     resposta.put("vaga", vaga.getTitulo());
//     resposta.put("empresa", vaga.getEmpresa().getNome());
//     resposta.put("candidatos", candidatos);

//     return ResponseEntity.ok(resposta);
// }
@SuppressWarnings("unchecked")// suprimi pq eu sei do promebla com a entity manager   
    public ResponseEntity<?> listarCandidatosDetalhadosRaw(@PathVariable Long idVaga) {
    Vaga vaga = vagaRepo.findById(idVaga)
            .orElseThrow(() -> new RuntimeException("Vaga não encontrada"));


    Set<String> linguagensVaga = vaga.getTecnologias().stream()
            .map(t -> t.getNome().toLowerCase())
            .collect(Collectors.toSet());

    String sql = """
        SELECT
            u.nome,
            u.profissao,
            u.github_username,
            c.percentual_compatibilidade,
            c.data_candidatura
        FROM candidaturas c
        JOIN usuarios u ON c.id_usuario = u.id_usuario
        WHERE c.id_vaga = :idVaga
    """;

    List<Object[]> resultados = entityManager
            .createNativeQuery(sql)
            .setParameter("idVaga", idVaga)
            .getResultList();

    List<Map<String, Object>> candidatos = new ArrayList<>();

    for (Object[] row : resultados) {
        String nome = (String) row[0];
        String profissao = (String) row[1];
        String github = (String) row[2];
        Double compatibilidade = row[3] != null ? ((Number) row[3]).doubleValue() : null;

        List<List<Object>> linguagens = new ArrayList<>();
        long totalBytesMatch = 0;
        int totalFrameworks = 0;

        try {
            linguagens = GitHubLangStats.buscarLinguagensEFrameworksDetalhados(github);

            totalBytesMatch = linguagens.stream()
                    .filter(t -> t.get(1) instanceof Number)
                    .filter(t -> linguagensVaga.contains(((String) t.get(0)).toLowerCase()))
                    .mapToLong(t -> ((Number) t.get(1)).longValue())
                    .sum();

            totalFrameworks = (int) linguagens.stream()
                    .filter(t -> t.get(1) instanceof Integer)
                    .mapToInt(t -> (Integer) t.get(1)).sum();

        } catch (Exception e) {
            System.err.println("Erro GitHub [" + github + "]: " + e.getMessage());
        }

        Map<String, Object> candidato = new HashMap<>();
        candidato.put("nome", nome);
        candidato.put("profissao", profissao);
        candidato.put("github", github);
        candidato.put("compatibilidade", compatibilidade);
        candidato.put("linguagens", linguagens);
        candidato.put("totalBytesMatch", totalBytesMatch);
        candidato.put("totalFrameworks", totalFrameworks);

        candidatos.add(candidato);
    }

    candidatos.sort(Comparator
            .comparing((Map<String, Object> c) -> (Double) c.get("compatibilidade"), Comparator.reverseOrder())
            .thenComparing(c -> (Long) c.get("totalBytesMatch"), Comparator.reverseOrder())
            .thenComparing(c -> (Integer) c.get("totalFrameworks"), Comparator.reverseOrder())
    );

    Map<String, Object> resposta = new HashMap<>();
    resposta.put("vaga", vaga.getTitulo());
    resposta.put("empresa", vaga.getEmpresa().getNome());
    resposta.put("candidatos", candidatos);

    return ResponseEntity.ok(resposta);
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


        @GetMapping("/usuario/tecnologias/porcentagem")
    public ResponseEntity<?> listarTecnologiasPorsentagen(@AuthenticationPrincipal Usuario usuario) {
        try {
            String githubUsername = usuario.getGithubUsername();
            if (githubUsername == null || githubUsername.isEmpty()) {
                return ResponseEntity.badRequest().body("Usuário não possui GitHub cadastrado.");
            }

            List<String> linguagens = GitHubLangStats.buscarTecnologiasComPorcentagem(githubUsername);
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
            linguagensGitHub = GitHubLangStats.buscarLinguagensPorUsername(usuario.getGithubUsername());
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
        linguagensGitHub = GitHubLangStats.buscarLinguagensPorUsername(usuario.getGithubUsername());
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
