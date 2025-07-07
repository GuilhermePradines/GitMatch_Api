package br.com.gitmatch.gitmatch.service.vaga;

import br.com.gitmatch.gitmatch.dto.vaga.*;
import br.com.gitmatch.gitmatch.model.usuario.Usuario;
import br.com.gitmatch.gitmatch.model.vaga.*;
import br.com.gitmatch.gitmatch.repository.usuario.UsuarioRepository;
import br.com.gitmatch.gitmatch.repository.vaga.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.core.type.TypeReference;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;
import br.com.gitmatch.gitmatch.dto.usuario.UsuarioResumoDTO;
@Service
public class VagaService {

    @Autowired
    private VagaRepository vagaRepo;

    @Autowired
    private TecnologiaRepository tecnologiaRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private CandidaturaRepository candidaturaRepo;

    

    @Transactional
    public VagaDetalhesDTO criarVaga(Long idEmpresa, VagaDTO dto) {
        Usuario empresa = usuarioRepo.findById(idEmpresa)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        if (!empresa.getTipoUsuario().name().equals("EMPRESA")) {
            throw new RuntimeException("Usuário não é uma empresa");
        }

        Vaga vaga = new Vaga();
        vaga.setEmpresa(empresa);
        vaga.setTitulo(dto.getTitulo());
        vaga.setDescricao(dto.getDescricao());
        vaga.setAreaAtuacao(dto.getAreaAtuacao());
        vaga.setLocalizacao(dto.getLocalizacao()); 
        vaga.setTurno(dto.getTurno());             

       
        Set<Tecnologia> tecnologias = dto.getTecnologias().stream()
                .map(nome -> tecnologiaRepo.findByNome(nome)
                        .orElseGet(() -> {
                            Tecnologia novaTec = new Tecnologia();
                            novaTec.setNome(nome);
                            return tecnologiaRepo.save(novaTec);
                        }))
                .collect(Collectors.toSet());

        vaga.setTecnologias(tecnologias);

        vaga = vagaRepo.save(vaga);

        return converterParaDTO(vaga);
    }

    public VagaDetalhesDTO converterParaDTO(Vaga vaga) {
        VagaDetalhesDTO dto = new VagaDetalhesDTO();
        dto.setIdVaga(vaga.getIdVaga());
        dto.setTitulo(vaga.getTitulo());
        dto.setDescricao(vaga.getDescricao());
        dto.setAreaAtuacao(vaga.getAreaAtuacao());
        dto.setLocalizacao(vaga.getLocalizacao()); 
        dto.setTurno(vaga.getTurno());             
        dto.setDataCriacao(vaga.getDataCriacao());
        dto.setAtivo(vaga.isAtivo());
        dto.setIdEmpresa(vaga.getEmpresa().getIdUsuario());
        dto.setTecnologias(vaga.getTecnologias().stream().map(Tecnologia::getNome).collect(Collectors.toSet()));
        return dto;
    }

    @Transactional
    public VagaDetalhesDTO editarVaga(Long idVaga, Long idEmpresa, VagaDTO dto) {
        Vaga vaga = vagaRepo.findById(idVaga)
                .orElseThrow(() -> new RuntimeException("Vaga não encontrada"));

        if (!vaga.getEmpresa().getIdUsuario().equals(idEmpresa)) {
            throw new RuntimeException("Você não tem permissão para editar esta vaga");
        }

        vaga.setTitulo(dto.getTitulo());
        vaga.setDescricao(dto.getDescricao());
        vaga.setAreaAtuacao(dto.getAreaAtuacao());
        vaga.setLocalizacao(dto.getLocalizacao());
        vaga.setTurno(dto.getTurno());

        Set<Tecnologia> tecnologias = dto.getTecnologias().stream()
                .map(nome -> tecnologiaRepo.findByNome(nome)
                        .orElseGet(() -> {
                            Tecnologia novaTec = new Tecnologia();
                            novaTec.setNome(nome);
                            return tecnologiaRepo.save(novaTec);
                        }))
                .collect(Collectors.toSet());

        vaga.setTecnologias(tecnologias);

        vaga = vagaRepo.save(vaga);

        return converterParaDTO(vaga);
    }

    @Transactional
    public void desativarVaga(Long idVaga, Long idEmpresa) {
        Vaga vaga = vagaRepo.findById(idVaga)
                .orElseThrow(() -> new RuntimeException("Vaga não encontrada"));

        if (!vaga.getEmpresa().getIdUsuario().equals(idEmpresa)) {
            throw new RuntimeException("Você não tem permissão para deletar esta vaga");
        }

        vaga.setAtivo(false);
        vagaRepo.save(vaga);
    }


    public List<VagaDetalhesDTO> listarTodasVagas() {
    List<Vaga> vagas = vagaRepo.findAll();
   
    return vagas.stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
}

