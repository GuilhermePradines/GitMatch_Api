package br.com.gitmatch.gitmatch.service.vaga;

import br.com.gitmatch.gitmatch.dto.vaga.*;
import br.com.gitmatch.gitmatch.model.usuario.Usuario;
import br.com.gitmatch.gitmatch.model.vaga.*;
import br.com.gitmatch.gitmatch.repository.usuario.UsuarioRepository;
import br.com.gitmatch.gitmatch.repository.vaga.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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

        // Pega ou cria tecnologias
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
        dto.setDataCriacao(vaga.getDataCriacao());
        dto.setAtivo(vaga.isAtivo());
        dto.setIdEmpresa(vaga.getEmpresa().getIdUsuario());
        dto.setTecnologias(vaga.getTecnologias().stream().map(Tecnologia::getNome).collect(Collectors.toSet()));
        return dto;
    }

    public List<VagaDetalhesDTO> listarVagasEmpresa(Long idEmpresa) {
        Usuario empresa = usuarioRepo.findById(idEmpresa)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        List<Vaga> vagas = vagaRepo.findByEmpresa(empresa);
        return vagas.stream().map(this::converterParaDTO).collect(Collectors.toList());
    }

    @Transactional
    public CandidaturaDetalhesDTO candidatar(Long idUsuario, Long idVaga, Double percentualCompatibilidade) {
        Usuario candidato = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Candidato não encontrado"));

        Vaga vaga = vagaRepo.findById(idVaga)
                .orElseThrow(() -> new RuntimeException("Vaga não encontrada"));

        // Verifica se já existe candidatura
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
            dto.setIdUsuario(c.getCandidato().getIdUsuario());
            dto.setIdVaga(c.getVaga().getIdVaga());
            dto.setPercentualCompatibilidade(c.getPercentualCompatibilidade());
            dto.setDataCandidatura(c.getDataCandidatura());
            return dto;
        }).collect(Collectors.toList());
    }

    public Long getUsuarioIdByEmail(String email) {
    return usuarioRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"))
            .getIdUsuario();
}
    

}
