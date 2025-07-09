package br.com.gitmatch.gitmatch.dto.vaga;

import br.com.gitmatch.gitmatch.dto.usuario.UsuarioResumoDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
public class CandidaturaDetalhesDTO {
    private Long idCandidatura;
    private Long idUsuario;
    private Long idVaga;
    private Double percentualCompatibilidade;
    private LocalDateTime dataCandidatura;
    private UsuarioResumoDTO candidato;
    private List<LinguagemTecnologiaDTO> linguagens;
    private List<FrameworkProjetoDTO> frameworks;
}