    public List<VagaDetalhesDTO> listarTodasVagasAtivas() {
        List<Vaga> vagas = vagaRepo.findByAtivoTrue();
        return vagas.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    
    

    public List<VagaDetalhesDTO> listarVagasEmpresa(Long idEmpresa) {
    usuarioRepo.findById(idEmpresa)
            .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
    
    List<Vaga> vagas = vagaRepo.findByEmpresaWithTecnologias(idEmpresa);
    
    return vagas.stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
}


    @Transactional
    public CandidaturaDetalhesDTO candidatar(Long idUsuario, Long idVaga, Double percentualCompatibilidade) {
        Usuario candidato = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Candidato não encontrado"));

        Vaga vaga = vagaRepo.findById(idVaga)
                .orElseThrow(() -> new RuntimeException("Vaga não encontrada"));

       
        candidaturaRepo.findByCandidatoAndVaga(candidato, vaga)
                .ifPresent(c -> {
                    throw new RuntimeException("Candidato já se inscreveu nessa vaga");
                });

        Candidatura candidatura = new Candidatura();
        candidatura.setCandidato(candidato);
        candidatura.setVaga(vaga);
        candidatura.setPercentualCompatibilidade(percentualCompatibilidade);
        candidatura.setDataCandidatura(java.time.LocalDateTime.now());

        candidatura = candidaturaRepo.save(candidatura);

        CandidaturaDetalhesDTO dto = new CandidaturaDetalhesDTO();
        dto.setIdCandidatura(candidatura.getIdCandidatura());
        dto.setIdUsuario(candidato.getIdUsuario());
        dto.setIdVaga(vaga.getIdVaga());
        dto.setPercentualCompatibilidade(percentualCompatibilidade);
        dto.setDataCandidatura(candidatura.getDataCandidatura());

        return dto;
    }

    public List<CandidaturaDetalhesDTO> listarCandidaturasPorVaga(Long idVaga) {
        Vaga vaga = vagaRepo.findById(idVaga)
                .orElseThrow(() -> new RuntimeException("Vaga não encontrada"));

        List<Candidatura> candidaturas = candidaturaRepo.findByVaga(vaga);

        return candidaturas.stream().map(c -> {
            CandidaturaDetalhesDTO dto = new CandidaturaDetalhesDTO();
            dto.setIdCandidatura(c.getIdCandidatura());
            dto.setIdVaga(c.getVaga().getIdVaga());
            dto.setPercentualCompatibilidade(c.getPercentualCompatibilidade());
            dto.setDataCandidatura(c.getDataCandidatura());

            UsuarioResumoDTO usuarioDTO = new UsuarioResumoDTO(
                    c.getCandidato().getNome(),
                    c.getCandidato().getProfissao(),
                    c.getCandidato().getFotoPerfil());

            dto.setCandidato(usuarioDTO);

            return dto;
        }).collect(Collectors.toList());
    }


    public Long getUsuarioIdByEmail(String email) {
        return usuarioRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"))
                .getIdUsuario();
    }



public List<VagaTecnologiaDTO> buscarVagasPorTecnologias(List<String> tecnologiasBuscadas) {

String[] tecArray = tecnologiasBuscadas.toArray(new String[0]);
List<Vaga> vagas = vagaRepo.findVagasAtivasPorTecnologias(tecArray);
    for (Vaga v : vagas) {
        System.out.println("Empresa: " + v.getEmpresa().getNome());
    }

    return vagas.stream()
            .map(v -> new VagaTecnologiaDTO(
                    v.getTitulo(),
                    v.getEmpresa().getNome(),
                    v.getTecnologias()
                        .stream()
                        .map(t -> t.getNome())
                        .collect(Collectors.toList())
            ))
            .collect(Collectors.toList());
}






private static HttpRequest.Builder requestBuilderWithAuth(URI uri, String token) {
    return HttpRequest.newBuilder(uri)
            .header("Authorization", "token " + token)
            .header("Accept", "application/vnd.github.v3+json");
}

public static List<String> getLanguageNames(String username) throws Exception {
    String token = ""; // idealmente use @Value ou variável de ambiente
    HttpClient client = HttpClient.newHttpClient();
    ObjectMapper mapper = new ObjectMapper();

    HttpRequest requestRepos = requestBuilderWithAuth(
        URI.create("https://api.github.com/users/" + username + "/repos"),
        token
    ).build();

    HttpResponse<String> responseRepos = client.send(requestRepos, HttpResponse.BodyHandlers.ofString());
    List<Map<String, Object>> repos = mapper.readValue(responseRepos.body(), new TypeReference<>() {});
    Map<String, Long> totalLangs = new HashMap<>();

    for (Map<String, Object> repo : repos) {
        String languagesUrl = (String) repo.get("languages_url");
        HttpRequest requestLang = requestBuilderWithAuth(URI.create(languagesUrl), token).build();
        HttpResponse<String> responseLang = client.send(requestLang, HttpResponse.BodyHandlers.ofString());

        if (responseLang.body().contains("\"message\"")) continue;

        Map<String, Long> langs = mapper.readValue(responseLang.body(), new TypeReference<>() {});
        langs.forEach((lang, bytes) -> totalLangs.merge(lang, bytes, Long::sum));
    }

    return new ArrayList<>(totalLangs.keySet());
}

public int calcularCompatibilidade(List<String> tecnologiasVaga, List<String> tecnologiasCandidato) {
    if (tecnologiasVaga == null || tecnologiasVaga.isEmpty()) return 0;

    // Transforma tudo em minúsculo e remove espaços
    Set<String> normalizadasVaga = tecnologiasVaga.stream()
        .map(t -> t.toLowerCase().trim())
        .collect(Collectors.toSet());

    Set<String> normalizadasCandidato = tecnologiasCandidato.stream()
        .map(t -> t.toLowerCase().trim())
        .collect(Collectors.toSet());

    long emComum = normalizadasVaga.stream()
        .filter(normalizadasCandidato::contains)
        .count();

    return (int) ((emComum * 100.0) / normalizadasVaga.size());
}



}
