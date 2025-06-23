package br.com.gitmatch.gitmatch.dto.vaga;

import lombok.Data;
import java.util.Set;

@Data
public class VagaDTO {
    private String titulo;
    private String descricao;
    private String areaAtuacao;
    private Set<String> tecnologias;  
}
