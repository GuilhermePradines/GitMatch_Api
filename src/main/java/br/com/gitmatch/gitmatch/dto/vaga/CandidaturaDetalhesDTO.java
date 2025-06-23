package br.com.gitmatch.gitmatch.dto.vaga;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CandidaturaDetalhesDTO {
    private Long idCandidatura;
    private Long idUsuario;
    private Long idVaga;
    private Double percentualCompatibilidade;
    private LocalDateTime dataCandidatura;
}
