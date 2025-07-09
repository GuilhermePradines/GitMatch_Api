package br.com.gitmatch.gitmatch.dto.vaga;

import lombok.*;
import java.util.List;
import br.com.gitmatch.gitmatch.dto.vaga.CandidatoDetalhadoDTO;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VagaComCandidatosDTO {
    private String tituloVaga;
    private String nomeEmpresa;
    private List<CandidatoDetalhadoDTO> candidatos;
}
