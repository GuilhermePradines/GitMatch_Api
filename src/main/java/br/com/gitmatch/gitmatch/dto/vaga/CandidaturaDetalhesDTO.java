package br.com.gitmatch.gitmatch.dto.vaga;

import br.com.gitmatch.gitmatch.dto.usuario.UsuarioResumoDTO;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CandidaturaDetalhesDTO {
    private Long idCandidatura;
    private Long idUsuario;
    private Long idVaga;
    private Double percentualCompatibilidade;
    private LocalDateTime dataCandidatura;
    private UsuarioResumoDTO candidato;
}
