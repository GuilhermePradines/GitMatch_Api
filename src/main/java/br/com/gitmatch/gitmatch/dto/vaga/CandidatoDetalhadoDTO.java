package br.com.gitmatch.gitmatch.dto.vaga;

import lombok.*;
import java.util.List;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandidatoDetalhadoDTO {
    private String nomeCandidato;
    private String profissao;
    private double percentualCompatibilidade;
    private LocalDateTime dataCandidatura;
    private List<List<Object>> linguagensEFrameworks;
    private String githubUsername;
}