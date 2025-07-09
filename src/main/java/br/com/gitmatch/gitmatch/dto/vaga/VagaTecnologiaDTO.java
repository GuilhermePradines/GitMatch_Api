package br.com.gitmatch.gitmatch.dto.vaga;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VagaTecnologiaDTO {
    private Long idVaga;
    private String tituloVaga;
    private String nomeEmpresa;
    private List<String> tecnologias;
}